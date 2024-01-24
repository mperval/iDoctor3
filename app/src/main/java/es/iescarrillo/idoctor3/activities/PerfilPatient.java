package es.iescarrillo.idoctor3.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.squareup.picasso.Picasso;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Patient;
import es.iescarrillo.idoctor3.services.PatientService;

public class PerfilPatient extends AppCompatActivity {
    private EditText etNamePatientPerfil, etSurnamePatientPerfil, etPasswordPatientPerfil, etDNIPatientPerfil, etEmailPatientPerfil, etPhonePatientPerfil;
    private Switch switchHealthInsurancePerfil;
    private Button btnBackPatientRegisterPerfil, btnUpdatePatientPerfil;
    private ImageView imgPatient;
    private Picasso picasso;
    private String storage_path = "Images/*";
    private Patient patient;
    private FirebaseFirestore mfirestore;
    private String idd = "0";
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    private String getPhoto;
    private String photo = "photo";
    private SharedPreferences sharedPreferences;
    private PatientService patientService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_patient);

        //obtengo la id del paciente.
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");
        String photo = sharedPreferences.getString("photo", "");

        patientService = new PatientService(getApplicationContext());
        Log.i("idPatient", id);

        patient = new Patient();

        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        etNamePatientPerfil = findViewById(R.id.etNamePatientPerfil);
        etSurnamePatientPerfil = findViewById(R.id.etSurnamePatientPerfil);
        etDNIPatientPerfil = findViewById(R.id.etDNIPatientPerfil);
        etEmailPatientPerfil = findViewById(R.id.etEmailPatientPerfil);
        etPhonePatientPerfil = findViewById(R.id.etPhonePatientPerfil);
        switchHealthInsurancePerfil = findViewById(R.id.switchHealthInsurancePerfil);
        btnBackPatientRegisterPerfil = findViewById(R.id.btnBackPatientPerfil);
        btnUpdatePatientPerfil = findViewById(R.id.btnUpdatePatientPerfil);
        imgPatient = findViewById(R.id.imgPatientPerfil);

        Picasso.get().load(photo).into(imgPatient);

        if (patientService != null) {
            patientService.getPatientById(id, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot datasnapshot : snapshot.getChildren()){

                        patient = datasnapshot.getValue(Patient.class);
                        Log.i("snapshot", snapshot.toString());

                        if(patient!=null){

                            etNamePatientPerfil.setText(patient.getName());
                            etSurnamePatientPerfil.setText(patient.getSurname());
                            etDNIPatientPerfil.setText(patient.getDni());
                            etEmailPatientPerfil.setText(patient.getEmail());
                            etPhonePatientPerfil.setText(patient.getPhone());
                            switchHealthInsurancePerfil.setChecked(patient.getHealthInsurance());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Firebase", "Error en la lectura en la base de datos.", error.toException());
                }
            });
        }

        btnUpdatePatientPerfil.setOnClickListener(v -> {
            String namePatient = etNamePatientPerfil.getText().toString().trim();
            String surnamePatient = etSurnamePatientPerfil.getText().toString().trim();
            String dniPatient = etDNIPatientPerfil.getText().toString().trim();
            String emailPatient = etEmailPatientPerfil.getText().toString().trim();
            String phonePatient = etPhonePatientPerfil.getText().toString().trim();
            Boolean healthInsurance = switchHealthInsurancePerfil.isChecked();

            if(namePatient.isEmpty()||surnamePatient.isEmpty() ||dniPatient.isEmpty() || phonePatient.isEmpty()||emailPatient.isEmpty()){
                Toast.makeText(PerfilPatient.this, "All fields must be completed", Toast.LENGTH_SHORT).show();
                }else {
                    patient.setName(namePatient);
                    patient.setSurname(surnamePatient);
                    patient.setDni(dniPatient);
                    patient.setHealthInsurance(healthInsurance);
                    patient.setEmail(emailPatient);
                    patient.setPhone(phonePatient);
                    patient.setUsername(patient.getUsername());
                    patient.setPassword(patient.getPassword());
                    patient.setRole(patient.getRole());
                    patient.setId(id);

                    if (image_url == null) {
                        patient.setPhoto(photo);
                        patientService.updatePatient(patient);

                        Toast.makeText(PerfilPatient.this, "Patient changed", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MenuActivity.class);
                        startActivity(intent);
                    }else {
                        patient.setPhoto(getPhoto);
                        patientService.updatePatient(patient);
                        subirPhoto(image_url, id);
                        Intent intent = new Intent(this, MenuActivity.class);
                        startActivity(intent);
                        Toast.makeText(PerfilPatient.this, "Updated profile", Toast.LENGTH_SHORT).show();
                    }
                }
        });
        btnBackPatientRegisterPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
        });

        imgPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones al hacer clic en el ImageView
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

    private void subirPhoto(Uri image_url, String idd) {
        String rute_storage_photo = storage_path + "" + photo + idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("photo", download_uri);
                            getPhoto = download_uri;
                            //actualiza el documento en la bbdd asociandole el id
                            mfirestore.collection("Images").document(idd).update(map);
                            Toast.makeText(PerfilPatient.this, "Updated photo", Toast.LENGTH_SHORT).show();

                            patient.setPhoto(getPhoto);
                            patientService.updatePatient(patient);

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PerfilPatient.this, "Error loading photo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}