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

import org.apache.commons.lang3.ArrayUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Patient;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.services.PatientService;
import es.iescarrillo.idoctor3.services.ProfessionalService;

public class ChangePassword extends AppCompatActivity {
    private Button btnBackChanger, btnChangerPassword;
    private EditText etEnterPassword, etNewPassword, etRepitePassword;

    private SharedPreferences sharedPreferences;
    private ProfessionalService professionalService;
    private PatientService patientService;
    private Professional professional;
    private Patient patient;
    //datos del professional
    private String name,surname,collegiateNumber,description,selectedSpeciality,password,username,photo;
    //datos del paciente
    private String namePatient, surnamePatient, dniPatient, emailPatient, phonePatient, photoPatient, passwordPatient, usernamePatient;
    private Integer assessments;
    private Double stars;
    private Boolean healthInsurance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnChangerPassword = findViewById(R.id.btnChangerPassword);
        btnBackChanger = findViewById(R.id.btnBackChanger);
        etEnterPassword = findViewById(R.id.etEnterPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etRepitePassword = findViewById(R.id.etRepitPassword);

        professionalService = new ProfessionalService(getApplication());
        patientService = new PatientService(getApplication());

        professional = new Professional();
        patient = new Patient();

        btnChangerPassword.setOnClickListener(v -> {
            sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "");
            String id = sharedPreferences.getString("id", "");
            Log.i(" id: ", id);
            Log.i(" rol: ", role);

            // Check user role
            if (role.equals("Professional")) {

                // Retrieve entered passwords
                String enterPassword = etEnterPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String repitePassword = etRepitePassword.getText().toString().trim();

                // Check if any of the password fields is empty
                if (checkEmpty(enterPassword, newPassword, repitePassword)) {
                    Toast.makeText(ChangePassword.this, "All fields must be completed", Toast.LENGTH_SHORT).show();
                } else {

                    professionalService.getProfessionalById(id, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot datasnapshot : snapshot.getChildren()){

                                Professional professional2 = datasnapshot.getValue(Professional.class);
                                Log.i("snapshot", snapshot.toString());
                                if(professional2!=null) {

                                    name = professional2.getName();
                                    surname = professional2.getSurname();
                                    collegiateNumber = professional2.getCollegiateNumber();
                                    description = professional2.getDescription();
                                    selectedSpeciality = professional2.getSpeciality();
                                    password = professional2.getPassword();
                                    assessments = professional2.getAssessments();
                                    stars = professional2.getStars();
                                    username = professional2.getUsername();
                                    photo = professional2.getPhoto();
                                }
                                Log.i("Entered Password", enterPassword);
                                Log.i("Stored Password", password);
                                // Check if the entered password matches the stored password
                                if (BCrypt.checkpw(enterPassword, password)) {

                                    // Check if the new password and repeated password match
                                    if (newPassword.equals(repitePassword)) {

                                        // Asignamos los valores introducidos
                                        professional.setName(name);
                                        professional.setSurname(surname);
                                        Log.i("surname", surname);
                                        professional.setCollegiateNumber(collegiateNumber);
                                        professional.setDescription(description);
                                        professional.setSpeciality(selectedSpeciality);
                                        String encryptPassword = BCrypt.hashpw(etNewPassword.getText().toString(), BCrypt.gensalt(5));
                                        professional.setPassword(encryptPassword);
                                        professional.setRole(role);
                                        professional.setAssessments(assessments);
                                        professional.setStars(stars);
                                        professional.setUsername(username);
                                        professional.setPhoto(photo);
                                        professional.setId(id);

                                        professionalService.updateProfessional(professional);
                                        // Display a success message
                                        Log.i("id password", "Password updated successfully");
                                        Toast.makeText(ChangePassword.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        Intent intentMenuActivity = new Intent(ChangePassword.this, MenuActivity.class);
                                        startActivity(intentMenuActivity);
                                        finish();
                                    }
                                }else {
                                    Toast.makeText(ChangePassword.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("Firebase", "Error en la lectura en la base de datos.", error.toException());
                        }
                    });
                }

            } else {
                // Retrieve entered passwords
                String enterPassword = etEnterPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String repitePassword = etRepitePassword.getText().toString().trim();

                // Check if any of the password fields is empty
                if (checkEmpty(enterPassword, newPassword, repitePassword)) {
                    Toast.makeText(ChangePassword.this, "All fields must be completed", Toast.LENGTH_SHORT).show();
                } else {

                    patientService.getPatientById(id, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot datasnapshot : snapshot.getChildren()) {

                                Patient patient2 = datasnapshot.getValue(Patient.class);
                                Log.i("snapshot", snapshot.toString());
                                if (patient2 != null) {

                                    namePatient = patient2.getName();
                                    surnamePatient = patient2.getSurname();
                                    dniPatient = patient2.getDni();
                                    emailPatient = patient2.getEmail();
                                    phonePatient = patient2.getPhone();
                                    passwordPatient = patient2.getPassword();
                                    usernamePatient = patient2.getUsername();
                                    healthInsurance = patient2.getHealthInsurance();
                                    photoPatient = patient2.getPhoto();
                                }
                                Log.i("Entered Password", enterPassword);
                                Log.i("Stored Password", passwordPatient);
                                // Check if the entered password matches the stored password
                                if (BCrypt.checkpw(enterPassword, passwordPatient)) {

                                    // Check if the new password and repeated password match
                                    if (newPassword.equals(repitePassword)) {

                                        // Asignamos los valores introducidos
                                        patient.setName(namePatient);
                                        patient.setSurname(surnamePatient);
                                        patient.setDni(dniPatient);
                                        patient.setHealthInsurance(healthInsurance);
                                        patient.setEmail(emailPatient);
                                        patient.setPhone(phonePatient);
                                        patient.setUsername(usernamePatient);
                                        String encryptPassword = BCrypt.hashpw(etNewPassword.getText().toString(), BCrypt.gensalt(5));
                                        patient.setPassword(encryptPassword);
                                        patient.setRole(role);
                                        patient.setPhoto(photoPatient);
                                        patient.setId(id);

                                        patientService.updatePatient(patient);
                                        // Display a success message
                                        Log.i("id password", "Password updated successfully");
                                        Toast.makeText(ChangePassword.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                        Intent intentMenuActivity = new Intent(ChangePassword.this, MenuActivity.class);
                                        startActivity(intentMenuActivity);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(ChangePassword.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("Firebase", "Error en la lectura en la base de datos.", error.toException());
                        }
                    });
                }
            }
        });

        btnBackChanger.setOnClickListener(v -> {

            Intent viewMainActivity = new Intent(this, MenuActivity.class);
            startActivity(viewMainActivity);
            finish();

        });
    }

    // After password update logic, navigate to the MenuActivity
    private boolean checkEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }


}