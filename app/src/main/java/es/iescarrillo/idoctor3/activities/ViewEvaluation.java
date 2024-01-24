package es.iescarrillo.idoctor3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.adapters.AdapterConsultation;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.Evaluation;
import es.iescarrillo.idoctor3.models.EvaluationDisplayData;
import es.iescarrillo.idoctor3.models.Report;
import es.iescarrillo.idoctor3.services.EvaluationService;
import es.iescarrillo.idoctor3.services.ReportService;

public class ViewEvaluation extends AppCompatActivity {

    private TextView tvDescripEvalPac, tvExpEvalPac, tvTreatEvalPac;
    private Button btnBackEvalPac, btnViewReport;
    private EvaluationService evaluationService;
    private ReportService reportService;
    private SharedPreferences sharedPreferences;
    //el id será el de la evaluacion
    private String id, idGlobal;
    private String appointmentId;
    private Evaluation evaluation;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_evaluation);

        //recojo componentes
        getComponentFromView();

        //Inicializamos el servicio
        evaluationService = new EvaluationService(getApplicationContext());
        reportService = new ReportService(getApplicationContext());
        Intent intent = getIntent();
        appointmentId = intent.getStringExtra("appointmentId");

        evaluation = new Evaluation();
        //recojo el id
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        idGlobal = sharedPreferences.getString("id", "");

        evaluationService.getEvaluationByAppointmentId(appointmentId,  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    appointmentId = snapshot.getKey(); // Utiliza getKey para obtener el ID del nodo
                    Log.i("appointmentId", appointmentId);

                    EvaluationDisplayData displayData = snapshot.getValue(EvaluationDisplayData.class);

                    tvDescripEvalPac.setText(displayData.getDescription());
                    tvExpEvalPac.setText(displayData.getExploration());
                    tvTreatEvalPac.setText(displayData.getTreatment());
                    id = displayData.getId();
                    Log.i("id evaluation", displayData.getId());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //maneja erroes de lectura de la base de datos si es necesario
                Log.w("Firebase", "Error en la lectura de la BBDDD");
            }
        });

        btnViewReport.setOnClickListener(v -> {

            mostrarDialogo();

        });





        btnBackEvalPac.setOnClickListener(v -> {

            Intent intentConsultations = new Intent(this, MenuActivity.class);
            startActivity(intentConsultations);
            finish();

        });




    }

    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        tvDescripEvalPac = findViewById(R.id.tvDescripEvalPac);
        tvExpEvalPac = findViewById(R.id.tvExpEvalPac);
        tvTreatEvalPac = findViewById(R.id.tvTreatEvalPac);
        btnBackEvalPac = findViewById(R.id.btnBackEvalPac);
        btnViewReport = findViewById(R.id.btnViewReport);

    }

    private void mostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download Report")
                .setMessage("Do you want to download the report associated with the evaluation?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        reportService.getReportByEvaluationId(id,  new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    id = snapshot.getKey(); // Utiliza getKey para obtener el ID del nodo
                                    Log.i("id de la evaluacion para el report", id);

                                    if (snapshot.exists()) {
                                        //saco datos del report
                                        Report report = snapshot.getValue(Report.class);
                                        String link = report.getLink();
                                        Log.i("titulo report", report.getTitle());

                                        //abro el pdf
                                        openPdf(link);
                                    }else {
                                        Toast.makeText(context, "No hay evaluaciones", Toast.LENGTH_SHORT).show();
                                    }


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //maneja erroes de lectura de la base de datos si es necesario
                                Log.w("Firebase", "Error en la lectura de la BBDDD");
                            }
                        });


                        Intent intent = new Intent(ViewEvaluation.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Ccción cuando el usuario elige "No"
                        // Cerrar el diálogo
                        dialog.dismiss();
                        Intent intentMain = new Intent(ViewEvaluation.this, MenuActivity.class);
                        startActivity(intentMain);
                        finish();
                    }
                })
                .show();
    }

    private void openPdf(String pdfLink) {
        //metodo para abrir el pdf a traves de un link por parametro
        Uri pdfUri = Uri.parse(pdfLink);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (Exception e) {
            // Manejar la excepción si no hay aplicaciones para abrir PDF instaladas en el dispositivo
            e.printStackTrace();
        }
    }

}