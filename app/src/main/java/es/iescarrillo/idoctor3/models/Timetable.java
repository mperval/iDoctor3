package es.iescarrillo.idoctor3.models;

import java.io.Serializable;
import java.time.LocalTime;

public class Timetable extends DomainEntity implements Serializable {

    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String consultationId;

    public Timetable() {}

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    @Override
    public String toString() {
        return "Timetable{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", consultationId='" + consultationId + '\'' +
                '}';
    }
}
