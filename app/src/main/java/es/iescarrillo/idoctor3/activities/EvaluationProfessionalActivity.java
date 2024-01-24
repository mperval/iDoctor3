package es.iescarrillo.idoctor3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.time.LocalDateTime;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.listView.LvConsultations;
import es.iescarrillo.idoctor3.models.Evaluation;
import es.iescarrillo.idoctor3.models.Report;
import es.iescarrillo.idoctor3.services.EvaluationService;
import es.iescarrillo.idoctor3.services.PatientService;

public class EvaluationProfessionalActivity extends AppCompatActivity {
    private EditText etDescripEvalProf, etExpEvalProf, etTreatEvalProf;
    private Button btnBackEval, btnAddEval;
    private Evaluation evaluation;
    private EvaluationService evaluationService;
    private String evaluationId, appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_professional);

        //llamo a los componentes
        getComponentFromView();

        //creo objeto evaluacion
        evaluation = new Evaluation();

        Intent intent = getIntent();
        appointmentId = intent.getStringExtra("appointmentId");
        //Inicializamos el servicio
        evaluationService = new EvaluationService(getApplicationContext());

        btnAddEval.setOnClickListener(v ->{

            // Obtener los datos ingresados por el usuario
            String descripEvalProf = etDescripEvalProf.getText().toString().trim();
            String expEvalProf = etExpEvalProf.getText().toString().trim();
            String treatEvalProf = etTreatEvalProf.getText().toString().trim();

            if (descripEvalProf.isEmpty() || expEvalProf.isEmpty() || treatEvalProf.isEmpty()) {
                Toast.makeText(EvaluationProfessionalActivity.this, "All fields must be completed", Toast.LENGTH_SHORT).show();

            }else {

                evaluation.setDescription(descripEvalProf);
                evaluation.setExploration(expEvalProf);
                evaluation.setTreatment(treatEvalProf);
                evaluation.setAppointmentId(appointmentId);

                // Convertir LocalDateTime a String
                evaluation.setEvaluationDateTime(LocalDateTime.now());
                Log.i("el local date time", String.valueOf(LocalDateTime.now()));

                // Almacenar la fecha y hora actual como cadena directamente
                evaluationId = evaluationService.insertEvaluation(evaluation);

                Toast.makeText(this, "Evaluation Registered", Toast.LENGTH_SHORT).show();

                mostrarDialogo();

                //Intent intentConsultations = new Intent(this, MenuActivity.class);
                //startActivity(intentConsultations);
                //finish();

            }

        });

        btnBackEval.setOnClickListener(v -> {
            Intent intentMenu = new Intent(this, MenuActivity.class);
            startActivity(intentMenu);
            finish();
        });

    }
    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        etDescripEvalProf = findViewById(R.id.etDescripEvalProf);
        etExpEvalProf = findViewById(R.id.etExpEvalProf);
        etTreatEvalProf = findViewById(R.id.etTreatEvalProf);
        btnBackEval = findViewById(R.id.btnBackEval);
        btnAddEval = findViewById(R.id.btnAddEval);

    }

    // Método para verificar si algún campo está vacío
    private boolean checkEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true; // Devuelve true si al menos un campo está vacío
            }
        }
        return false; // Devuelve false si todos los campos están llenos
    }

    private void mostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add report")
                .setMessage("Do you want to add a report? You will not be able to add it later.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EvaluationProfessionalActivity.this, ReportActivity.class);
                        intent.putExtra("evaluationId", evaluationId);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Ccción cuando el usuario elige "No"
                        // Cerrar el diálogo
                        dialog.dismiss();
                        Intent intentMain = new Intent(EvaluationProfessionalActivity.this, MenuActivity.class);
                        startActivity(intentMain);
                        finish();
                    }
                })
                .show();
    }
}