package es.iescarrillo.idoctor3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.Timetable;

public class AdapterTimetables extends ArrayAdapter<Timetable> {

    private Context context;
    private List<Timetable> timetables;

    public AdapterTimetables(Context context, List<Timetable> timetables){
        super(context, 0, timetables);
        this.context=context;
        this.timetables = timetables;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Timetable timetable = getItem(position);

        if (convertView == null) {
            //inflo los item del listview
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_timetable, parent, false);
        }
        TextView tvDayOfWeek = convertView.findViewById(R.id.tvDayOfWeek);
        TextView tvStartTime = convertView.findViewById(R.id.tvStartTime);
        TextView tvEndTime = convertView.findViewById(R.id.tvEndTime);

        if (timetable != null) {
            //introduzco los valores
            tvDayOfWeek.setText("Day of Week: " +timetable.getDayOfWeek());
            tvStartTime.setText("Start Time: " +timetable.getStartTime().toString());
            tvEndTime.setText("End Time: " +timetable.getEndTime().toString());

        }

        return convertView;
    }
}
