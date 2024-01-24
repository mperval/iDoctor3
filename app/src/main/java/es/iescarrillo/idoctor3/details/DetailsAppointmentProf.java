package es.iescarrillo.idoctor3.details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Map;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.activities.EvaluationProfessionalActivity;
import es.iescarrillo.idoctor3.activities.TimeTableActivity;
import es.iescarrillo.idoctor3.listView.LvMyAppointmentConsultation;
import es.iescarrillo.idoctor3.listView.LvMyAppointmentProf;
import es.iescarrillo.idoctor3.listView.LvTimetable;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.models.Timetable;
import es.iescarrillo.idoctor3.services.AppointmentService;

public class DetailsAppointmentProf extends AppCompatActivity {

    private Appointment appointment;
    private EditText etDateAppointmentDetailsProf, etTimeAppointmentDetailsProf, etTimeUpdateDetailsProf;
    private Switch switchAppointmentDetailsProf;
    private Button btnTimeUpdateDetailsProf, btnDeleteAppointmentDetailsProf, btnUpdateAppointmentDetailsProf, btnBackAppointmentDetailsProf, btnAddEvaluation;
    private SharedPreferences sharedPreferences;
    private String professionalId;
    private AppointmentService appointmentService;
    private int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_appointment_prof);

        appointmentService = new AppointmentService(getApplicationContext());

        appointment = new Appointment();

        //Variables de sesion
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        professionalId = sharedPreferences.getString("id", "");

        Intent intent = getIntent();
        Appointment receivedAppointment = (Appointment) intent.getSerializableExtra("appointment");

        Log.i("receivedAppointmentLocalDateTime", receivedAppointment.getId());

        Log.i("receivedAppointmentLOCALDATETIME", receivedAppointment.getAppointmentDate().toString()+" / "+receivedAppointment.getAppointmentTime().toString() );

        Log.i("receivedAppointmentLOCALDATETIME", convertirALocalDateTime(receivedAppointment.getAppointmentDate(), receivedAppointment.getAppointmentTime()).toString());


        getComponentFromView();

        //Rellenar información en los campos de texto
        etDateAppointmentDetailsProf.setText(receivedAppointment.getAppointmentDate().toString());
        etTimeAppointmentDetailsProf.setText(receivedAppointment.getAppointmentTime().toString());
        switchAppointmentDetailsProf.setChecked(receivedAppointment.getActive());

        btnTimeUpdateDetailsProf.setOnClickListener(this::onClick);

        btnBackAppointmentDetailsProf.setOnClickListener(v -> {
            Intent viewLvMyAppointmentProf = new Intent(this, LvMyAppointmentConsultation.class);
            startActivity(viewLvMyAppointmentProf);
            finish();
        });

        btnDeleteAppointmentDetailsProf.setOnClickListener(v -> {
            appointmentService.deleteAppointment(receivedAppointment.getId());

            Toast.makeText(this, "Appointment Deleted", Toast.LENGTH_SHORT).show();

            Intent intentLvMyAppointmentProf = new Intent(this, LvMyAppointmentConsultation.class);
            startActivity(intentLvMyAppointmentProf);
            finish();
        });

        btnUpdateAppointmentDetailsProf.setOnClickListener(v -> {

            String newHour = etTimeUpdateDetailsProf.getText().toString();

            if (newHour.isEmpty()) {

                appointment.setId(receivedAppointment.getId());
                appointment.setConsultationId(receivedAppointment.getConsultationId());
                appointment.isActive(switchAppointmentDetailsProf.isChecked());
                appointment.setAppLocalDateTime(convertirALocalDateTime(receivedAppointment.getAppointmentDate(), receivedAppointment.getAppointmentTime()));
                appointmentService.updateAppointment(appointment);

                Toast.makeText(DetailsAppointmentProf.this, "Appointment updated", Toast.LENGTH_LONG).show();


            } else {
                appointment.setId(receivedAppointment.getId());
                appointment.setConsultationId(receivedAppointment.getConsultationId());
                Log.i("etTimeUpdateDetailsProf", etTimeUpdateDetailsProf.getText().toString());
                LocalTime newTime = LocalTime.parse(etTimeUpdateDetailsProf.getText().toString());
                appointment.setAppLocalDateTime(convertirALocalDateTime(receivedAppointment.getAppointmentDate(), newTime));
                appointment.isActive(switchAppointmentDetailsProf.isChecked());
                appointmentService.updateAppointment(appointment);

                Toast.makeText(DetailsAppointmentProf.this, "Appointment updated", Toast.LENGTH_LONG).show();

            }

        });

        btnAddEvaluation.setOnClickListener(v -> {

            Intent intentLvMyAppointmentProf = new Intent(this, EvaluationProfessionalActivity.class);
            intentLvMyAppointmentProf.putExtra("appointmentId", receivedAppointment.getId());
            startActivity(intentLvMyAppointmentProf);
            finish();
        });
    }


    private void getComponentFromView() {
        etDateAppointmentDetailsProf = findViewById(R.id.etDateAppointmentDetailsProf);
        etTimeAppointmentDetailsProf = findViewById(R.id.etTimeAppointmentDetailsProf);
        etTimeUpdateDetailsProf = findViewById(R.id.etTimeUpdateDetailsProf);
        switchAppointmentDetailsProf = findViewById(R.id.switchAppointmentDetailsProf);
        btnTimeUpdateDetailsProf = findViewById(R.id.btnTimeUpdateDetailsProf);
        btnDeleteAppointmentDetailsProf = findViewById(R.id.btnDeleteAppointmentDetailsProf);
        btnUpdateAppointmentDetailsProf = findViewById(R.id.btnUpdateAppointmentDetailsProf);
        btnBackAppointmentDetailsProf = findViewById(R.id.btnBackAppointmentDetailsProf);
        switchAppointmentDetailsProf = findViewById(R.id.switchAppointmentDetailsProf);
        btnAddEvaluation = findViewById(R.id.btnAddEvaluation);

    }

    public void onClick(View v) {

        if (v == btnTimeUpdateDetailsProf) {
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
                    if (v == btnTimeUpdateDetailsProf) {
                        etTimeUpdateDetailsProf.setText(formattedHour + ":" + formattedMinute);
                        Log.i("etTimeUpdateDetailsProf", String.valueOf(etTimeUpdateDetailsProf));
                    }
                }
            }, hour, minute, true);
            timePickerDialog.show();
        }
    }

    public static LocalDateTime convertirALocalDateTime(LocalDate fecha, LocalTime hora) {
        return fecha.atTime(hora);
    }




}