package es.iescarrillo.idoctor3.models;

import java.io.Serializable;

public class Patient extends Person implements Serializable {

    private String dni;
    private String email;
    private String phone;
    private Boolean healthInsurance;

    public Patient() { super(); }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getHealthInsurance() {
        return healthInsurance;
    }

    public void setHealthInsurance(Boolean healthInsurance) {
        this.healthInsurance = healthInsurance;
    }
}
