package es.iescarrillo.idoctor3.models;

import java.io.Serializable;

public class EvaluationDisplayData extends DomainEntity implements Serializable {

    private String description;
    private String exploration;
    private String treatment;
    private String id;

    public EvaluationDisplayData() {}

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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
