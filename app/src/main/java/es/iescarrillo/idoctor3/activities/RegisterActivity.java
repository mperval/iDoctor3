package es.iescarrillo.idoctor3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.iescarrillo.idoctor3.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etPinRegister;
    private Button btnPatientRegister, btnProfessionalRegister;
    private String pin = "4321";
    private String pinRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPinRegister = findViewById(R.id.etPinRegister);
        btnPatientRegister = findViewById(R.id.btnPatientRegister);
        btnProfessionalRegister = findViewById(R.id.btnProfessionalRegister);

        btnPatientRegister.setOnClickListener(v -> {
            Intent patientRegister = new Intent(this, PatientRegisterActivity.class);
            startActivity(patientRegister);
            finish();
        });

        btnProfessionalRegister.setOnClickListener(v -> {

            pinRegister = etPinRegister.getText().toString().trim();

            if (pin.equals(pinRegister)) {

                Intent professionalRegister = new Intent(this, ProfessionalRegisterActivity.class);
                startActivity(professionalRegister);
                finish();

            } else {
                Toast.makeText(RegisterActivity.this, "PIN needed", Toast.LENGTH_SHORT).show();
            }

        });
    }
}