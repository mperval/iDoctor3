package es.iescarrillo.idoctor3.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.models.Evaluation;
import es.iescarrillo.idoctor3.models.Report;
import es.iescarrillo.idoctor3.services.ReportService;

public class ReportActivity extends AppCompatActivity {

    private EditText etTitleReport;
    private Button btnSendReport;
    private ImageView imgPdfReport;
    private static final int COD_SEL_PDF = 400; // Cambiado el código de selección para PDF
    private String link;
    private Uri url;
    private StorageReference storageReference;
    private String storage_path = "report/*";
    private ReportService reportService;
    private Report report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        reportService = new ReportService(getApplicationContext());
        //recojo los componentes
        getComponentFromView();
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();

        String evaluationId = intent.getStringExtra("evaluationId");

        report = new Report();

        btnSendReport.setOnClickListener(v -> {
            String title = etTitleReport.getText().toString().trim();

                // Validar que se hayan completado todos los campos
                if (title.isEmpty()) {
                    Toast.makeText(ReportActivity.this, "All fields must be completed", Toast.LENGTH_SHORT).show();
                }else {

                    if (url == null) {
                        Toast.makeText(ReportActivity.this, "You must register a document", Toast.LENGTH_LONG).show();
                    }else {

                        report.setEvaluationId(evaluationId);
                        report.setLink(link);
                        report.setTitle(title);
                        reportService.insertReport(report);
                        subirPDF(url, title); // Método para subir el archivo PDF
                        Toast.makeText(ReportActivity.this, "Report registered", Toast.LENGTH_LONG).show();

                        Intent intentMain = new Intent(ReportActivity.this, LoginActivity.class);
                        startActivity(intentMain);
                        finish();
                    }
                }

        });



        imgPdfReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPDF(); //metodo
            }
        });

    }

    // Metodo donde cargaremos todos los componentes de nuestra vista
    private void getComponentFromView(){
        imgPdfReport = findViewById(R.id.imgPdfReport);
        etTitleReport = findViewById(R.id.etTitleReport);
        btnSendReport = findViewById(R.id.btnSendReport);
    }

    private void uploadPDF() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("application/pdf"); // Especifica el tipo MIME para archivos PDF

        startActivityForResult(i, COD_SEL_PDF); //selección para PDF
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == COD_SEL_PDF) { //selección para PDF
                url = data.getData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subirPDF(Uri pdf_uri, String title) {
        String rute_storage_pdf = storage_path + "" + title + ".pdf"; //nombre del archivo y la extensión
        StorageReference reference = storageReference.child(rute_storage_pdf);

        reference.putFile(pdf_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("photo", download_uri);
                            link = download_uri;
                            Toast.makeText(ReportActivity.this, "Updated photo", Toast.LENGTH_SHORT).show();

                            report.setLink(link);
                            reportService.updateReport(report);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReportActivity.this, "Error uploading PDF", Toast.LENGTH_SHORT).show();
            }
        });
    }

}