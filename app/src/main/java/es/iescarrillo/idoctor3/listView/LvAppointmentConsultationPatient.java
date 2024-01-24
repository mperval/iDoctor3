package es.iescarrillo.idoctor3.listView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.adapters.AdapterAppointment;
import es.iescarrillo.idoctor3.details.DetailsConsultationAppointment;
import es.iescarrillo.idoctor3.details.DetailsConsultationPatient;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.services.AppointmentService;

public class LvAppointmentConsultationPatient extends AppCompatActivity {
    private List<Appointment> appointments = new ArrayList<>();
    private AppointmentService appointmentService;
    private ListView lvAppoConPa;
    private Button back;
    private AdapterAppointment adapterAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lv_appointment_consultation_patient);

        lvAppoConPa = findViewById(R.id.lvAppointmentPatientC);
        back = findViewById(R.id.btnBackLvAppointmentC);

        appointmentService = new AppointmentService(getApplicationContext());

        Intent intent = getIntent();

        if(intent != null){
            String consultationId = intent.getStringExtra("consultationId");
            if(consultationId != null){
                Log.i("consultationId", consultationId);
                getAppointment(consultationId);


            }
        }
        lvAppoConPa.setOnItemClickListener((parent, view, position, id) -> {
            Appointment appointment = (Appointment) parent.getItemAtPosition(position);

            Intent intent1 = new Intent(LvAppointmentConsultationPatient.this, DetailsConsultationAppointment.class);
            intent1.putExtra("objAppo", appointment);
            startActivity(intent1);
        });

        back.setOnClickListener(v -> {
            Intent view = new Intent(this, DetailsConsultationPatient.class);
            startActivity(view);
            finish();
        });
    }

    public void getAppointment(String receivedConsultationId){
        appointmentService.getAppointmentByConsultationId(receivedConsultationId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                appointments.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Appointment app = new Appointment();

                    app.setId(String.valueOf(snapshot.child("id").getValue()));
                    app.isActive((Boolean) snapshot.child("active").getValue());
                    app.setConsultationId(String.valueOf(snapshot.child("consultationId").getValue()));

                    LocalTime appointmentTime = convertSnapshotToTime(snapshot,"appLocalDateTime");
                    LocalDate appointmentDate = convertSnapshotToDate(snapshot, "appLocalDateTime");

                    app.setAppointmentDate(appointmentDate);
                    app.setAppointmentTime(appointmentTime);

                    appointments.add(app);
                    Log.i("FirebaseData", "Cita obtenida: " + app);
                }
                Collections.sort(appointments, new Comparator<Appointment>() {
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
                adapterAppointment = new AdapterAppointment(getApplicationContext(), appointments);
                lvAppoConPa.setAdapter(adapterAppointment);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private LocalDate convertSnapshotToDate(DataSnapshot snapshot, String dateTimeKey) {
        DataSnapshot dateTimeSnapshot = snapshot.child(dateTimeKey);

        Integer year = Integer.parseInt(dateTimeSnapshot.child("year").getValue().toString());
        Integer month = Integer.parseInt(dateTimeSnapshot.child("monthValue").getValue().toString());
        Integer day = Integer.parseInt(dateTimeSnapshot.child("dayOfMonth").getValue().toString());

        return LocalDate.of(year, month, day);
    }

    private LocalDateTime convertSnapshotToDateTime(DataSnapshot snapshot, String dateTimeKey) {
        DataSnapshot dateTimeSnapshot = snapshot.child(dateTimeKey);
        Map<String, Object> dateTimeMap = (Map<String, Object>) dateTimeSnapshot.getValue();

        int year = Integer.parseInt(dateTimeMap.get("year").toString());
        int month = Integer.parseInt(dateTimeMap.get("monthValue").toString());
        int dayOfMonth = Integer.parseInt(dateTimeMap.get("dayOfMonth").toString());
        int hour = Integer.parseInt(dateTimeMap.get("hour").toString());
        int minute = Integer.parseInt(dateTimeMap.get("minute").toString());
        int second = Integer.parseInt(dateTimeMap.get("second").toString());

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
    }
}