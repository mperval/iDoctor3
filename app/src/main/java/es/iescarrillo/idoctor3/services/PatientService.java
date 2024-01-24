package es.iescarrillo.idoctor3.services;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.models.Patient;

public class PatientService {

    // Referencia de la base de datos
    private DatabaseReference database;

    //Constructor vacio
    public PatientService (Context context){
        // Inicializamos la basde de datos y su referencia al nodo de superhéroes
        database = FirebaseDatabase.getInstance().getReference().child("patient");
    }

    public void insertPatient(Patient patient){
        //Utiliza push() para obtener una clave única y agregar el mensaje
        DatabaseReference databaseReference = database.push();
        patient.setId(databaseReference.getKey()); //Asigna el ID generado automáticamente

        //Ahora, utiliza setValue() en la nueva referencia para agregar el nuevo mensaje
        databaseReference.setValue(patient);
    }

    public void updatePatient(Patient patient){
        database.child(patient.getId()).setValue(patient);
    }

    public void deletePatient(String id){
        database.child(id).removeValue();
    }

    public void deletePatient(Patient patient){
        database.child(patient.getId()).removeValue();
    }

    public void getPatientByDni(String dni, ValueEventListener listener){
        Query query = database.orderByChild("dni").equalTo(dni);
        query.addListenerForSingleValueEvent(listener);
    }

    public void getPatientByUsername(String username, ValueEventListener listener){
        Query query = database.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(listener);
    }
    public void getPatientById(String id, ValueEventListener listener){
        Query query = database.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(listener);
    }
}
