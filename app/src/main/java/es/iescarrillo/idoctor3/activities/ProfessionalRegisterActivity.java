package es.iescarrillo.idoctor3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import org.mindrot.jbcrypt.BCrypt;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.services.PatientService;
import es.iescarrillo.idoctor3.services.ProfessionalService;

public class   ProfessionalRegisterActivity extends AppCompatActivity {

    private ImageView imgProfessional;
    private EditText etNameProfessional, etSurnameProfessional, etUsurnameProfessional, etPasswordProfessional,
            etCollegiateNumber, etDescription;
    private Spinner spSpeciality;

    private Button btnBack, btnAddProfessional;
    private ProfessionalService professionalService;
    private PatientService patientService;
    private Professional professional;
    private double stars = 0;
    private int assessments = 0;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String storage_path = "Images/*";
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    private String photo = "photo";
    private String getPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_register);

        // Inicializamos los servicios
        professionalService = new ProfessionalService(getApplicationContext());
        patientService = new PatientService(getApplicationContext());

        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Obtenemos los componentes de la vista
        getComponentFromView();

        professional = new Professional();
        Intent intent = getIntent();

        // Funcionales de los botones
        btnAddProfessional.setOnClickListener(v -> {

            // Obtener el valor seleccionado del Spinner
            String selectedSpeciality = spSpeciality.getSelectedItem().toString();

            // Obtener los datos ingresados por el usuario
            String nameProfessional = etNameProfessional.getText().toString().trim();
            String surnameProfessional = etSurnameProfessional.getText().toString().trim();
            String usurnameProfessional = etUsurnameProfessional.getText().toString().trim();
            String passwordProfessional = etPasswordProfessional.getText().toString().trim();
            String collegiateNumber = etCollegiateNumber.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            // Validar que se hayan completado todos los campos
            if (nameProfessional.isEmpty() || surnameProfessional.isEmpty() || usurnameProfessional.isEmpty() ||
                    passwordProfessional.isEmpty() || collegiateNumber.isEmpty() || description.isEmpty()) {
                Toast.makeText(ProfessionalRegisterActivity.this, "All fields must be completed", Toast.LENGTH_SHORT).show();

            }else {

            professionalService.getProfessionalByUsername(usurnameProfessional, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshotUsername) {

                    if (snapshotUsername.exists()) {
                        Toast.makeText(ProfessionalRegisterActivity.this, "Usurname already exists", Toast.LENGTH_SHORT).show();
                    }else {
                        professionalService.getProfessionalByCollegiateNumber(collegiateNumber, new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotCollegiateNumber) {

                            if (snapshotCollegiateNumber.exists()) {
                                Toast.makeText(ProfessionalRegisterActivity.this, "Collegiate Number already exists", Toast.LENGTH_SHORT).show();
                            }else {
                                patientService.getPatientByUsername(usurnameProfessional, new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshotUsername.exists()) {
                                                Toast.makeText(ProfessionalRegisterActivity.this, "Usurname already exists", Toast.LENGTH_SHORT).show();
                                            }else {
                                                // Asignamos los valores introducidos
                                                professional.setName(nameProfessional);
                                                professional.setSurname(surnameProfessional);
                                                professional.setUsername(usurnameProfessional);
                                                String encryptPassword = BCrypt.hashpw(etPasswordProfessional.getText().toString(), BCrypt.gensalt(4));
                                                professional.setPassword(encryptPassword);
                                                professional.setCollegiateNumber(collegiateNumber);
                                                professional.setDescription(description);
                                                professional.setSpeciality(selectedSpeciality);
                                                //seteo a 0 porque lo tienen que poner los clientes
                                                professional.setStars(stars);
                                                professional.setAssessments(assessments);
                                                professional.setRole("Professional");
                                                professional.setPhoto(getPhoto);

                                                if (image_url == null) {

                                                    Toast.makeText(ProfessionalRegisterActivity.this, "You must register a photo", Toast.LENGTH_LONG).show();

                                                }else{

                                                    professionalService.insertProfessional(professional);

                                                    subirPhoto(image_url, professional.getId());

                                                    Toast.makeText(ProfessionalRegisterActivity.this, "Professional registered", Toast.LENGTH_LONG).show();

                                                    Intent intentMain = new Intent(ProfessionalRegisterActivity.this, LoginActivity.class);
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
        });

        // Funcionales de los botones
        btnBack.setOnClickListener(v -> {

            Intent viewMenuIntent = new Intent(this, MenuActivity.class);
            startActivity(viewMenuIntent);
            finish();

        });


        imgProfessional.setOnClickListener(new View.OnClickListener() {
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
        if(resultCode == RESULT_OK){
            if (requestCode == COD_SEL_IMAGE){
                imgProfessional.setImageURI(data.getData());
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
                            mfirestore.collection("Images").document(id).update(map);
                            Toast.makeText(ProfessionalRegisterActivity.this, "Updated photo", Toast.LENGTH_SHORT).show();

                            professional.setPhoto(getPhoto);
                            professionalService.updateProfessional(professional);

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfessionalRegisterActivity.this, "Error loading photo", Toast.LENGTH_SHORT).show();
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


    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        imgProfessional = findViewById(R.id.imgProfessional);
        etNameProfessional = findViewById(R.id.etNameProfessional);
        etSurnameProfessional = findViewById(R.id.etSurnameProfessional);
        etUsurnameProfessional = findViewById(R.id.etUsurnameProfessional);
        etPasswordProfessional = findViewById(R.id.etPasswordProfessional);
        etCollegiateNumber = findViewById(R.id.etCollegiateNumber);
        spSpeciality = findViewById(R.id.spSpeciality);
        etDescription = findViewById(R.id.etDescription);
        btnBack = findViewById(R.id.btnBack);
        btnAddProfessional = findViewById(R.id.btnAddProfessional);

        // Define un arreglo de elementos
        String[] items = {"General", "Fisioterapia", "Odontología"};

        // Crea un ArrayAdapter usando el arreglo de elementos y un diseno de Spinner predeterminado
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);

        // Especifica el disenno para el menu desplegable
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Establece el adaptador en el Spinner
        spSpeciality.setAdapter(adapter);


    }
}