package es.iescarrillo.idoctor3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.adapters.AdapterSpinnerAppDuration;
import es.iescarrillo.idoctor3.adapters.AdapterSpinnerNextDays;
import es.iescarrillo.idoctor3.listView.LvConsultationsAppointment;
import es.iescarrillo.idoctor3.listView.LvTimetable;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.Timetable;
import es.iescarrillo.idoctor3.services.AppointmentService;
import es.iescarrillo.idoctor3.services.TimetableService;

public class GenerateAppointmentActivity extends AppCompatActivity {

    private Button btnBackGenApp, btnGenApp;
    private Spinner spinnerNextDays, spinnerDuration;
    private EditText etDaysApp, etDurationApp;
    private AppointmentService appointmentService;
    private TimetableService timetableService;
    private List<Timetable> timetableList;
    private AdapterSpinnerAppDuration adapterSpinnerAppDuration;
    private AdapterSpinnerNextDays adapterSpinnerNextDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_appointment);

        getComponentFromView();


        appointmentService = new AppointmentService(getApplicationContext());
        timetableService = new TimetableService(getApplicationContext());

        Intent intent = getIntent();
        Consultation receivedConsultation = (Consultation) intent.getSerializableExtra("consultation");

        getTimetableList(receivedConsultation.getId().toString());
        setupSpinner();
        getAppointment(receivedConsultation.getId().toString());

        btnBackGenApp.setOnClickListener(v -> {
            Intent intentLVConsultApp = new Intent(this, LvConsultationsAppointment.class);
            startActivity(intentLVConsultApp);
            finish();
        });

        btnGenApp.setOnClickListener(v -> {

            String nextDays = String.valueOf(spinnerNextDays.getSelectedItem());
            String duration = String.valueOf(spinnerDuration.getSelectedItem());

            etDaysApp.setText(nextDays);
            etDurationApp.setText(duration);

            LocalDateTime currentDateTime = LocalDateTime.now();

            for (int i = 0; i < Integer.parseInt(nextDays); i++) {
                LocalDateTime nextDateTime = currentDateTime.plusDays(i);

                for (Timetable timetable : timetableList) {
                    if (timetable.getDayOfWeek().equalsIgnoreCase(nextDateTime.getDayOfWeek().toString())) {
                        Log.i("localDateTimeDuration", nextDateTime.getDayOfWeek() + " - " + nextDateTime);

                        LocalDateTime startDateTime = LocalDateTime.of(nextDateTime.toLocalDate(), timetable.getStartTime());
                        LocalDateTime endDateTime = LocalDateTime.of(nextDateTime.toLocalDate(), timetable.getEndTime());

                        // Definir el intervalo
                        Duration intervalo = Duration.ofMinutes(Long.parseLong(duration));

                        // Inicializar el datetime de inicio
                        LocalDateTime j = startDateTime;

                        // Iterar hasta que el datetime de inicio sea antes del datetime de fin
                        while (j.isBefore(endDateTime)) {
                            // Imprimir el datetime de inicio y fin de cada intervalo
                            Log.i("localDateTimeDuration", j + " - " + j.plus(intervalo));

                            Appointment app = new Appointment();

                            app.isActive(true);
                            app.setAppLocalDateTime(j);
                            app.setConsultationId(receivedConsultation.getId().toString());

                            Log.i("appointment", app.toString());

                            appointmentService.insertAppointment(app);

                            // Avanzar al siguiente intervalo
                            j = j.plus(intervalo);
                        }
                    }
                }
            }


        });
    }

    private void getComponentFromView() {
        btnBackGenApp = findViewById(R.id.btnBackGenApp);
        btnGenApp = findViewById(R.id.btnGenApp);
        spinnerNextDays = findViewById(R.id.spinnerNextDays);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        etDaysApp = findViewById(R.id.etDaysApp);
        etDurationApp = findViewById(R.id.etDurationApp);

    }

    private void getTimetableList(String receivedConsultationId) {

        timetableList = new ArrayList<>();

        Log.i("getTimetableList", receivedConsultationId);

        timetableService.getTimetableByConsultationId(receivedConsultationId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                timetableList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Timetable t = new Timetable();
                    t.setConsultationId(String.valueOf(snapshot.child("consultationId").getValue()));
                    t.setId(String.valueOf(snapshot.child("id").getValue()));
                    t.setDayOfWeek(String.valueOf(snapshot.child("dayOfWeek").getValue()));

                    LocalTime startTime = convertSnapshotToTime(snapshot, "startTime");
                    LocalTime endTime = convertSnapshotToTime(snapshot, "endTime");

                    t.setStartTime(startTime);
                    t.setEndTime(endTime);

                    timetableList.add(t);
                }
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



    private void setupSpinner() {

        adapterSpinnerAppDuration = new AdapterSpinnerAppDuration(this, android.R.layout.simple_spinner_item);
        spinnerDuration.setAdapter(adapterSpinnerAppDuration);

        adapterSpinnerNextDays = new AdapterSpinnerNextDays(this, android.R.layout.simple_spinner_item);
        spinnerNextDays.setAdapter(adapterSpinnerNextDays);
    }

    public void getAppointment(String receivedConsultationId){

        List<Appointment>appointments=new ArrayList<>();

        appointmentService.getAppointmentByConsultationId(receivedConsultationId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                appointments.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Appointment app = new Appointment();

                    app.setId(String.valueOf(snapshot.child("id").getValue()));
                    app.setConsultationId(String.valueOf(snapshot.child("consultationId").getValue()));
                    app.isActive((Boolean) snapshot.child("active").getValue());

                    LocalTime appointmentTime = convertSnapshotToTime(snapshot,"appLocalDateTime");
                    LocalDate appointmentDate = convertSnapshotToDate(snapshot, "appLocalDateTime");

                    //TODO TRAER UN LOCALDATETIME DEL TIRÓN, EN UNA VARIABLE LOCALDATETIME

                    LocalDateTime localDateTime = convertSnapshotToDateTime(snapshot,"appLocalDateTime");
                    Log.i("LOCALDATETIME", String.valueOf(localDateTime));

                    app.setAppointmentDate(appointmentDate);
                    app.setAppointmentTime(appointmentTime);

                    appointments.add(app);

                }

                for (Appointment appointment: appointments) {

                    Log.i("appointment to String", appointment.toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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