package es.iescarrillo.idoctor3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.details.DetailsProfessional;
import es.iescarrillo.idoctor3.listView.LvProfessional;
import es.iescarrillo.idoctor3.models.Assessment;
import es.iescarrillo.idoctor3.models.Evaluation;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.models.Report;
import es.iescarrillo.idoctor3.services.AssessmentService;
import es.iescarrillo.idoctor3.services.EvaluationService;
import es.iescarrillo.idoctor3.services.ProfessionalService;

public class AssessmentActivity extends AppCompatActivity {

    private RatingBar ratingBarDetailsAssess;
    private EditText  etDescriptionAss, etTitleAss;
    private Button btnBackAssess, btnAddAssess;
    private double stars = 0;
    private AssessmentService assessmentService;
    private ProfessionalService professionalService;
    private Assessment assessment;
    private SharedPreferences sharedPreferences;
    private Integer assessments;
    private Professional professional;
    private String professionalId, usurnameProfessional, collegiateNumber, description, name, password, photo, role , speciality, surname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        getComponentFromView();

        professional = new Professional();
        assessment = new Assessment();

        //Inicializamos el servicio
        assessmentService = new AssessmentService(getApplicationContext());
        professionalService = new ProfessionalService(getApplicationContext());

        Intent intent = getIntent();
        professionalId = intent.getStringExtra("professionalId");

        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Establecer un listener para escuchar cambios en el RatingBar
        ratingBarDetailsAssess.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                //seteo el valor
                stars = rating;
                Log.i("stars", String.valueOf(stars));
                Toast.makeText(AssessmentActivity.this, "Valor seleccionado: " + rating, Toast.LENGTH_SHORT).show();
            }
        });


        btnAddAssess.setOnClickListener(v -> {

            // Obtener los datos ingresados por el usuario
            String descripAss = etDescriptionAss.getText().toString().trim();
            String titleass = etTitleAss.getText().toString().trim();
            String stars2 = String.valueOf(stars);

                if (descripAss.isEmpty() || titleass.isEmpty() || stars2.isEmpty()) {
                    Toast.makeText(AssessmentActivity.this, "All fields must be completed", Toast.LENGTH_SHORT).show();

                }else {

                    assessment.setAssessmentDateTime(LocalDateTime.now());
                    assessment.setDescription(descripAss);
                    assessment.setStars(stars);
                    assessment.setProfessionalId(professionalId);
                    assessment.setUsername(username);

                    assessmentService.insertAssessment(assessment);

                    Toast.makeText(this, "Assessment Registered", Toast.LENGTH_SHORT).show();

                    professionalService.getProfessionalByAssessmentId(professionalId,  new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                professionalId = snapshot.getKey(); // Utiliza getKey para obtener el ID del nodo
                                Log.i("id del proff", professionalId);

                                if (snapshot.exists()) {
                                    //saco datos del report
                                    Professional professional = snapshot.getValue(Professional.class);
                                    usurnameProfessional = professional.getName();
                                    assessments = professional.getAssessments() + 1;
                                    collegiateNumber = professional.getCollegiateNumber();
                                    description = professional.getDescription();
                                    name = professional.getName();
                                    password = professional.getPassword();
                                    photo = professional.getPhoto();
                                    role = professional.getRole();
                                    speciality = professional.getSpeciality();
                                    surname = professional.getSurname();


                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //maneja erroes de lectura de la base de datos si es necesario
                            Log.w("Firebase", "Error en la lectura de la BBDDD");
                        }
                    });

                    // Asignamos los valores introducidos
                    /*
                    professional.setName(name);
                    professional.setSurname(surname);
                    professional.setCollegiateNumber(collegiateNumber);
                    professional.setDescription(description);
                    professional.setSpeciality(speciality);
                    professional.setPassword(password);
                    professional.setRole(role);
                    professional.setAssessments(assessments);
                    professional.setStars(stars);
                    professional.setUsername(usurnameProfessional);
                    professional.setId(professionalId);
                    professional.setPhoto(photo);

                    //professionalService.updateProfessional(professional);
                       */


                    Intent intentConsultations = new Intent(this, MenuActivity.class);
                    startActivity(intentConsultations);
                    finish();

                }



        });

        btnBackAssess.setOnClickListener(v -> {
            Intent intent1 = new Intent(AssessmentActivity.this, DetailsProfessional.class);
            startActivity(intent1);
        });





    }

    // Método donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        etDescriptionAss = findViewById(R.id.etDescriptionAss);
        etTitleAss = findViewById(R.id.etTitleAss);
        btnBackAssess = findViewById(R.id.btnBackAssess);
        btnAddAssess = findViewById(R.id.btnAddAssess);
        ratingBarDetailsAssess = findViewById(R.id.ratingBarDetailsAssess);

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