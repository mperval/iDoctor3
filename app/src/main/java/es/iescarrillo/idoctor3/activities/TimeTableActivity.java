package es.iescarrillo.idoctor3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.ArrayUtils;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.adapters.AdapterSpinnerConsultation;
import es.iescarrillo.idoctor3.adapters.AdapterSpinnerDayOfWeek;
import es.iescarrillo.idoctor3.adapters.AdapterSpinnerSearch;
import es.iescarrillo.idoctor3.listView.LvConsultations;
import es.iescarrillo.idoctor3.listView.LvTimetable;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.models.Timetable;
import es.iescarrillo.idoctor3.services.ConsultationService;
import es.iescarrillo.idoctor3.services.TimetableService;

public class TimeTableActivity extends AppCompatActivity {

    private Button btnAddTimetable, btnBackTimeTable, btnEndTime, btnStarTime, btnUpdateTimetable,
            btnDeleteTimetable;
    private EditText etStartTime, etEndTime;
    private Spinner spinnerDayOfWeek, spinnerConsultation;
    private AdapterSpinnerDayOfWeek adapterSpinnerDayOfWeek;
    private TimetableService timetableService;
    private int hour, minute;
    private Timetable timetable;
    private SharedPreferences sharedPreferences;
    private ConsultationService consultationService;
    private String professionalId;
    private AdapterSpinnerConsultation adapterSpinnerConsultation;
    private List<Consultation> consultationList;
    private String[] consultationItems;
    private List<Timetable> daysOfWeek;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        //Inicializamos el servicio
        timetableService = new TimetableService(getApplicationContext());
        consultationService = new ConsultationService(getApplicationContext());

        timetable = new Timetable();

        //Variables de sesion
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        professionalId = sharedPreferences.getString("id", "");

        daysOfWeek = new ArrayList<>();
        consultationList = new ArrayList<>();

        //Cargamos componentes
        getComponentFromView();
        btnStarTime.setOnClickListener(this::onClick);
        btnEndTime.setOnClickListener(this::onClick);


        //Cargamos el spinner
        setupDayOfWeekSpinner();
        setupConsultationSpinner();

        //dias de la semana

        // Mostrar el ProgressDialog
        progressDialog = ProgressDialog.show(this, "Charging", "Please wait...", true);

        // Cargar el spinner y los datos desde Firebase en segundo plano
        new LoadDataAsyncTask().execute();

        //Cargamos el intent

        Intent intent = getIntent();
        Timetable receivedTimetable = (Timetable) intent.getSerializableExtra("timetable");

