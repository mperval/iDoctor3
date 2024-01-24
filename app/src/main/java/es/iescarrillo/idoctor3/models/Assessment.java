package es.iescarrillo.idoctor3.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Assessment extends DomainEntity implements Serializable {

    private String username;
    private String title;
    private String description;
    private Double stars;
    private LocalDateTime assessmentDateTime;
    private String professionalId;

    public Assessment() {super();}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public LocalDateTime getAssessmentDateTime() {
        return assessmentDateTime;
    }

    public void setAssessmentDateTime(LocalDateTime assessmentDateTime) {
        this.assessmentDateTime = assessmentDateTime;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }
}
