package es.iescarrillo.idoctor3.listView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.adapters.AdapterAppointment;
import es.iescarrillo.idoctor3.details.DetailsAppointmentProf;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.services.AppointmentService;

public class LvMyAppointmentProf extends AppCompatActivity {

    private Button btnBackMyLvConsApp;
    private ListView lvMyConsultationApp;
    private AppointmentService appointmentService;
    private SharedPreferences sharedPreferences;
    private ArrayList<Appointment> appointmentsAdapterArray;
    private List<Appointment> appointmentList;
    private AdapterAppointment adapterAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lv_my_appointment_prof);

        appointmentService = new AppointmentService(getApplicationContext());
        //recojo el id
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        String professionalId = sharedPreferences.getString("id", "");

        //Recojo el objet del intent anterior
        Intent intent = getIntent();
        Consultation receivedConsultation = (Consultation) intent.getSerializableExtra("consultation");
        Log.i("receivedConsultation", receivedConsultation.toString());

        //llamo al metodo
        getComponentFromView();

        //inicializamos la lista de consultas que le vamos a pasar a nuestro adaptador
        appointmentsAdapterArray = new ArrayList<>();
        appointmentList = new ArrayList<>();

        //Obtenemos la referencial al nodo 'consultation'
        DatabaseReference dbConsultation = FirebaseDatabase.getInstance().getReference().child("appointment");

        appointmentService.getAppointmentByConsultationId(receivedConsultation.getId().toString(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Appointment app = new Appointment();

                    app.setId(String.valueOf(snapshot.child("id").getValue()));
                    app.setConsultationId(String.valueOf(snapshot.child("consultationId").getValue()));
                    app.isActive((Boolean) snapshot.child("active").getValue());

                    LocalTime appointmentTime = convertSnapshotToTime(snapshot,"appLocalDateTime");
                    LocalDate appointmentDate = convertSnapshotToDate(snapshot, "appLocalDateTime");


                    app.setAppointmentDate(appointmentDate);
                    app.setAppointmentTime(appointmentTime);

                    appointmentList.add(app);
                }

                // Ordenar la lista por date y time
                Collections.sort(appointmentList, new Comparator<Appointment>() {
                    @Override
                    public int compare(Appointment app1, Appointment app2) {
                        // Comparar por date
                        int dateComparison = app1.getAppointmentDate().compareTo(app2.getAppointmentDate());

                        // Si las fechas son iguales, comparar por time
                        if (dateComparison == 0) {
                            return app1.getAppointmentTime().compareTo(app2.getAppointmentTime());
                        }

                        return dateComparison;
                    }
                });

                // Crear y establecer el adaptador después de ordenar la lista
                adapterAppointment = new AdapterAppointment(getApplicationContext(), appointmentList);
                lvMyConsultationApp.setAdapter(adapterAppointment);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.w("Firebase", "Error en la lectura de la BBDDD");

            }
        });

        lvMyConsultationApp.setOnItemClickListener((parent, view, position, id) -> {

            Appointment appointment = (Appointment) parent.getItemAtPosition(position);
            Log.i("consultation que pulsa", appointment.getId());

            Intent intent1 = new Intent(LvMyAppointmentProf.this, DetailsAppointmentProf.class);
            intent1.putExtra("appointment", appointment);
            startActivity(intent1);
        });
    }

    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){

        btnBackMyLvConsApp = findViewById(R.id.btnBackMyLvConsApp);
        lvMyConsultationApp = findViewById(R.id.lvMyConsultationApp);
    }

    /**
     * Método para convertir a LocalTime
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

    /**
     * Método para convertir a LocalDate
     * @param snapshot
     * @param dateTimeKey
     * @return
     */
    private LocalDate convertSnapshotToDate(DataSnapshot snapshot, String dateTimeKey) {
        DataSnapshot dateTimeSnapshot = snapshot.child(dateTimeKey);

        Integer year = Integer.parseInt(dateTimeSnapshot.child("year").getValue().toString());
        Integer month = Integer.parseInt(dateTimeSnapshot.child("monthValue").getValue().toString());
        Integer day = Integer.parseInt(dateTimeSnapshot.child("dayOfMonth").getValue().toString());

        return LocalDate.of(year, month, day);
    }
}