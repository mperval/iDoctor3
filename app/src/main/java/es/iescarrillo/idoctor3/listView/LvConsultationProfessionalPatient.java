package es.iescarrillo.idoctor3.listView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.activities.ConsultationRegisterActivity;
import es.iescarrillo.idoctor3.activities.MenuActivity;
import es.iescarrillo.idoctor3.adapters.AdapterConsultation;
import es.iescarrillo.idoctor3.details.DetailsConsultation;
import es.iescarrillo.idoctor3.details.DetailsConsultationPatient;
import es.iescarrillo.idoctor3.details.DetailsProfessional;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.services.ConsultationService;

public class LvConsultationProfessionalPatient extends AppCompatActivity {
    private Button btnBackLVCons;
    private AdapterConsultation adapterConsultation;
    private ListView lvConsultation;
    private String id, idConsultation;
    private SharedPreferences sharedPreferences;
    private ConsultationService consultationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lv_consultation_professional_patient);

        Intent intent = getIntent();
        if(intent != null){

            id = intent.getStringExtra("professionalId");

            Log.i("professionalId", id);

            consultationService = new ConsultationService(getApplicationContext());

            //llamo al metodo
            getComponentFromView();

            //inicializamos la lista de consultas que le vamos a pasar a nuestro adaptador
            ArrayList<Consultation> consultations = new ArrayList<>();

            //anadimos la listener que estara en continua ejecucion comprobando si hay cambios
            consultationService.getConsultationByProfessionalId(id, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //limpiamos los datos de la lista, sino se duplican
                    consultations.clear();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        idConsultation = snapshot.getKey(); // Utiliza getKey para obtener el ID del nodo
                        Log.i("idConsultation", idConsultation);

                        Consultation consultation = snapshot.getValue(Consultation.class);
                        consultations.add(consultation);
                    }

                    //una vez los datos anadidos a nuestra lista, se la pasamos al adaptador
                    adapterConsultation = new AdapterConsultation(getApplicationContext(), consultations);
                    lvConsultation.setAdapter(adapterConsultation);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //maneja erroes de lectura de la base de datos si es necesario
                    Log.w("Firebase", "Error en la lectura de la BBDDD");
                }
            });

            lvConsultation.setOnItemClickListener(((parent, view, position, idd) -> {
                Consultation selectedConsultation = (Consultation) parent.getItemAtPosition(position);

                Intent intent1 = new Intent(this, DetailsConsultationPatient.class);
                intent1.putExtra("professionalId", selectedConsultation.getProfessionalId());
                intent1.putExtra("ConsultationId", selectedConsultation.getId());
                startActivity(intent1);
            }));

            btnBackLVCons.setOnClickListener(v-> {

                Intent intentMenu = new Intent(LvConsultationProfessionalPatient.this, LvProfessional.class);
                startActivity(intentMenu);
                finish();

            });
        }
    }

    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        btnBackLVCons = findViewById(R.id.btnBackLVConsP);
        lvConsultation = findViewById(R.id.lvConsultationP);

    }
}