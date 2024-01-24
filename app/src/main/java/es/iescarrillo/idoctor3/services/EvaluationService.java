package es.iescarrillo.idoctor3.services;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.models.Evaluation;

public class EvaluationService {

    // Referencia de la base de datos
    private DatabaseReference database;

    //Constructor vacio
    public EvaluationService (Context context){
        // Inicializamos la basde de datos y su referencia al nodo
        database = FirebaseDatabase.getInstance().getReference().child("evaluation");
    }

    public String insertEvaluation(Evaluation evaluation){
        //Utiliza push() para obtener una clave única y agregar el mensaje
        DatabaseReference databaseReference = database.push();
        evaluation.setId(databaseReference.getKey()); //Asigna el ID generado automáticamente

        //Ahora, utiliza setValue() en la nueva referencia para agregar el nuevo mensaje
        databaseReference.setValue(evaluation);

        return evaluation.getId();
    }

    public void getEvaluationByAppointmentId(String appointmentId, ValueEventListener listener){
        Query query = database.orderByChild("appointmentId").equalTo(appointmentId);
        query.addListenerForSingleValueEvent(listener);
    }








}
