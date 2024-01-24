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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
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

import org.apache.commons.lang3.ArrayUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Patient;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.services.PatientService;
import es.iescarrillo.idoctor3.services.ProfessionalService;

public class PerfilProfessional extends AppCompatActivity {
// TODO mostrar imagen del profesional
    private ImageView imgPerfilProfessional;
    private RatingBar ratingBarProfile;
    private EditText etNamePerfilProfessional, etSurnamePerfilProfessional, etPerfilCollegiateNumber,
            etPerfilDescription, etAssessmentsProfile;
    private Spinner spSpecialityPerfil;

    private Button btnBackMenu, btnChangeProfessional;
    private ProfessionalService professionalService;
    private PatientService patientService;
    private Professional professional;
    private double stars;
    private int assessments;
    private String role, password;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String storage_path = "Images/*";
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private SharedPreferences sharedPreferences;
    private Uri image_url;
    private String photo = "photo";
    private String getPhoto;
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_professional);

    // Inicializamos los servicios
    professionalService = new ProfessionalService(getApplicationContext());
    patientService = new PatientService(getApplicationContext());

    mfirestore = FirebaseFirestore.getInstance();
    mAuth = FirebaseAuth.getInstance();
    storageReference = FirebaseStorage.getInstance().getReference();

    // Obtenemos los componentes de la vista
    getComponentFromView();

    sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
    String id = sharedPreferences.getString("id", "");
    String photo = sharedPreferences.getString("photo", "");
    String username = sharedPreferences.getString("username", "");
    Log.i("id pro", id);

    professional = new Professional();
    Intent intent = getIntent();

    // Usar Picasso para cargar la imagen en el ImageView
    picasso.get().load(photo).into(imgPerfilProfessional);

        professionalService.getProfessionalById(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()){

                    Professional professional = datasnapshot.getValue(Professional.class);
                    Log.i("snapshot", snapshot.toString());
                    if(professional!=null){

                        String[] items = {"General", "Fisioterapia", "Odontología"};
                        // Obtén la especialidad del profesional
                        String speciality = professional.getSpeciality();

                        // Encuentra el índice de la especialidad en el array items
                        int position  = ArrayUtils.indexOf(items, speciality);

                        etNamePerfilProfessional.setText(professional.getName());
                        etSurnamePerfilProfessional.setText(professional.getSurname());
                        etPerfilCollegiateNumber.setText(professional.getCollegiateNumber());
                        spSpecialityPerfil.setSelection(position);
                        etPerfilDescription.setText(professional.getDescription());
                        etAssessmentsProfile.setText(String.valueOf(professional.getAssessments()));
                        // Obtiene el valor de las estrellas del profesional
                        Double stars = professional.getStars();
                        float rating = Float.parseFloat(String.valueOf(stars));
                        ratingBarProfile.setRating(rating);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en la lectura en la base de datos.", error.toException());
            }
        });

        // Funcionales de los botones
        btnChangeProfessional.setOnClickListener(v -> {

        // Obtener el valor seleccionado del Spinner
        String selectedSpeciality = spSpecialityPerfil.getSelectedItem().toString();

        // Obtener los datos ingresados por el usuario
        String nameProfessional = etNamePerfilProfessional.getText().toString().trim();
        String surnameProfessional = etSurnamePerfilProfessional.getText().toString().trim();
        String collegiateNumber = etPerfilCollegiateNumber.getText().toString().trim();
        String description = etPerfilDescription.getText().toString().trim();
        List<Professional> professionales = new ArrayList<>();

        // Validar que se hayan completado todos los campos
        if (nameProfessional.isEmpty() || surnameProfessional.isEmpty() || description.isEmpty()) {
            Toast.makeText(PerfilProfessional.this, "All fields must be completed", Toast.LENGTH_SHORT).show();

        }else {
            professionalService.getProfessionalById(id, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                            // Convierte cada nodo de la base de datos a un objeto Superhero
                            Professional snapProfessional = snapshot2.getValue(Professional.class);
                            role = snapProfessional.getRole();
                            stars = snapProfessional.getStars();
                            assessments = snapProfessional.getAssessments();
                            password = snapProfessional.getPassword();
                        }
                            // Asignamos los valores introducidos
                            professional.setName(nameProfessional);
                            professional.setSurname(surnameProfessional);
                            professional.setCollegiateNumber(collegiateNumber);
                            professional.setDescription(description);
                            professional.setSpeciality(selectedSpeciality);
                            professional.setPassword(password);
                            professional.setRole(role);
                            professional.setAssessments(assessments);
                            Log.i("setAssessments", String.valueOf(assessments));
                            professional.setStars(stars);
                            professional.setUsername(username);
                            professional.setId(id);

                        if (image_url == null) {

                            professional.setPhoto(photo);
                            professionalService.updateProfessional(professional);

                            Toast.makeText(PerfilProfessional.this, "Professional changed", Toast.LENGTH_LONG).show();

                            Intent intentMain = new Intent(PerfilProfessional.this, LoginActivity.class);
                            startActivity(intentMain);
                            finish();

                        } else {

                            professional.setPhoto(getPhoto);
                            professionalService.updateProfessional(professional);

                            subirPhoto(image_url, id);

                            Toast.makeText(PerfilProfessional.this, "Professional changed", Toast.LENGTH_LONG).show();

                            Intent intentMain = new Intent(PerfilProfessional.this, LoginActivity.class);
                            startActivity(intentMain);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error", error.getDetails());
                }
            });
        }
    });

    // Funcionales de los botones
        btnBackMenu.setOnClickListener(v -> {

        Intent viewMenuIntent = new Intent(this, MenuActivity.class);
        startActivity(viewMenuIntent);
        finish();

    });

        imgPerfilProfessional.setOnClickListener(new View.OnClickListener() {
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
                imgPerfilProfessional.setImageURI(data.getData());
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
                            Toast.makeText(PerfilProfessional.this, "Updated photo", Toast.LENGTH_SHORT).show();

                            professional.setPhoto(getPhoto);
                            professionalService.updateProfessional(professional);

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PerfilProfessional.this, "Error loading photo", Toast.LENGTH_SHORT).show();
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


    // Método donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        imgPerfilProfessional = findViewById(R.id.imgPerfilProfessional);
        etNamePerfilProfessional = findViewById(R.id.etNamePerfilProfessional);
        etSurnamePerfilProfessional = findViewById(R.id.etSurnamePerfilProfessional);
        etPerfilCollegiateNumber = findViewById(R.id.etPerfilCollegiateNumber);
        spSpecialityPerfil = findViewById(R.id.spSpecialityPerfil);
        etPerfilDescription = findViewById(R.id.etPerfilDescription);
        etAssessmentsProfile = findViewById(R.id.etAssessmentsProfile);
        btnBackMenu = findViewById(R.id.btnBackMenu);
        btnChangeProfessional = findViewById(R.id.btnChangeProfessional);
        ratingBarProfile = findViewById(R.id.ratingBarProfile);

        // Define un arreglo de elementos
        String[] items = {"General", "Fisioterapia", "Odontología"};

        // Crea un ArrayAdapter usando el arreglo de elementos y un diseno de Spinner predeterminado
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);

        // Especifica el disenno para el menu desplegable
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Establece el adaptador en el Spinner
        spSpecialityPerfil.setAdapter(adapter);
    }
}