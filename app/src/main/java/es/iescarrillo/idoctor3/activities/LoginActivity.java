package es.iescarrillo.idoctor3.activities;

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

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalTime;
import java.util.Date;



import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.models.Evaluation;
import es.iescarrillo.idoctor3.models.Patient;
import es.iescarrillo.idoctor3.services.AppointmentService;
import es.iescarrillo.idoctor3.services.PatientService;
import es.iescarrillo.idoctor3.services.ProfessionalService;

public class LoginActivity extends AppCompatActivity {

    private PatientService patientService;
    private ProfessionalService professionalService;
    private EditText etEnterUser, etEnterPassword;
    private Button btnSignUp, btnLogin;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        patientService = new PatientService(getApplicationContext());
        professionalService = new ProfessionalService(getApplicationContext());

        etEnterUser = findViewById(R.id.etUserNameLogin);
        etEnterPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> {
            Intent registerActivity = new Intent(this, RegisterActivity.class);

            startActivity(registerActivity);

            finish();
        });

        btnLogin.setOnClickListener(v -> {
            if (etEnterUser.getText().toString().isEmpty() || etEnterPassword.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "All fields must be completed", Toast.LENGTH_SHORT).show();
            } else {

                String username = etEnterUser.getText().toString();
                String password = etEnterPassword.getText().toString();

                // Realiza una consulta en el nodo de pacientes
                patientService.getPatientByUsername(username, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot patientSnapshot) {

                        Log.i("PatientSnapshot", patientSnapshot.toString());

                        boolean patientFound = false;

                        for (DataSnapshot patient : patientSnapshot.getChildren()) {
                            String storedPassword = (String) patient.child("password").getValue();
                            String id = (String) patient.child("id").getValue();
                            String role = (String) patient.child("role").getValue();
                            String photo = (String) patient.child("photo").getValue();

                            if (BCrypt.checkpw(password, storedPassword)) {
                                // Contraseña correcta, realiza acciones para pacientes

                                sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("login", true);
                                editor.putString("id", id);
                                editor.putString("username", username);
                                editor.putString("role", role);
                                editor.putString("photo", photo);

                                editor.apply();

                                Log.i(" role: ", role);
                                Log.i(" username: ", username);
                                Log.i(" id: ", id);

                                Toast.makeText(LoginActivity.this, "Correct Patient Login", Toast.LENGTH_SHORT).show();
                                Intent patientIntent = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(patientIntent);
                                finish();
                                patientFound = true;
                                break;
                            }
                        }

                        if (!patientFound) {

                            // Si no se encontró en pacientes, realiza una consulta en el nodo de profesionales
                            professionalService.getProfessionalByUsername(username, new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot professionalSnapshot) {

                                    Log.i("ProfessionalSnapshot", professionalSnapshot.toString());

                                    boolean professionalFound = false;

                                    for (DataSnapshot professional : professionalSnapshot.getChildren()) {
                                        String storedPassword = (String) professional.child("password").getValue();
                                        String id = (String) professional.child("id").getValue();
                                        String role = (String) professional.child("role").getValue();
                                        String photo = (String) professional.child("photo").getValue();

                                        if (BCrypt.checkpw(password, storedPassword)) {
                                            // Contraseña correcta, realiza acciones para profesionales

                                            sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean("login", true);
                                            editor.putString("id", id);
                                            editor.putString("role", role);
                                            editor.putString("username", username);
                                            editor.putString("photo", photo);
                                            editor.apply();

                                            Log.i(" role: ", role);
                                            Log.i(" username: ", username);
                                            Log.i(" id: ", id);

                                            Toast.makeText(LoginActivity.this, "Correct Professional Login", Toast.LENGTH_SHORT).show();
                                            Intent professionalIntent = new Intent(LoginActivity.this, MenuActivity.class);
                                            startActivity(professionalIntent);
                                            finish();
                                            professionalFound = true;
                                            break;
                                        }
                                    }

                                    if (!professionalFound) {
                                        // Contraseña incorrecta para profesionales o usuario no encontrado
                                        Toast.makeText(LoginActivity.this, "Incorrect Password or Username", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Error", error.getMessage());
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error Login", error.getDetails());
                    }
                });
            }
        });

    }
}