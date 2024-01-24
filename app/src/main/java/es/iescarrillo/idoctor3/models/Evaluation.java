package es.iescarrillo.idoctor3.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Evaluation extends DomainEntity implements Serializable {

    private String description;
    private String exploration;
    private String treatment;
    private LocalDateTime evaluationDateTime;
    private String appointmentId;

    public Evaluation() { super(); }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExploration() {
        return exploration;
    }

    public void setExploration(String exploration) {
        this.exploration = exploration;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public LocalDateTime getEvaluationDateTime() {
        return evaluationDateTime;
    }

    public void setEvaluationDateTime(LocalDateTime evaluationDateTime) {
        this.evaluationDateTime = evaluationDateTime;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "description='" + description + '\'' +
                ", exploration='" + exploration + '\'' +
                ", treatment='" + treatment + '\'' +
                ", evaluationDateTime=" + evaluationDateTime +
                ", appointmentId='" + appointmentId + '\'' +
                '}';
    }
}
