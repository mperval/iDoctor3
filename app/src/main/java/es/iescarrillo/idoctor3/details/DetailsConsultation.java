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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.ArrayUtils;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.listView.LvConsultations;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.services.ConsultationService;

public class DetailsConsultation extends AppCompatActivity {
    private EditText etAddressViewConsultation, etCityViewConsultation, etEmailViewConsultation,
            etPhoneViewConsultation, etPhoneViewAuxConsultation, etObservationViewConsultation;
    private Button btnDeteleViewConsultation, btnUpdateViewConsultation, btnBackViewConsultation;
    private Consultation consultation;
    private ConsultationService consultationService;
    private SharedPreferences sharedPreferences;
    private String id, idConsultation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_consultation);

        //recojo el id
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        idConsultation = sharedPreferences.getString("idConsultation", "");
        Log.i("id de la consulta", idConsultation);

        consultation = new Consultation();
        consultationService = new ConsultationService(getApplicationContext());
        //llamo al metodo
        getComponentFromView();

        consultationService.getConsultationById(idConsultation, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()){

                    Consultation consultation = datasnapshot.getValue(Consultation.class);
                    Log.i("snapshot", snapshot.toString());

                    if(datasnapshot!=null){
                        etAddressViewConsultation.setText(consultation.getAddress());
                        etCityViewConsultation.setText(consultation.getCity());
                        etEmailViewConsultation.setText(consultation.getEmail());
                        etPhoneViewConsultation.setText(consultation.getPhone());
                        etPhoneViewAuxConsultation.setText(consultation.getPhoneAux());
                        etObservationViewConsultation.setText(consultation.getObservation());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en la lectura en la base de datos.", error.toException());
            }
        });

        btnUpdateViewConsultation.setOnClickListener(v-> {

            // Obtener los datos ingresados por el usuario
            String addressConsultation = etAddressViewConsultation.getText().toString().trim();
            String cityConsultation = etCityViewConsultation.getText().toString().trim();
            String emailConsultation = etEmailViewConsultation.getText().toString().trim();
            String phoneConsultation = etPhoneViewConsultation.getText().toString().trim();
            String phoneAuxConsultation = etPhoneViewAuxConsultation.getText().toString().trim();
            String observationConsultation = etObservationViewConsultation.getText().toString().trim();

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
                consultation.setId(idConsultation);
                consultationService.updateConsultation(consultation);
                Toast.makeText(this, "Consultation changed", Toast.LENGTH_SHORT).show();

                Intent intentConsultations = new Intent(this, LvConsultations.class);
                startActivity(intentConsultations);
                finish();

            }
        });

        btnDeteleViewConsultation.setOnClickListener(v-> {

            consultationService.deleteConsultation(idConsultation);
            Toast.makeText(this, "Consultation Deleted", Toast.LENGTH_SHORT).show();

            Intent intentConsultations = new Intent(this, LvConsultations.class);
            startActivity(intentConsultations);
            finish();

        });


        btnBackViewConsultation.setOnClickListener(v-> {

            Intent intentLv = new Intent(this, LvConsultations.class);
            startActivity(intentLv);
            finish();

        });
    }


    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        etAddressViewConsultation = findViewById(R.id.etAddressViewConsultation);
        etCityViewConsultation = findViewById(R.id.etCityViewConsultation);
        etEmailViewConsultation = findViewById(R.id.etEmailViewConsultation);
        etPhoneViewConsultation = findViewById(R.id.etPhoneViewConsultation);
        etPhoneViewAuxConsultation = findViewById(R.id.etPhoneViewAuxConsultation);
        etObservationViewConsultation = findViewById(R.id.etObservationViewConsultation);
        btnBackViewConsultation = findViewById(R.id.btnBackViewConsultation);
        btnDeteleViewConsultation = findViewById(R.id.btnDeteleViewConsultation);
        btnUpdateViewConsultation = findViewById(R.id.btnUpdateViewConsultation);
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