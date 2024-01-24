package es.iescarrillo.idoctor3.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.adapters.AdapterConsultation;
import es.iescarrillo.idoctor3.details.DetailsAppointmentProf;
import es.iescarrillo.idoctor3.listView.LvConsultations;
import es.iescarrillo.idoctor3.listView.LvConsultationsAppointment;
import es.iescarrillo.idoctor3.listView.LvMyAppointmentConsultation;
import es.iescarrillo.idoctor3.listView.LvTimetable;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.services.ConsultationService;

public class ProfessionalFragment extends Fragment {

    private Button btnConsultation, btnTimetable, btnGenerateAppointment, btnMyAppointments;
    private Button btnEvaluation;
    private SharedPreferences sharedPreferences;
    private String id;

    private ConsultationService consultationService;
    private List<Consultation> consultationList;

    public ProfessionalFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento para el rol Professional
        View view = inflater.inflate(R.layout.fragment_professional, container, false);

        // Obtenemos los componentes de la vista
        getComponentFromView(view);

        consultationService = new ConsultationService(requireContext());
        consultationList = new ArrayList<>();

        //con el requiereContext especifico el contexto
        sharedPreferences = requireContext().getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");

        comprobarConsultas(id);

        btnConsultation.setOnClickListener(v -> {
            Intent intentConsultation = new Intent(requireActivity(), LvConsultations.class);
            startActivity(intentConsultation);
        });

        btnTimetable.setOnClickListener(v -> {

            if(consultationList.size()==0){
                Toast.makeText(requireActivity(), "Requires to insert a Consultation", Toast.LENGTH_LONG).show();

            }else{
                Intent intentTimetable = new Intent(requireActivity(), LvTimetable.class);
                startActivity(intentTimetable);
            }

        });

        btnGenerateAppointment.setOnClickListener(v -> {
            Intent intentGenerateAppointment = new Intent(requireActivity(), LvConsultationsAppointment.class);
            startActivity(intentGenerateAppointment);
        });

        btnMyAppointments.setOnClickListener(v -> {
            Intent intentGenerateAppointment = new Intent(requireActivity(), LvMyAppointmentConsultation.class);
            startActivity(intentGenerateAppointment);
        });


        btnEvaluation.setOnClickListener(v -> {
            Intent intentEvaluation = new Intent(requireActivity(), EvaluationProfessionalActivity.class);
            startActivity(intentEvaluation);
        });

        return view;
    }

    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(View view){
        btnConsultation = view.findViewById(R.id.btnConsultation);
        btnTimetable = view.findViewById(R.id.btnTimetable);
        btnGenerateAppointment = view.findViewById(R.id.btnGenerateAppointment);
        btnMyAppointments = view.findViewById(R.id.btnMyAppointments);
        btnEvaluation = view.findViewById(R.id.btnEvaluation);
    }

    /**
     * Metodo para comprobar si el profesional tiene consultas. Si es null se bloquea el btnTimetable,
     * si no casca al abrir el intent ya que no pueden cargarse los spinnner.
     * @param id
     */
    public void comprobarConsultas(String id){

        consultationService.getConsultationByProfessionalId(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //limpiamos los datos de la lista, sino se duplican
                consultationList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Consultation consultation = snapshot.getValue(Consultation.class);
                    Log.i("consultationLVAppointment", consultation.toString());
                    consultationList.add(consultation);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //maneja erroes de lectura de la base de datos si es necesario
                Log.w("Firebase", "Error en la lectura de la BBDDD");
            }
        });

    }


}
