package es.iescarrillo.idoctor3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Professional;

public class AdapterProfessional extends ArrayAdapter<Professional> {

    private List<Professional> professionals;
    private Picasso picasso;

    public AdapterProfessional(@NonNull Context context, List<Professional> professionals) {
        super(context, 0, professionals);
        this.professionals = professionals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Professional professional = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_professional, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.imagenViewItemProf);
        TextView name = convertView.findViewById(R.id.tvNameItemProf);
        TextView speciality = convertView.findViewById(R.id.tvSpecialityItemProf);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBarItemProf);

        if (professional != null) {

            picasso.get().load(professional.getPhoto()).into(imageView);
            name.setText("Name: " + professional.getName());
            speciality.setText("Speciality: " + professional.getSpeciality());

            if(professional.getStars()==null){
                ratingBar.setRating(0);
            }else{
                double starsValue = professional.getStars().doubleValue();
                float rating = (float) starsValue;

                ratingBar.setRating(rating);
            }
        }

        return convertView;
    }
}
