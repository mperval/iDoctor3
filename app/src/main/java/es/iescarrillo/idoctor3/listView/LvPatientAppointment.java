package es.iescarrillo.idoctor3.listView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.activities.MenuActivity;
import es.iescarrillo.idoctor3.activities.PatientFragment;
import es.iescarrillo.idoctor3.activities.ViewEvaluation;
import es.iescarrillo.idoctor3.adapters.AdapterObjectAppointment;
import es.iescarrillo.idoctor3.details.DetailsPatientAppointment;
import es.iescarrillo.idoctor3.models.Appointment;
import es.iescarrillo.idoctor3.models.Consultation;
import es.iescarrillo.idoctor3.models.ObjetAppointment;
import es.iescarrillo.idoctor3.models.Professional;
import es.iescarrillo.idoctor3.services.AppointmentService;
import es.iescarrillo.idoctor3.services.ConsultationService;
import es.iescarrillo.idoctor3.services.PatientService;
import es.iescarrillo.idoctor3.services.ProfessionalService;

public class LvPatientAppointment extends AppCompatActivity {

    private ListView lvAppointmentPatient;
    private Button btnBackLvAppointment;
    private ProfessionalService professionalService;
    private AppointmentService appointmentService;
    private ConsultationService consultationService;
    private PatientService patientService;
    private String id;
    private SharedPreferences sharedPreferences;
    private AdapterObjectAppointment adapterObjectAppointment;
    private List<Appointment> appointmentList;
    private List<Consultation> consultationList;
    private List<Professional> professionalList;
    private List<ObjetAppointment> ListAppoConsulPro;
    private ObjetAppointment objetAppointment;
    private LocalTime appointmentTime;
    private LocalDate appointmentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_appointment);

        btnBackLvAppointment = findViewById(R.id.btnBackLvAppointment);
        lvAppointmentPatient = findViewById(R.id.lvAppointmentPatient);

        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");

        consultationService = new ConsultationService(getApplicationContext());
        professionalService = new ProfessionalService(getApplicationContext());
        appointmentService = new AppointmentService(getApplicationContext());
        patientService = new PatientService(getApplicationContext());

        appointmentList = new ArrayList<>();
        consultationList = new ArrayList<>();
        professionalList = new ArrayList<>();

        cargarListView();

        Log.i("tamanio del array ListAppoConsulPro1", String.valueOf(consultationList.size()));
        Log.i("tamanio del array ListAppoConsulPro2", String.valueOf(professionalList.size()));

        btnBackLvAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(LvPatientAppointment.this, MenuActivity.class);
            startActivity(intent);
        });

    }

    public void cargarListView() {
        appointmentService.viewAppointmentEnable(id, new ValueEventListener() {//entro en el nodo appointment
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                //recorro citas
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment app = new Appointment();

                    appointmentTime = convertSnapshotToTime(dataSnapshot,"appLocalDateTime");
                    appointmentDate = convertSnapshotToDate(dataSnapshot, "appLocalDateTime");

                    Log.i("Esto es el time", String.valueOf(appointmentTime));
                    Log.i("Esto es el date", String.valueOf(appointmentDate));

                    app.setAppointmentDate(appointmentDate);
                    app.setAppointmentTime(appointmentTime);
                    app.setPatientId(id);
                    app.setConsultationId(dataSnapshot.child("consultationId").getValue().toString());

                    appointmentList.add(app);

                    Log.i("tamanio del array ListAppoConsulPro", String.valueOf(appointmentList.size()));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en la lectura de firebase.", error.toException());
            }
        });

        // Ordenar la lista por date y time
        Collections.sort(appointmentList, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment app1, Appointment app2) {
                // Comparar por date
                int dateComparison = app1.getAppointmentDate().compareTo(app2.getAppointmentDate());

                // Si las fechas son iguales, comparar por time
                if (dateComparison == 0) {
                    return app1.getAppointmentTime().compareTo(app2.getAppointmentTime());
                }

                return dateComparison;
            }
        });
        consultationService.getConsultationOrderByCity(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                consultationList.clear();
                //recorro citas
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Consultation consultation = dataSnapshot.getValue(Consultation.class);

                    consultationList.add(consultation);

                    Log.i("tamanio del array ListAppoConsulPro", String.valueOf(consultationList.size()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en la lectura de firebase.", error.toException());
            }
        });
        professionalService.getProfessionalsOrderByName(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                professionalList.clear();
                ListAppoConsulPro = new ArrayList<>();
                //recorro citas
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Professional professional = dataSnapshot.getValue(Professional.class);

                    professionalList.add(professional);

                    Log.i("tamanio del array ListAppoConsulPro", String.valueOf(professionalList.size()));
                }
                for (Appointment appointment : appointmentList) {
                    Log.i("este es el for de appointmet", appointment.toString());
                    for (Consultation consultation : consultationList) {
                        Log.i("este es el for de consultation", consultation.toString());
                        for (Professional professional : professionalList) {
                            Log.i("este es el for de professional", professional.toString());
                            if (appointment.getPatientId().equals(id) && appointment.getConsultationId().equals(consultation.getId()) && consultation.getProfessionalId().equals(professional.getId())) {
                                objetAppointment = new ObjetAppointment();
                                //TODO REVISAR LA FECHA.
                                objetAppointment.setId(appointment.getId());
                                objetAppointment.setIdPatient(appointment.getPatientId());
                                objetAppointment.setAppointmentDate(appointmentDate);
                                objetAppointment.setAppointmentTime(appointmentTime);
                                objetAppointment.setDoctorName(professional.getName());
                                objetAppointment.setAddress(consultation.getAddress());
                                objetAppointment.setSpeciality(professional.getSpeciality());
                                objetAppointment.setCity(consultation.getCity());
                                objetAppointment.setActive(appointment.getActive());
                                objetAppointment.setIdConsultation(consultation.getId());

                                ListAppoConsulPro.add(objetAppointment);
                                Log.i("OBJECT ", objetAppointment.toString());
                            }
                        }
                    }
                }
                adapterObjectAppointment = new AdapterObjectAppointment(getApplicationContext(), ListAppoConsulPro);
                adapterObjectAppointment.notifyDataSetChanged();
                lvAppointmentPatient.setAdapter(adapterObjectAppointment);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Error en la lectura de firebase.", error.toException());
            }
        });

        lvAppointmentPatient.setOnItemClickListener((parent, view, position, id1) -> {
            ObjetAppointment objetAppointment1 = (ObjetAppointment) parent.getItemAtPosition(position);

            Intent intent = new Intent(this, ViewEvaluation.class);
            //TODO LA FECHA.
            intent.putExtra("appointmentId", objetAppointment1.getId());
            intent.putExtra("idConsultation", objetAppointment1.getIdConsultation());
            //intent.putExtra("date", objetAppointment1.getAppointmentDate());
            //intent.putExtra("time", objetAppointment1.getAppointmentTime());
            intent.putExtra("nameDoctor", objetAppointment1.getDoctorName());
            intent.putExtra("address", objetAppointment1.getAddress());
            intent.putExtra("speciality", objetAppointment1.getSpeciality());
            intent.putExtra("city", objetAppointment1.getCity());
            intent.putExtra("activate", objetAppointment1.getActive());
            intent.putExtra("idPatient", id);

            startActivity(intent);
        });
    }

    private LocalTime convertSnapshotToTime(DataSnapshot snapshot, String timeKey) {
        DataSnapshot timeSnapshot = snapshot.child(timeKey);
        Integer hours = Integer.parseInt(timeSnapshot.child("hour").getValue().toString());
        Integer minutes = Integer.parseInt(timeSnapshot.child("minute").getValue().toString());

        // Agregar un "0" delante de la hora si es menor que 10
        String formattedHours = (hours < 10) ? "0" + hours : String.valueOf(hours);
        // Agregar un "0" delante de los minutos si son menores que 10
        String formattedMinutes = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);

        return LocalTime.parse(formattedHours + ":" + formattedMinutes);
    }

    /**
     * Método para convertir a LocalDate
     * @param snapshot
     * @param dateTimeKey
     * @return
     */
    private LocalDate convertSnapshotToDate(DataSnapshot snapshot, String dateTimeKey) {
        DataSnapshot dateTimeSnapshot = snapshot.child(dateTimeKey);

        Integer year = Integer.parseInt(dateTimeSnapshot.child("year").getValue().toString());
        Integer month = Integer.parseInt(dateTimeSnapshot.child("monthValue").getValue().toString());
        Integer day = Integer.parseInt(dateTimeSnapshot.child("dayOfMonth").getValue().toString());

        return LocalDate.of(year, month, day);
    }

}