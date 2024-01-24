package es.iescarrillo.idoctor3.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.security.identity.CipherSuiteNotSupportedException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.listView.LvPatientAppointment;
import es.iescarrillo.idoctor3.listView.LvProfessional;

public class PatientFragment extends Fragment {

    private Button btnProfesionals, btnListAppointments, btnEvalAppoint;
    public PatientFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento para el rol Patient
        View view = inflater.inflate(R.layout.fragment_patient, container, false);
        btnProfesionals = view.findViewById(R.id.btnProfesionals);
        btnListAppointments = view.findViewById(R.id.btnListAppointments);
        btnEvalAppoint = view.findViewById(R.id.btnEvalAppoint);

        btnProfesionals.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LvProfessional.class);
            startActivity(intent);
        });

        btnListAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LvPatientAppointment.class);
            startActivity(intent);
        });

        btnEvalAppoint.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewEvaluation.class);
            startActivity(intent);
        });


        return view;
    }
}

