package es.iescarrillo.idoctor3.details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.services.AppointmentService;

public class DetailsPatientAppointment extends AppCompatActivity {

    private EditText etDateAppointmentDetails, etTimeAppointmentDetails, etNameProfessionalApppointment, etSpecialityPProfessionalApppointmentDetails, etAddressConsultationAppointmentDetails, etCityConsultationAppointmentDetails;
   private Button btnDeleteAppointment;
   private AppointmentService appointmentService;
   private Appointment appointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_patient_appointment);

        appointmentService = new AppointmentService(getApplicationContext());
        appointment = new Appointment();

        etDateAppointmentDetails = findViewById(R.id.etDateAppointmentDetails);
        etTimeAppointmentDetails = findViewById(R.id.etTimeAppointmentDetails);
        etNameProfessionalApppointment = findViewById(R.id.etNameProfessionalApppointment);
        etSpecialityPProfessionalApppointmentDetails = findViewById(R.id.etSpecialityPProfessionalApppointmentDetails);
        etAddressConsultationAppointmentDetails = findViewById(R.id.etAddressConsultationAppointmentDetails);
        etCityConsultationAppointmentDetails = findViewById(R.id.etCityConsultationAppointmentDetails);
        btnDeleteAppointment = findViewById(R.id.btnDeleteAppointment);

        Intent intent = getIntent();

        if(intent != null){
            String id = intent.getStringExtra("id");
            //String date = intent.getStringExtra("date");
            //String time = intent.getStringExtra("time");
            String nameDoctor = intent.getStringExtra("nameDoctor");
            String address = intent.getStringExtra("address");
            String speciality = intent.getStringExtra("speciality");
            String city = intent.getStringExtra("city");
            String activate = intent.getStringExtra("activate");
            String idPatient = intent.getStringExtra("idPatient");
            String idConsultation = intent.getStringExtra("idConsultation");

            //etDateAppointmentDetails.setText(date);
            //etTimeAppointmentDetails.setText(time);
            etNameProfessionalApppointment.setText(nameDoctor);
            etSpecialityPProfessionalApppointmentDetails.setText(speciality);
            etAddressConsultationAppointmentDetails.setText(address);
            etCityConsultationAppointmentDetails.setText(city);

            btnDeleteAppointment.setOnClickListener(v -> {
                appointment.setId(id);
                /*appointment.setAppointmentDate(date);
                appointment.setAppointmentTime(time);*/
                appointment.setPatientId("");
                appointment.isActive(false);
                appointment.setConsultationId(idConsultation);
                appointmentService.updateAppointment(appointment);

            });

        }



    }
}