package es.iescarrillo.idoctor3.services;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.models.Evaluation;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.models.Report;

public class ReportService {

    // Referencia de la base de datos
    private DatabaseReference database;

    //Constructor vacio
    public ReportService (Context context){
        // Inicializamos la basde de datos y su referencia al nodo
        database = FirebaseDatabase.getInstance().getReference().child("report");
    }

    public void insertReport(Report report){
        //Utiliza push() para obtener una clave única y agregar el mensaje
        DatabaseReference databaseReference = database.push();
        report.setId(databaseReference.getKey()); //Asigna el ID generado automáticamente

        //Ahora, utiliza setValue() en la nueva referencia para agregar el nuevo mensaje
        databaseReference.setValue(report);
    }

    public void updateReport(Report report) {
        database.child(report.getId()).setValue(report);
    }

    public void getReportByEvaluationId(String evaluationId, ValueEventListener listener){
        Query query = database.orderByChild("evaluationId").equalTo(evaluationId);
        query.addListenerForSingleValueEvent(listener);
    }


}
