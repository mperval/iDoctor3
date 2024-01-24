package es.iescarrillo.idoctor3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.models.Timetable;


public class AdapterAppointment extends ArrayAdapter<Appointment> {

    private Context context;
    private List<Appointment> appointments;

    public AdapterAppointment(Context context, List<Appointment> appointments){
        super(context, 0, appointments);
        this.context=context;
        this.appointments = appointments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Appointment appointment = getItem(position);

        if (convertView == null) {
            //inflo los item del listview
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_appointment, parent, false);
        }

        TextView appDate = convertView.findViewById(R.id.tvDateApp);
        TextView appTime = convertView.findViewById(R.id.tvTimeApp);
        Switch appActive = convertView.findViewById(R.id.switchActiveApp);


        if (appointment != null) {
            //introduzco los valores

            appDate.setText("Date: "+appointment.getAppointmentDate());
            appTime.setText("Time: "+appointment.getAppointmentTime());
            appActive.setChecked(appointment != null && appointment.getActive() != null && appointment.getActive());

        }

        return convertView;
    }
}
