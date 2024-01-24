package es.iescarrillo.idoctor3.models;

import java.io.Serializable;

public class Professional extends Person implements Serializable {

    private String collegiateNumber;
    private String speciality;
    private String description;
    private Double stars;
    private Integer assessments;

    public Professional() {super();}

    public String getCollegiateNumber() {
        return collegiateNumber;
    }

    public void setCollegiateNumber(String collegiateNumber) {
        this.collegiateNumber = collegiateNumber;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
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

    public Integer getAssessments() {
        return assessments;
    }

    public void setAssessments(Integer assessments) {
        this.assessments = assessments;
    }


    @Override
    public String toString() {
        return "Professional{" +
                "collegiateNumber='" + collegiateNumber + '\'' +
                ", speciality='" + speciality + '\'' +
                ", description='" + description + '\'' +
                ", stars=" + stars +
                ", assessments=" + assessments +
                '}';
    }
}
