package es.iescarrillo.idoctor3.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Patient;
import es.iescarrillo.idoctor3.services.PatientService;
import es.iescarrillo.idoctor3.services.ProfessionalService;

public class PatientRegisterActivity extends AppCompatActivity {

    private EditText etNamePatient, etSurnamePatient, etUsernamePatient, etPasswordPatient, etDNIPatient, etEmailPatient,
            etPhonePatient;
    private ImageView imgPatient;
    private Switch switchHealthInsurance;
    private Button btnBackPatientRegister, btnPatientRegister;
    private PatientService patientService;
    private ProfessionalService professionalService;
    private Patient patient;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;

    private StorageReference storageReference;
    private String storage_path = "Images/*";

    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;

    private Uri image_url;
    private String photo = "photo";
    private String idd = "0";
    private String getPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        // Obtenemos los componentes de la vista
        getComponentFromView();

        //Inicializamos los servicios
        patientService = new PatientService(getApplicationContext());
        professionalService = new ProfessionalService(getApplicationContext());

        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        patient = new Patient();

        btnPatientRegister.setOnClickListener(v -> {

            String namePatient = etNamePatient.getText().toString().trim();
            String surnamePatient = etSurnamePatient.getText().toString().trim();
            String usernamePatient = etUsernamePatient.getText().toString().trim();
            String passwordPatient = etPasswordPatient.getText().toString().trim();
            String dniPatient = etDNIPatient.getText().toString().trim();
            String emailPatient = etEmailPatient.getText().toString().trim();
            String phonePatient = etPhonePatient.getText().toString().trim();
            Boolean healthInsurance = switchHealthInsurance.isChecked();

            if(namePatient.isEmpty()||surnamePatient.isEmpty()||usernamePatient.isEmpty()||passwordPatient.isEmpty()||dniPatient.isEmpty()||
            phonePatient.isEmpty()||emailPatient.isEmpty()){
                Toast.makeText(PatientRegisterActivity.this, "All fields must be completed", Toast.LENGTH_SHORT).show();

            }else{
                patientService.getPatientByDni(dniPatient, new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshotDNI) {

                        if(snapshotDNI.exists()){
                            Toast.makeText(PatientRegisterActivity.this, "DNI already exists", Toast.LENGTH_SHORT).show();
                        }else{
                            patientService.getPatientByUsername(usernamePatient, new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotUsername) {

                                    if (snapshotUsername.exists()){
                                        Toast.makeText(PatientRegisterActivity.this, "Username already in use", Toast.LENGTH_SHORT).show();
                                    }else{

                                        professionalService.getProfessionalByUsername(usernamePatient, new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshotUsername.exists()) {
                                                        Toast.makeText(PatientRegisterActivity.this, "Usurname already exists", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        patient.setName(namePatient);
                                                        patient.setSurname(surnamePatient);
                                                        patient.setUsername(usernamePatient);

                                                        String encryptPassword = BCrypt.hashpw(passwordPatient, BCrypt.gensalt(4));
                                                        patient.setPassword(encryptPassword);

                                                        patient.setDni(dniPatient);
                                                        patient.setEmail(emailPatient);
                                                        patient.setPhone(phonePatient);
                                                        patient.setHealthInsurance(healthInsurance);
                                                        patient.setRole("Patient");
                                                        patient.setPhoto(getPhoto);

                                                        if (image_url == null) {

                                                            Toast.makeText(PatientRegisterActivity.this, "You must register a photo", Toast.LENGTH_LONG).show();

                                                        }else{

                                                            patientService.insertPatient(patient);

                                                            subirPhoto(image_url, patient.getId());

                                                            Toast.makeText(PatientRegisterActivity.this, "Patient registered", Toast.LENGTH_LONG).show();

                                                            Intent intentMain = new Intent(PatientRegisterActivity.this, LoginActivity.class);
                                                            startActivity(intentMain);
                                                            finish();

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e("error Collegiate Number", error.getDetails());
                                                }
                                            });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("error Username", error.getDetails());
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error Dni", error.getDetails());
                    }
                });
            }
        });

        btnBackPatientRegister.setOnClickListener(v -> {
            Intent patientRegister = new Intent(this, LoginActivity.class);
            startActivity(patientRegister);
            finish();
        });

        imgPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

    }

    private void uploadPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");

        startActivityForResult(i, COD_SEL_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == COD_SEL_IMAGE) {
                imgPatient.setImageURI(data.getData());
                image_url = data.getData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subirPhoto(Uri image_url, String id) {
        String rute_storage_photo = storage_path + "" + photo + id;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                if (uriTask.isSuccessful()) {
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("photo", download_uri);
                            getPhoto = download_uri;
                            mfirestore.collection("Images").document(idd).update(map);
                            Toast.makeText(PatientRegisterActivity.this, "Updated photo", Toast.LENGTH_SHORT).show();

                            patient.setPhoto(getPhoto);
                            patientService.updatePatient(patient);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PatientRegisterActivity.this, "Error loading photo", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void getComponentFromView() {
        etNamePatient = findViewById(R.id.etNamePatient);
        etSurnamePatient = findViewById(R.id.etSurnamePatient);
        etUsernamePatient = findViewById(R.id.etUsernamePatient);
        etPasswordPatient = findViewById(R.id.etPasswordPatient);
        etDNIPatient = findViewById(R.id.etDNIPatient);
        etEmailPatient = findViewById(R.id.etEmailPatient);
        etPhonePatient = findViewById(R.id.etPhonePatient);
        switchHealthInsurance = findViewById(R.id.switchHealthInsurance);
        btnBackPatientRegister = findViewById(R.id.btnBackPatientRegister);
        btnPatientRegister = findViewById(R.id.btnPatientRegister);
        imgPatient = findViewById(R.id.imgPatientRegister);
    }


}