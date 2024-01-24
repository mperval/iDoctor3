package es.iescarrillo.idoctor3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.ObjetAppointment;
import es.iescarrillo.idoctor3.models.Professional;

public class AdapterObjectAppointment extends ArrayAdapter<ObjetAppointment> {
    private List<ObjetAppointment> objetAppointments;

    public AdapterObjectAppointment(@NonNull Context context, List<ObjetAppointment> objetAppointments) {
        super(context, 0, objetAppointments);
        this.objetAppointments = objetAppointments;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObjetAppointment objetAppointment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_appointment_patient, parent, false);
        }
        TextView tvItemDateAppointment = convertView.findViewById(R.id.tvItemDateAppointment);
        TextView tvItemTimeAppointment = convertView.findViewById(R.id.tvItemTimeAppointment);
        TextView tvNameProfessional = convertView.findViewById(R.id.tvNameProfessional);
        TextView tvSpecialityProfessional = convertView.findViewById(R.id.tvSpecialityProfessional);
        TextView tvAddressConsultation = convertView.findViewById(R.id.tvAddressConsultation);
        TextView tvCityConsultation = convertView.findViewById(R.id.tvCityConsultation);

        if (objetAppointment != null) {

            tvItemDateAppointment.setText("Date: " + objetAppointment.getAppointmentDate());
            tvItemTimeAppointment.setText("Time: " + objetAppointment.getAppointmentTime());
            tvNameProfessional.setText("Name Professional: " + objetAppointment.getDoctorName());
            tvSpecialityProfessional.setText("Speciality: " + objetAppointment.getSpeciality());
            tvAddressConsultation.setText("Address: " + objetAppointment.getAddress());
            tvCityConsultation.setText("City: " + objetAppointment.getCity());
        }

        return convertView;
    }

}
