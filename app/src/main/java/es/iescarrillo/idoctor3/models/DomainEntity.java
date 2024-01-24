package es.iescarrillo.idoctor3.models;

import java.io.Serializable;

public abstract class DomainEntity implements Serializable {

    private String id;

    public DomainEntity(){ }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DomainEntity{" +
                "id='" + id + '\'' +
                '}';
    }
}
