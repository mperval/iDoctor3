package es.iescarrillo.idoctor3.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.activities.MenuActivity;
import es.iescarrillo.idoctor3.listView.LvConsultationProfessionalPatient;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.services.AppointmentService;

public class DetailsConsultationAppointment extends AppCompatActivity {
    private Appointment receivedAppointment;
    private EditText etDateAppointmentDetailsA, etTimeAppointmentDetailsA;
    private Switch switch1A;
    private Button btnReserveAppointmentA, btnBackA;
    private SharedPreferences sharedPreferences;
    private String patientId;
    private Appointment app;
    private AppointmentService appointmentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_consultation_appointment);

        app = new Appointment();

        appointmentService = new AppointmentService(getApplicationContext());

        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        patientId = sharedPreferences.getString("id", "");
        Log.i("idPatient", patientId);

        etDateAppointmentDetailsA = findViewById(R.id.etDateAppointmentDetailsA);
        etTimeAppointmentDetailsA = findViewById(R.id.etTimeAppointmentDetailsA);
        btnReserveAppointmentA = findViewById(R.id.btnReserveAppointmentA);

        btnBackA = findViewById(R.id.btnBackA);
        switch1A = findViewById(R.id.switch1A);

        Intent intent = getIntent();

        receivedAppointment = (Appointment) intent.getSerializableExtra("objAppo");

        if (receivedAppointment != null) {
            etDateAppointmentDetailsA.setText(String.valueOf(receivedAppointment.getAppointmentDate()));
            etTimeAppointmentDetailsA.setText(String.valueOf(receivedAppointment.getAppointmentTime()));
            switch1A.setChecked(receivedAppointment.getActive());
            Log.i("idAppo", receivedAppointment.getId());
        }


        btnReserveAppointmentA.setOnClickListener(v -> {

            app.isActive(false);
            app.setId(receivedAppointment.getId());
            app.setConsultationId(receivedAppointment.getConsultationId());
            app.isActive(switch1A.isChecked());
            app.setAppLocalDateTime(convertirALocalDateTime(receivedAppointment.getAppointmentDate(), receivedAppointment.getAppointmentTime()));
            app.setPatientId(patientId);

            Log.i("app pickeada", app.toString());

            appointmentService.updateAppointment(app);

            Toast.makeText(getApplicationContext(), "Appointment reserved", Toast.LENGTH_SHORT).show();
        });

        btnBackA.setOnClickListener(v -> {

            Intent intent1 = new Intent(DetailsConsultationAppointment.this, MenuActivity.class);
            startActivity(intent1);
            finish();

        });
    }


    public static LocalDateTime convertirALocalDateTime(LocalDate fecha, LocalTime hora) {
        return fecha.atTime(hora);
    }
}