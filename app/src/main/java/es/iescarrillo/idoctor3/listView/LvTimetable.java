package es.iescarrillo.idoctor3.listView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.activities.MenuActivity;
import es.iescarrillo.idoctor3.activities.ProfessionalFragment;
import es.iescarrillo.idoctor3.activities.TimeTableActivity;
import es.iescarrillo.idoctor3.adapters.AdapterSpinnerConsultation;
import es.iescarrillo.idoctor3.adapters.AdapterTimetables;
import es.iescarrillo.idoctor3.details.DetailsProfessional;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.models.Timetable;
import es.iescarrillo.idoctor3.services.ConsultationService;
import es.iescarrillo.idoctor3.services.TimetableService;

public class LvTimetable extends AppCompatActivity {

    private Button btnAddTimetable, btnBackLVTimetable;
    private ListView lvTimetables;
    private TimetableService timetableService;
    private Spinner spinnerConsultation;
    private AdapterSpinnerConsultation adapterSpinnerConsultation;
    private ConsultationService consultationService;
    private SharedPreferences sharedPreferences;
    private String professionalId;
    private List<Consultation> consultationList;
    private List<Timetable> timetables;
    private AdapterTimetables adapterTimetables;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lv_timetable);

        //Inicializamos el servicio
        timetableService = new TimetableService(getApplicationContext());
        consultationService = new ConsultationService(getApplicationContext());

        //Variables de sesion
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        professionalId = sharedPreferences.getString("id", "");

        //Cargamos componentes
        getComponentFromView();


        // Mostrar el ProgressDialog
        progressDialog = ProgressDialog.show(this, "Charging", "Please wait...", true);

        // Cargar el spinner y los datos desde Firebase en segundo plano
        new LoadDataAsyncTask().execute();


        /**
         * Con esto compruebo que cada vez que cambie el spinner, se carguen los horarios de la nueva selección
         */
        spinnerConsultation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Cuando se selecciona un elemento en el Spinner, actualizar la ListView

                timetables.clear();
                Log.i("timetables array cambio spinner", timetables.toString());
                updateListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No es necesario hacer nada aquí
            }
        });


    }

    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView() {
        btnAddTimetable = findViewById(R.id.btnAddTimetableLV);
        btnBackLVTimetable = findViewById(R.id.btnBackLVTimetable);
        spinnerConsultation = findViewById(R.id.spinnerTimetable);
        lvTimetables = findViewById(R.id.lvTimetables);
    }

    private void setupConsultationSpinner() {
        consultationList = new ArrayList<>();

        consultationService.getConsultationByProfId(professionalId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                consultationList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Consultation consultation = snapshot.getValue(Consultation.class);
                    consultationList.add(consultation);
                }

                adapterSpinnerConsultation = new AdapterSpinnerConsultation(getApplicationContext(), android.R.layout.simple_spinner_item, consultationList);
                adapterSpinnerConsultation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerConsultation.setAdapter(adapterSpinnerConsultation);

                // Cerrar ProgressDialog después de cargar los datos
                progressDialog.dismiss();

                // Continuar con la lógica después de obtener los datos
                loadDataAfterFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en el filtrado de consultas por id.", error.toException());
                // Cerrar ProgressDialog en caso de error
                progressDialog.dismiss();
            }
        });
    }

    private void loadDataAfterFirebase() {
        // Puedes realizar operaciones después de obtener datos de Firebase
        // Por ejemplo, cargar la lista de Timetables
        timetableService.getTimetableByConsultationId(((Consultation) spinnerConsultation.getSelectedItem()).getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timetables.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Timetable t = new Timetable();
                    t.setConsultationId(String.valueOf(snapshot.child("consultationId").getValue()));
                    t.setId(String.valueOf(snapshot.child("id").getValue()));
                    t.setDayOfWeek(String.valueOf(snapshot.child("dayOfWeek").getValue()));

                    LocalTime startTime = convertSnapshotToTime(snapshot, "startTime");
                    LocalTime endTime = convertSnapshotToTime(snapshot, "endTime");

                    t.setStartTime(startTime);
                    t.setEndTime(endTime);

                    timetables.add(t);
                }

                adapterTimetables = new AdapterTimetables(getApplicationContext(), timetables);
                lvTimetables.setAdapter(adapterTimetables);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar la cancelación si es necesario
            }
        });



        lvTimetables.setOnItemClickListener((parent, view, position, id) -> {
            Timetable timetable = (Timetable) parent.getItemAtPosition(position);

            Intent intent = new Intent(LvTimetable.this, TimeTableActivity.class);
            intent.putExtra("timetable", timetable);
            startActivity(intent);
        });

        btnAddTimetable.setOnClickListener(v -> {

            Intent intentMenu = new Intent(LvTimetable.this, TimeTableActivity.class);
            startActivity(intentMenu);
            finish();
        });

        btnBackLVTimetable.setOnClickListener(v -> {
            Intent intentMenu = new Intent(this, MenuActivity.class);
            startActivity(intentMenu);
            finish();
        });
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
           timetables = new ArrayList<>();

            // Simular la obtención de datos de Firebase
            SystemClock.sleep(500); // Simula un trabajo en segundo plano de 3 segundos

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Setup del spinner después de obtener datos de Firebase
            setupConsultationSpinner();


        }
    }

    private LocalTime convertSnapshotToTime(DataSnapshot snapshot, String timeKey) {
        DataSnapshot timeSnapshot = snapshot.child(timeKey);
        Integer hours = Integer.parseInt(timeSnapshot.child("hour").getValue().toString());
        Integer minutes = Integer.parseInt(timeSnapshot.child("minute").getValue().toString());

        // Agregar un "0" delante de la hora si es menor que 10
        String formattedHours = (hours < 10) ? "0" + hours : String.valueOf(hours);
        // Agregar un "0" delante de los minutos si son menores que 10
        String formattedMinutes = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);

        return LocalTime.parse(formattedHours + ":" + formattedMinutes);
    }

    private void updateListView() {
        // Mostrar el ProgressDialog
        progressDialog = ProgressDialog.show(this, "Charging", "Please wait...", true);

        // Obtener el ID de la consulta seleccionada
        String selectedConsultationId = ((Consultation) spinnerConsultation.getSelectedItem()).getId();

        // Cargar los datos de los horarios para la nueva consulta desde Firebase
        timetableService.getTimetableByConsultationId(selectedConsultationId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timetables.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Timetable t = new Timetable();
                    t.setConsultationId(String.valueOf(snapshot.child("consultationId").getValue()));
                    t.setId(String.valueOf(snapshot.child("id").getValue()));
                    t.setDayOfWeek(String.valueOf(snapshot.child("dayOfWeek").getValue()));

                    LocalTime startTime = convertSnapshotToTime(snapshot, "startTime");
                    LocalTime endTime = convertSnapshotToTime(snapshot, "endTime");

                    t.setStartTime(startTime);
                    t.setEndTime(endTime);
                    timetables.add(t);
                }

                Log.i("timetables cambio spinner", timetables.toString());
                // Actualizar el Adapter de la ListView con los nuevos datos
                adapterTimetables.notifyDataSetChanged();

                adapterTimetables = new AdapterTimetables(getApplicationContext(), timetables);
                lvTimetables.setAdapter(adapterTimetables);

                // Cerrar ProgressDialog después de cargar los datos
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar la cancelación si es necesario
                Log.w("Firebase", "Error en la carga de horarios por ID de consulta.", error.toException());
                // Cerrar ProgressDialog en caso de error
                progressDialog.dismiss();
            }
        });
    }




}