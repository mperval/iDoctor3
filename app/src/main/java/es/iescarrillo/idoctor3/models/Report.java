package es.iescarrillo.idoctor3.models;

public class Report extends DomainEntity{

    private String title;
    private String link;
    private String evaluationId;

    public Report() { super(); }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
    }
}
