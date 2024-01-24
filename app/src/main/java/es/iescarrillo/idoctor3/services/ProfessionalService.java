package es.iescarrillo.idoctor3.services;

import com.google.firebase.database.DatabaseReference;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.idoctor3.models.Professional;

public class ProfessionalService {

    DatabaseReference database;

    public ProfessionalService(Context context) {
        database = FirebaseDatabase.getInstance().getReference().child("professional");
    }

    /**
     * Inserta un nuevo objeto Professional en la base de datos.
     *
     * @param professional El objeto Professional que se va a insertar en la base de datos.
     */
    public void insertProfessional(Professional professional) {
        // Utiliza push() para obtener una clave única y agregar el mensaje
        DatabaseReference newReference = database.push();
        professional.setId(newReference.getKey()); // Asigna el ID generado automáticamente

        // Ahora, utiliza setValue() en la nueva referencia para agregar el nuevo mensaje
        newReference.setValue(professional);
    }

    /**
     * Actualiza un objeto Professional existente en la base de datos.
     *
     * @param professional El objeto Professional que se va a actualizar en la base de datos.
     */
    public void updateProfessional(Professional professional) {
        database.child(professional.getId()).setValue(professional);
    }

    /**
     * Elimina un profesional de la base de datos según el ID proporcionado.
     *
     * @param id El ID del profesional que se va a eliminar de la base de datos.
     */
    public void deleteProfessional(String id) {
        database.child(id).removeValue();
    }

    public void getProfessionalByCollegiateNumber(String collegiateNumber, ValueEventListener listener) {
        Query query = database.orderByChild("collegiateNumber").equalTo(collegiateNumber);
        query.addListenerForSingleValueEvent(listener);
    }

    /**
     * Agrega un ValueEventListener a la referencia de la base de datos para obtener información
     * relacionada con los profesionales y activar eventos cuando hay cambios en esos datos.
     *
     * @param listener ValueEventListener que maneja los eventos de cambios de datos en Firebase.
     */
    public void getProfessionalsOrderByName(ValueEventListener listener) {
        Query query = database.orderByChild("name");
        query.addValueEventListener(listener);
    }

    public void getProfessionalByUsername(String username, ValueEventListener listener) {
        Query query = database.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(listener);
    }

    public void getProfessionalById(String id, ValueEventListener listener) {
        Query query = database.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(listener);
    }

    public void getProfessionalOrderBySpeciality(ValueEventListener listener){
        Query query = database.orderByChild("speciality");
        query.addValueEventListener(listener);
    }

    public void getProfessionalByAssessmentId(String id, ValueEventListener listener){
        Query query = database.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(listener);
    }


}
