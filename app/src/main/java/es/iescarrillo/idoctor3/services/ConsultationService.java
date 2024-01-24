package es.iescarrillo.idoctor3.services;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.Patient;

public class ConsultationService {

    // Referencia de la base de datos
    private DatabaseReference database;

    //Constructor vacio
    public ConsultationService (Context context){
        // Inicializamos la basde de datos y su referencia al nodo
        database = FirebaseDatabase.getInstance().getReference().child("consultation");
    }

    public void insertConsultation(Consultation consultation){
        //Utiliza push() para obtener una clave única y agregar el mensaje
        DatabaseReference databaseReference = database.push();
        consultation.setId(databaseReference.getKey()); //Asigna el ID generado automáticamente

        //Ahora, utiliza setValue() en la nueva referencia para agregar el nuevo mensaje
        databaseReference.setValue(consultation);
    }

    public void updateConsultation(Consultation consultation){
        database.child(consultation.getId()).setValue(consultation);
    }

    public void deleteConsultation(String id){
        database.child(id).removeValue();
    }

    public void getConsultationOrderByCity(ValueEventListener listener){
        Query query = database.orderByChild("city");
        query.addValueEventListener(listener);
    }

    public void getConsultationByProfId(String professionalId, ValueEventListener listener){
        Query query = database.orderByChild("professionalId").equalTo(professionalId);
        query.addValueEventListener(listener);
    }



    public void getConsultationById(String id, ValueEventListener listener){
        Query query = database.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(listener);
    }

    public void getConsultationByProfessionalId(String professionalId, ValueEventListener listener){
        Query query = database.orderByChild("professionalId").equalTo(professionalId);
        query.addListenerForSingleValueEvent(listener);
    }

}
