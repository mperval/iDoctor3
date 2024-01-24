package es.iescarrillo.idoctor3.details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.activities.AssessmentActivity;
import es.iescarrillo.idoctor3.activities.LoginActivity;
import es.iescarrillo.idoctor3.activities.MenuActivity;
import es.iescarrillo.idoctor3.activities.PatientFragment;
import es.iescarrillo.idoctor3.listView.LvConsultationProfessionalPatient;
import es.iescarrillo.idoctor3.listView.LvProfessional;
import es.iescarrillo.idoctor3.models.Professional;

public class DetailsProfessional extends AppCompatActivity {
    private EditText name, surname, description, speciality, assessments;
    ImageView imgProfessional;
    Button back, btnConsultation, btnAssessment;
    private RatingBar stars;
    private Professional receivedProfessional;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_professional);
        //inicializacion de los editText
        name = findViewById(R.id.etNameProfessionalDetails);
        stars = findViewById(R.id.ratingBarDetailsProf);
        surname = findViewById(R.id.etSurnameProfessionalDetails);
        description = findViewById(R.id.etDescriptionDetails);
        speciality = findViewById(R.id.etSpecialityDetails);
        assessments = findViewById(R.id.etAssessmentsDetails);
        imgProfessional = findViewById(R.id.imgProfessional);
        back = findViewById(R.id.btnBack);
        btnConsultation = findViewById(R.id.btnConsultation);
        btnAssessment = findViewById(R.id.btnAssessment);

        Intent intent = getIntent();

        if (intent != null) {
            receivedProfessional = (Professional) intent.getSerializableExtra("professional");

            // Accedo a los datos del objeto Professional
            if (receivedProfessional != null) {
                name.setText(receivedProfessional.getName());

                if(receivedProfessional.getStars()==null){
                    stars.setRating(0);
                }else{
                    //parseo de double a float
                    double starsValue = receivedProfessional.getStars().doubleValue();
                    float rating = (float) starsValue;
                    stars.setRating(rating);
                }
                Picasso.get().load(receivedProfessional.getPhoto()).into(imgProfessional);
                surname.setText(receivedProfessional.getSurname());
                speciality.setText(receivedProfessional.getSpeciality());
                description.setText(receivedProfessional.getDescription());
                assessments.setText(String.valueOf(receivedProfessional.getAssessments()));


            }
        }
        back.setOnClickListener(v -> {
            Intent intent1 = new Intent(DetailsProfessional.this, LvProfessional.class);
            startActivity(intent1);
        });
        btnConsultation.setOnClickListener(v -> {
            Intent intent2 = new Intent(DetailsProfessional.this, LvConsultationProfessionalPatient.class);
            //pasar el idProfessional al listView de LvConsultationProfessionalPatient para sacar el las consultas del profesional.
            intent2.putExtra("professionalId", receivedProfessional.getId());
            startActivity(intent2);
        });

        btnAssessment.setOnClickListener(v -> {

            Intent intent1 = new Intent(DetailsProfessional.this, AssessmentActivity.class);
            intent1.putExtra("professionalId", receivedProfessional.getId());
            startActivity(intent1);
        });
    }
}