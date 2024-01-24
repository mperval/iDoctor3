package es.iescarrillo.idoctor3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.listView.LvConsultations;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.services.ConsultationService;
import es.iescarrillo.idoctor3.services.PatientService;

public class ConsultationRegisterActivity extends AppCompatActivity {
    private EditText etAddressConsultation, etCityConsultation, etEmailConsultation,
            etPhoneConsultation, etPhoneAuxConsultation, etObservationConsultation;
    private Button btnBack, btnRegisterConsultation;
    private Consultation consultation;
    private ConsultationService consultationService;
    private SharedPreferences sharedPreferences;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_register);

        //recojo el id
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        Log.i("id para consulta", id);

        consultation = new Consultation();
        consultationService = new ConsultationService(getApplicationContext());
        //llamo al metodo
        getComponentFromView();

        btnRegisterConsultation.setOnClickListener(v-> {

            // Obtener los datos ingresados por el usuario
            String addressConsultation = etAddressConsultation.getText().toString().trim();
            String cityConsultation = etCityConsultation.getText().toString().trim();
            String emailConsultation = etEmailConsultation.getText().toString().trim();
            String phoneConsultation = etPhoneConsultation.getText().toString().trim();
            String phoneAuxConsultation = etPhoneAuxConsultation.getText().toString().trim();
            String observationConsultation = etObservationConsultation.getText().toString().trim();

            // Validar que se hayan completado todos los campos
            if (addressConsultation.isEmpty() || cityConsultation.isEmpty() || emailConsultation.isEmpty() ||
                    phoneConsultation.isEmpty() || phoneAuxConsultation.isEmpty() || observationConsultation.isEmpty()) {
                Toast.makeText(this, "All fields must be completed", Toast.LENGTH_SHORT).show();

            }else {

                consultation.setAddress(addressConsultation);
                consultation.setCity(cityConsultation);
                consultation.setEmail(emailConsultation);
                consultation.setPhone(phoneConsultation);
                consultation.setPhoneAux(phoneAuxConsultation);
                consultation.setObservation(observationConsultation);
                //seteo el id del professional
                consultation.setProfessionalId(id);

                consultationService.insertConsultation(consultation);
                Toast.makeText(this, "Consultation Registered", Toast.LENGTH_SHORT).show();

                Intent intentConsultations = new Intent(this, LvConsultations.class);
                startActivity(intentConsultations);
                finish();

            }
        });

        btnBack.setOnClickListener(v-> {

            Intent intentBack = new Intent(this, MenuActivity.class);
            startActivity(intentBack);
            finish();

        });


    }


    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        etAddressConsultation = findViewById(R.id.etAddressConsultation);
        etCityConsultation = findViewById(R.id.etCityConsultation);
        etEmailConsultation = findViewById(R.id.etEmailConsultation);
        etPhoneConsultation = findViewById(R.id.etPhoneConsultation);
        etPhoneAuxConsultation = findViewById(R.id.etPhoneAuxConsultation);
        etObservationConsultation = findViewById(R.id.etObservationConsultation);
        btnBack = findViewById(R.id.btnBack);
        btnRegisterConsultation = findViewById(R.id.btnRegisterConsultation);
    }

    // Método para verificar si algún campo está vacío
    private boolean checkEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true; // Devuelve true si al menos un campo está vacío
            }
        }
        return false; // Devuelve false si todos los campos están llenos
    }


}