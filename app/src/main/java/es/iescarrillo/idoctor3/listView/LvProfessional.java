package es.iescarrillo.idoctor3.listView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.iescarrillo.idoctor3.activities.MenuActivity;
import es.iescarrillo.idoctor3.adapters.AdapterProfessional;
import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.adapters.AdapterSpinnerSearch;
import es.iescarrillo.idoctor3.details.DetailsProfessional;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.services.ConsultationService;
import es.iescarrillo.idoctor3.services.ProfessionalService;

public class LvProfessional extends AppCompatActivity {

    private ListView lvPro;
    private Button btnBackLvProfessional;
    private ProfessionalService proService;
    private AdapterProfessional adProfessional;
    private Spinner spinnerSearch;
    private EditText etSearch;
    private AdapterSpinnerSearch adapterSpinnerSearch;
    private List<Professional> professionals;
    private ConsultationService consultationService;
    private List<Professional> filteredProfessionals;
    private List<Consultation> consultations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lv_professional);

        //Inicializo el servicio
        proService = new ProfessionalService(getApplicationContext());
        consultationService = new ConsultationService(getApplicationContext());

        //inicializo el boton.
        btnBackLvProfessional = findViewById(R.id.btnBackLVProf);
        etSearch = findViewById(R.id.etSearchProf);
        spinnerSearch = findViewById(R.id.spinnerProf);
        etSearch = findViewById(R.id.etSearchProf);

        //Cargamos el spinner
        setupFiltersSpinner();

        professionals = new ArrayList<>();
        filteredProfessionals = new ArrayList<>();
        consultations = new ArrayList<>();
        lvPro = findViewById(R.id.lvProfessional);

        proService.getProfessionalsOrderByName(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                professionals.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Professional pro = snapshot.getValue(Professional.class);

                    professionals.add(pro);

                    Log.i("professional", snapshot.toString());
                }
                Log.i("tamanioArray", String.valueOf(professionals.size()));

                adProfessional = new AdapterProfessional(getApplicationContext(), professionals);
                lvPro.setAdapter(adProfessional);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en la lectura en la base de datos.", error.toException());
            }
        });

        /*
        EditText para filtrar los profesionales
         */
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString();

                filterProfessional(searchText); // Llama a un método para filtrar los contactos
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //paso los datos a otra actividad.
        lvPro.setOnItemClickListener((parent, view, position, id) -> {
            Professional professional = (Professional) parent.getItemAtPosition(position);

            Intent intent = new Intent(LvProfessional.this, DetailsProfessional.class);
            intent.putExtra("professional", professional);
            startActivity(intent);
        });

        btnBackLvProfessional.setOnClickListener(v -> {
            Intent viewCourseManagerActivity = new Intent(this, MenuActivity.class);
            startActivity(viewCourseManagerActivity);
            finish();
        });
    }

    // Método para configurar el Spinner con las opciones
    private void setupFiltersSpinner() {
        // Crea una instancia de tu AdapterSpinnerSearch
        adapterSpinnerSearch = new AdapterSpinnerSearch(this, android.R.layout.simple_spinner_item);

        // Establece el adaptador para el Spinner
        spinnerSearch.setAdapter(adapterSpinnerSearch);

    }

    private void filterProfessional(String searchText) {

        if (String.valueOf(spinnerSearch.getSelectedItem()).equals("Name")) {

            Log.i("spinner", String.valueOf(spinnerSearch.getSelectedItem()));

            proService.getProfessionalsOrderByName(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    professionals.clear();
                    filteredProfessionals.clear();


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Professional pro = snapshot.getValue(Professional.class);

                        professionals.add(pro);

                        Log.i("professional", snapshot.toString());
                    }
                    Log.i("tamanioArray", String.valueOf(professionals.size()));

                    for (Professional professional : professionals) {
                        // Puedes ajustar este filtro según tus necesidades, aquí se filtran por name y surname
                        if (professional.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            filteredProfessionals.add(professional);
                        }
                    }

                    adProfessional = new AdapterProfessional(getApplicationContext(), filteredProfessionals);
                    lvPro.setAdapter(adProfessional);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Firebase", "Error en el filtrado por nombre.", error.toException());
                }
            });
        } else if (String.valueOf(spinnerSearch.getSelectedItem()).equals("Speciality")) {

            Log.i("spinner", String.valueOf(spinnerSearch.getSelectedItem()));

            proService.getProfessionalOrderBySpeciality(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    professionals.clear();
                    filteredProfessionals.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Professional pro = snapshot.getValue(Professional.class);

                        professionals.add(pro);

                        Log.i("professional", snapshot.toString());
                    }
                    Log.i("tamanioArray", String.valueOf(professionals.size()));

                    for (Professional professional : professionals) {
                        // Puedes ajustar este filtro según tus necesidades, aquí se filtran por name y surname
                        if (professional.getSpeciality().toLowerCase().contains(searchText.toLowerCase())) {
                            filteredProfessionals.add(professional);
                        }
                    }

                    adProfessional = new AdapterProfessional(getApplicationContext(), filteredProfessionals);
                    lvPro.setAdapter(adProfessional);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Firebase", "Error en el filtrado por nombre.", error.toException());
                }
            });

        } else if (String.valueOf(spinnerSearch.getSelectedItem()).equals("City")) {

            Log.i("spinner", String.valueOf(spinnerSearch.getSelectedItem()));
            Log.i("searchText", searchText);


            consultationService.getConsultationOrderByCity(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    consultations.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        Consultation consultation = dataSnapshot.getValue(Consultation.class);

                        consultations.add(consultation);

                    }

                    proService.getProfessionalsOrderByName(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            professionals.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                Professional pro = snapshot.getValue(Professional.class);

                                professionals.add(pro);

                            }

                            Log.i("profesionals tamanio", String.valueOf(professionals.size()));
                            Log.i("consultations tamanio", String.valueOf(consultations.size()));
                            filteredProfessionals.clear();
                            for (Consultation consultation : consultations) {
                                Log.i("consultations tamanio", String.valueOf(consultations.size()));

                                for (Professional professional : professionals) {

                                    Log.i("profesionals tamanio", String.valueOf(professionals.size()));

                                    if (consultation.getCity().toLowerCase().contains(searchText.toLowerCase()) && consultation.getProfessionalId().equals(professional.getId())) {
                                        if(!filteredProfessionals.contains(professional))
                                            filteredProfessionals.add(professional);

                                        Log.i("profesionals filtered tamanio", String.valueOf(filteredProfessionals.size()));

                                    }

                                }

                            }
                            adProfessional = new AdapterProfessional(getApplicationContext(), filteredProfessionals);
                            lvPro.setAdapter(adProfessional);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("Firebase", "Error en el filtrado por nombre.", error.toException());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Firebase", "Error en la búsqueda de ciudad.", error.toException());
                }
            });

        } else {

        }
    }


}