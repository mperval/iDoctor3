package es.iescarrillo.idoctor3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.iescarrillo.idoctor3.models.Consultation;

public class AdapterSpinnerConsultation extends ArrayAdapter<Consultation> {
    private LayoutInflater inflater;
    private List<Consultation> consultationList;

    public AdapterSpinnerConsultation(Context context, int resource, List<Consultation> consultations){
        super(context, resource, consultations);
        this.inflater = LayoutInflater.from(context);
        this.consultationList = consultations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent){
        View view=inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(consultationList.get(position).getAddress()+" ("+consultationList.get(position).getCity()+")");
        return view;
    }
}
