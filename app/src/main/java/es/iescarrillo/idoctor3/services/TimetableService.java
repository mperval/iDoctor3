package es.iescarrillo.idoctor3.services;

import android.content.Context;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.models.Timetable;

public class TimetableService {

    DatabaseReference database;

    public TimetableService (Context context){
        database = FirebaseDatabase.getInstance().getReference().child("timetable");
    }

    public void insertTimetable(Timetable timetable){
        // Utiliza push() para obtener una clave única y agregar el mensaje
        DatabaseReference newReference = database.push();
        timetable.setId(newReference.getKey()); // Asigna el ID generado automáticamente

        // Ahora, utiliza setValue() en la nueva referencia para agregar el nuevo mensaje
        newReference.setValue(timetable);
    }

    public void updateTimetable(Timetable timetable){
        database.child(timetable.getId()).setValue(timetable);
    }

    public void deleteTimetable (String id){database.child(id).removeValue();}


    public void getTimetableByConsultationId(String id, ValueEventListener listener){
        Query query = database.orderByChild("consultationId").equalTo(id);
        query.addValueEventListener(listener);
    }
    public void getTimetableOrderByDay(ValueEventListener listener){
        Query query = database.orderByChild("dayOfWeek");
        query.addListenerForSingleValueEvent(listener);
    }
}
