package es.iescarrillo.idoctor3.services;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.models.Consultation;

public class AppointmentService {

    private DatabaseReference database;

    public AppointmentService (Context context){
        // Inicializamos la basde de datos y su referencia al nodo
        database = FirebaseDatabase.getInstance().getReference().child("appointment");
    }

    public void insertAppointment(Appointment appointment){
        //Utiliza push() para obtener una clave única y agregar el mensaje
        DatabaseReference databaseReference = database.push();
        appointment.setId(databaseReference.getKey()); //Asigna el ID generado automáticamente

        //Ahora, utiliza setValue() en la nueva referencia para agregar el nuevo mensaje
        databaseReference.setValue(appointment);
    }

    public void updateAppointment(Appointment appointment){
        database.child(appointment.getId()).setValue(appointment);
    }

    public void deleteAppointment(String id){
        database.child(id).removeValue();
    }

    public void getAppointmentByConsultationId(String id, ValueEventListener listener){
        Query query = database.orderByChild("consultationId").equalTo(id);
        query.addValueEventListener(listener);
    }

    public void viewAppointmentEnable(String patientId, ValueEventListener listener){
        Query query = database.orderByChild("patientId").equalTo(patientId);
        query.addValueEventListener(listener);
    }
}