        if (receivedTimetable != null) {

            btnAddTimetable.setEnabled(false);

            Toast.makeText(TimeTableActivity.this, "Disabled Register Button", Toast.LENGTH_LONG).show();

            etStartTime.setText(String.valueOf(receivedTimetable.getStartTime()));
            etEndTime.setText(String.valueOf(receivedTimetable.getEndTime()));


            // Spinner dayOfWeek
            String[] items = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            String dayOfWeek = receivedTimetable.getDayOfWeek();

            int position = ArrayUtils.indexOf(items, dayOfWeek);
            spinnerDayOfWeek.setSelection(position);

            //Spinner Consultation
            consultationService.getConsultationById(receivedTimetable.getConsultationId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        // Obtener la consulta directamente
                        DataSnapshot consultationSnapshot = dataSnapshot.getChildren().iterator().next();

                        // Extraer los datos de la consulta
                        String address = consultationSnapshot.child("address").getValue(String.class);
                        String city = consultationSnapshot.child("city").getValue(String.class);

                        String consultation = address + " (" + city + ")";

                        int position = ArrayUtils.indexOf(consultationItems, consultation);
                        spinnerConsultation.setSelection(position);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Firebase", "Error al buscar la consulta por id.", error.toException());
                }
            });


        } else {

            btnDeleteTimetable.setEnabled(false);
            btnUpdateTimetable.setEnabled(false);

            Toast.makeText(TimeTableActivity.this, "Disabled Delete and Update Button", Toast.LENGTH_LONG).show();
        }


        btnBackTimeTable.setOnClickListener(v -> {
            Intent viewCourseManagerActivity = new Intent(this, LvTimetable.class);
            startActivity(viewCourseManagerActivity);
            finish();
        });


        btnAddTimetable.setOnClickListener(v -> {


            String dayOfWeek = String.valueOf(spinnerDayOfWeek.getSelectedItem());
            Log.i("dayOfWeek marcado en el spinner", dayOfWeek);
            String startTime = etStartTime.getText().toString().trim();

            String endTime = etEndTime.getText().toString().trim();




            if (checkEmpty(startTime, endTime)) {
                Toast.makeText(TimeTableActivity.this, "All fields must be completed", Toast.LENGTH_SHORT).show();


            } else if (LocalTime.parse(startTime).equals(LocalTime.parse(endTime)) || LocalTime.parse(startTime).isAfter(LocalTime.parse(endTime))) {
                Toast.makeText(TimeTableActivity.this, "Invalid Dates", Toast.LENGTH_SHORT).show();

            } else {

                Log.i("days of week size", String.valueOf(daysOfWeek.size()));
                for (Timetable timetable1 : daysOfWeek) {
                    Log.i("timetable day of week in add", timetable1.getDayOfWeek().toString());
                    if (timetable1.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                        Toast.makeText(TimeTableActivity.this, "Day of Week in use", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Si el bucle termina y no se ha ejecutado el return, entonces realizamos la inserción
                Timetable t2 = new Timetable();
                t2.setDayOfWeek(dayOfWeek);
                t2.setStartTime(LocalTime.parse(startTime));
                t2.setEndTime(LocalTime.parse(endTime));
                Consultation cs = (Consultation) spinnerConsultation.getSelectedItem();
                t2.setConsultationId(cs.getId());

                timetableService.insertTimetable(t2);

                Log.i("timetable", "horario añadido");

                Toast.makeText(TimeTableActivity.this, "Timetable registered", Toast.LENGTH_LONG).show();

                Intent intentConsultations = new Intent(this, LvTimetable.class);
                startActivity(intentConsultations);
                finish();
            }
        });

        btnUpdateTimetable.setOnClickListener(v -> {

            String dayOfWeek = String.valueOf(spinnerDayOfWeek.getSelectedItem());
            String startTime = etStartTime.getText().toString().trim();
            Log.i("starTime", startTime);
            String endTime = etEndTime.getText().toString().trim();
            Log.i("endTime", endTime);


            if (checkEmpty(startTime, endTime)) {
                Toast.makeText(TimeTableActivity.this, "All fields must be completed", Toast.LENGTH_SHORT).show();

            } else if (LocalTime.parse(startTime).equals(LocalTime.parse(endTime)) || LocalTime.parse(startTime).isAfter(LocalTime.parse(endTime))) {
                Toast.makeText(TimeTableActivity.this, "Invalid Dates", Toast.LENGTH_SHORT).show();

            } else {


                /**
                 *No podrá cambiar consultationId ni el dayOfWeek
                 */
                timetable.setDayOfWeek(dayOfWeek);
                timetable.setStartTime(LocalTime.parse(startTime));
                timetable.setEndTime(LocalTime.parse(endTime));
                timetable.setConsultationId(receivedTimetable.getConsultationId());
                timetable.setId(receivedTimetable.getId());

                timetableService.updateTimetable(timetable);

                Log.i("timetable", "horario añadido");

                Toast.makeText(TimeTableActivity.this, "Timetable updated", Toast.LENGTH_LONG).show();

                Intent intentConsultations = new Intent(this, LvTimetable.class);
                startActivity(intentConsultations);
                finish();


            }


        });

        btnDeleteTimetable.setOnClickListener(v -> {


            timetableService.deleteTimetable(receivedTimetable.getId());

            Toast.makeText(this, "Consultation Deleted", Toast.LENGTH_SHORT).show();

            Intent intentConsultations = new Intent(this, LvTimetable.class);
            startActivity(intentConsultations);
            finish();

        });


    }

    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView() {
        btnAddTimetable = findViewById(R.id.btnAddTimetable);
        btnBackTimeTable = findViewById(R.id.btnBackTimeTable);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnStarTime = findViewById(R.id.btnStarTime);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        spinnerDayOfWeek = findViewById(R.id.spinnerDayOfWeek);
        spinnerConsultation = findViewById(R.id.spinnerConsultation);
        btnUpdateTimetable = findViewById(R.id.btnUpdateTimetable);
        btnDeleteTimetable = findViewById(R.id.btnDeleteTimetable);
    }

    // Método para configurar el Spinner con las opciones
    private void setupDayOfWeekSpinner() {
        // Crea una instancia de tu AdapterSpinnerSearch
        adapterSpinnerDayOfWeek = new AdapterSpinnerDayOfWeek(this, android.R.layout.simple_spinner_item);

        // Establece el adaptador para el Spinner
        spinnerDayOfWeek.setAdapter(adapterSpinnerDayOfWeek);

    }

    public void onClick(View v) {

        if (v == btnStarTime || v == btnEndTime) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // Agregar un "0" delante de la hora si es menor que 10
                    String formattedHour = (hourOfDay < 10) ? "0" + hourOfDay : String.valueOf(hourOfDay);
                    // Agregar un "0" delante de los minutos si son menores que 10
                    String formattedMinute = (minute < 10) ? "0" + minute : String.valueOf(minute);
                    // Establecer el texto en el EditText
                    if (v == btnStarTime) {
                        etStartTime.setText(formattedHour + ":" + formattedMinute);
                        Log.i("etStartTime", String.valueOf(etStartTime));
                    } else {
                        etEndTime.setText(formattedHour + ":" + formattedMinute);
                        Log.i("etEndTime", String.valueOf(etEndTime));
                    }
                }
            }, hour, minute, true);
            timePickerDialog.show();
        }
    }

    private boolean checkEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true; // Devuelve true si al menos un campo está vacío
            }
        }
        return false; // Devuelve false si todos los campos están llenos
    }

    private void setupConsultationSpinner() {


        consultationService.getConsultationByProfId(professionalId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                consultationList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Consultation consultation = snapshot.getValue(Consultation.class);
                    consultationList.add(consultation);


                }

                Log.i("consultationList", String.valueOf(consultationList.size()));

                adapterSpinnerConsultation = new AdapterSpinnerConsultation(getApplicationContext(), android.R.layout.simple_spinner_item, consultationList);
                adapterSpinnerConsultation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerConsultation.setAdapter(adapterSpinnerConsultation);

                // Obtener los elementos del adaptador y colocarlos en un array de strings
                consultationItems = new String[adapterSpinnerConsultation.getCount()];
                for (int i = 0; i < adapterSpinnerConsultation.getCount(); i++) {
                    //consultationItems[i] = adapterSpinnerConsultation.getItem(i).toString();
                    consultationItems[i] = adapterSpinnerConsultation.getItem(i).getAddress() + " (" + adapterSpinnerConsultation.getItem(i).getCity() + ")";

                    Log.i("consultationItems", "Item " + i + ": " + consultationItems[i]);
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.w("Firebase", "Error en el filtrado de consultas por id.", error.toException());

            }
        });
    }


    /**
     * Método para formatear LocalTime
     *
     * @param snapshot
     * @param timeKey
     * @return
     */
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

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {


            // Simular la obtención de datos de Firebase
            SystemClock.sleep(500); // Simula un trabajo en segundo plano de 3 segundos
            daysOfWeek();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            SystemClock.sleep(500); // Simula un trabajo en segundo plano de 3 segundos


        }
    }

    private void daysOfWeek() {

        List<Timetable> timetables = new ArrayList<>();
        daysOfWeek.clear();
        timetables.clear();


         //Compruebo en todas las consultas del profesional ya que no puede estar en dos sitios a la vez

        timetableService.getTimetableOrderByDay(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Timetable t = new Timetable();
                    t.setConsultationId(String.valueOf(snapshot.child("consultationId").getValue()));
                    t.setId(String.valueOf(snapshot.child("id").getValue()));
                    t.setDayOfWeek(String.valueOf(snapshot.child("dayOfWeek").getValue()));

                    LocalTime startTime = convertSnapshotToTime(snapshot, "startTime");
                    LocalTime endTime = convertSnapshotToTime(snapshot, "endTime");

                    t.setStartTime(startTime);
                    t.setEndTime(endTime);

                    Log.i("timetable object", t.toString());
                    timetables.add(t);


                }

                for (Consultation consultation : consultationList) {
                    for (Timetable timetable1 : timetables) {
                        if (consultation.getId().equals(timetable1.getConsultationId())) {
                            daysOfWeek.add(timetable1);
                        } else {

                        }
                    }
                }

                Log.i("timetableWithWeek", daysOfWeek.toString());

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en el filtrado de timetables por id.", error.toException());
                progressDialog.dismiss();
            }
        });





    }


}