package es.iescarrillo.idoctor3.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment extends DomainEntity{

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Boolean active;
    private String patientId;
    private String consultationId;
    private LocalDateTime appLocalDateTime;



    public Appointment() { super(); }

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

    public void isActive(Boolean active) {
        this.active = active;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public LocalDateTime getAppLocalDateTime() {
        return appLocalDateTime;
    }

    public void setAppLocalDateTime(LocalDateTime appLocalDateTime) {
        this.appLocalDateTime = appLocalDateTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", active=" + active +
                ", patientId='" + patientId + '\'' +
                ", consultationId='" + consultationId + '\'' +
                ", appLocalDateTime=" + appLocalDateTime +
                '}';
    }




}
