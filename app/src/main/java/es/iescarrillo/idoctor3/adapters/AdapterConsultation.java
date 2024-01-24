package es.iescarrillo.idoctor3.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Consultation;

public class AdapterConsultation extends ArrayAdapter<Consultation> {

    private Context context;
    private ArrayList<Consultation> consultations;
    public AdapterConsultation(Context context, ArrayList<Consultation> consultation) {

        super(context, 0, consultation);
        this.context = context;
        this.consultations = consultations;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Consultation consultation = getItem(position);

        if (convertView == null) {
            //inflo los item del listview
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_consultation, parent, false);
        }
        TextView tvAddress = convertView.findViewById(R.id.tvAddress);
        TextView tvCity = convertView.findViewById(R.id.tvCity);

        if (consultation != null) {
            //introduzco los valores
            tvAddress.setText("Address: " +consultation.getAddress());
            tvCity.setText("City: " +consultation.getCity());

        }

        return convertView;
    }
}
