package es.iescarrillo.idoctor3.services;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.activities.AssessmentActivity;
import es.iescarrillo.idoctor3.models.Assessment;

public class AssessmentService {

    // Referencia de la base de datos
    private DatabaseReference database;

    //Constructor vacio
    public AssessmentService (Context context){
        // Inicializamos la basde de datos y su referencia al nodo
        database = FirebaseDatabase.getInstance().getReference().child("assessment");
    }

    public void insertAssessment(Assessment assessment){
        //Utiliza push() para obtener una clave única y agregar el mensaje
        DatabaseReference databaseReference = database.push();
        assessment.setId(databaseReference.getKey()); //Asigna el ID generado automáticamente

        //Ahora, utiliza setValue() en la nueva referencia para agregar el nuevo mensaje
        databaseReference.setValue(assessment);
    }

}
