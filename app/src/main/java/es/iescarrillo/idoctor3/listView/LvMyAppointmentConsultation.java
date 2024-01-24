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
import es.iescarrillo.idoctor3.activities.GenerateAppointmentActivity;
import es.iescarrillo.idoctor3.activities.MenuActivity;
import es.iescarrillo.idoctor3.adapters.AdapterConsultation;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.services.ConsultationService;

public class LvMyAppointmentConsultation extends AppCompatActivity {

    private Button btnBackMyAppConsLv;
    private AdapterConsultation adapterConsultation;
    private ListView lvMyAppCons;
    private SharedPreferences sharedPreferences;
    private String id, consultationId;
    private ConsultationService consultationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lv_my_appointment_consultation);

        consultationService = new ConsultationService(getApplicationContext());
        //recojo el id
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");

        //llamo al metodo
        getComponentFromView();

        //inicializamos la lista de consultas que le vamos a pasar a nuestro adaptador
        ArrayList<Consultation> consultations = new ArrayList<>();

        //Obtenemos la referencial al nodo 'consultation'
        DatabaseReference dbConsultation = FirebaseDatabase.getInstance().getReference().child("consultation");

        //anadimos la listener que estara en continua ejecucion comprobando si hay cambios
        consultationService.getConsultationByProfessionalId(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //limpiamos los datos de la lista, sino se duplican
                consultations.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    consultationId = snapshot.getKey(); // Utiliza getKey para obtener el ID del nodo
                    Log.i("consultationId", consultationId);

                    Consultation consultation = snapshot.getValue(Consultation.class);
                    Log.i("consultationLVAppointment", consultation.toString());
                    consultations.add(consultation);
                }

                //una vez los datos anadidos a nuestra lista, se la pasamos al adaptador
                adapterConsultation = new AdapterConsultation(getApplicationContext(), consultations);
                lvMyAppCons.setAdapter(adapterConsultation);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //maneja erroes de lectura de la base de datos si es necesario
                Log.w("Firebase", "Error en la lectura de la BBDDD");
            }
        });

        // Anade el listener al ListView para manejar clics en los elementos
        lvMyAppCons.setOnItemClickListener((parent, view, position, id) -> {

            Consultation consultation = (Consultation) parent.getItemAtPosition(position);
            Log.i("consultation que pulsa", consultation.getId());

            Intent intent = new Intent(LvMyAppointmentConsultation.this, LvMyAppointmentProf.class);
            intent.putExtra("consultation", consultation);
            Log.i("consultationLVAppointmentID", consultation.getId());
            startActivity(intent);
        });


        btnBackMyAppConsLv.setOnClickListener(v -> {

            Intent intentMenu = new Intent(this, MenuActivity.class);
            startActivity(intentMenu);
            finish();

        });

    }

    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView() {

        btnBackMyAppConsLv = findViewById(R.id.btnBackMyAppConsLv);
        lvMyAppCons = findViewById(R.id.lvMyAppCons);
    }
}
