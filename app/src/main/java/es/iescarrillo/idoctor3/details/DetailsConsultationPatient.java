package es.iescarrillo.idoctor3.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.listView.LvAppointmentConsultationPatient;
import es.iescarrillo.idoctor3.listView.LvConsultationProfessionalPatient;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.services.ConsultationService;

public class DetailsConsultationPatient extends AppCompatActivity {
private String id, consultationId;
    private EditText etAddressConsultation, etCityConsultation, etEmailConsultation, etPhoneConsultation, etPhoneAuxConsultation, etObservationConsultation;
    private Button btnAppointmentPatient, btnBackConsultation;
    private Consultation consultation;
    private ConsultationService consultationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_consultation_patient);

        Intent intent = getIntent();
        if(intent != null){
            id = intent.getStringExtra("professionalId");
            consultationId = intent.getStringExtra("ConsultationId");
            Log.i("professionalId", id);
            Log.i("ConsultationId", consultationId);
        }


        consultation = new Consultation();
        consultationService = new ConsultationService(getApplicationContext());
        //llamo al metodo
        getComponentFromView();

        consultationService.getConsultationById(consultationId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()){

                    Consultation consultation = datasnapshot.getValue(Consultation.class);
                    Log.i("snapshot", snapshot.toString());

                    if(datasnapshot != null){
                        etAddressConsultation.setText(consultation.getAddress());
                        etCityConsultation.setText(consultation.getCity());
                        etEmailConsultation.setText(consultation.getEmail());
                        etPhoneConsultation.setText(consultation.getPhone());
                        etPhoneAuxConsultation.setText(consultation.getPhoneAux());
                        etObservationConsultation.setText(consultation.getObservation());


                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en la lectura en la base de datos.", error.toException());
            }
        });
        btnBackConsultation.setOnClickListener(v -> {
            Intent intent1 = new Intent(DetailsConsultationPatient.this, LvConsultationProfessionalPatient.class);
            startActivity(intent1);
        });
        btnAppointmentPatient.setOnClickListener(v -> {
            Intent intent1 = new Intent(DetailsConsultationPatient.this, LvAppointmentConsultationPatient.class);
            intent1.putExtra("consultationId", consultationId);
            Log.i("ConsultationIdBnt", consultationId);
            startActivity(intent1);
        });
    }
    private void getComponentFromView(){
        etAddressConsultation = findViewById(R.id.etAddressConsultationP);
        etCityConsultation = findViewById(R.id.etCityConsultationP);
        etEmailConsultation = findViewById(R.id.etEmailConsultationP);
        etPhoneConsultation = findViewById(R.id.etPhoneConsultationP);
        etPhoneAuxConsultation = findViewById(R.id.etPhoneAuxConsultationP);
        etObservationConsultation = findViewById(R.id.etObservationConsultationP);
        btnBackConsultation = findViewById(R.id.btnBackAppointmentPatient);
        btnAppointmentPatient = findViewById(R.id.btnAppointmentPatient);
    }
}