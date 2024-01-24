package es.iescarrillo.idoctor3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterSpinnerDayOfWeek extends ArrayAdapter<String> {

    private LayoutInflater inflater;

    public AdapterSpinnerDayOfWeek(Context context, int resource){
        super(context, resource);
        this.inflater = LayoutInflater.from(context);

        // Agregar opciones al Spinner
        add("Monday");
        add("Tuesday");
        add("Wednesday");
        add("Thursday");
        add("Friday");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        TextView textView = view.findViewById(android.R.id.text1);

        // Mostrar las opciones de filtro en el Spinner
        String filterOption = getItem(position);
        textView.setText(filterOption);

        return view;
    }
}
