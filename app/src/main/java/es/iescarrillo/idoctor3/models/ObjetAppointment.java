package es.iescarrillo.idoctor3.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class ObjetAppointment {
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Boolean active;
    private String idPatient;
    private String city;
    private String idConsultation;
    private String address;
    private String doctorName;
    private String Speciality;
    private String id;

    public ObjetAppointment() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSpeciality() {
        return Speciality;
    }

    public void setSpeciality(String speciality) {
        Speciality = speciality;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(String idPatient) {
        this.idPatient = idPatient;
    }

    public String getIdConsultation() {
        return idConsultation;
    }

    public void setIdConsultation(String idConsultation) {
        this.idConsultation = idConsultation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Override
    public String toString() {
        return "ObjetAppointment{" +
                "appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", active=" + active +
                ", idPatient='" + idPatient + '\'' +
                ", idConsultation='" + idConsultation + '\'' +
                ", address='" + address + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
