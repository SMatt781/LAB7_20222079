package com.example.practica3_lab6_iot;

import com.google.firebase.firestore.Exclude;

public class SesionEstudio {

    @Exclude
    private String id;
    private String curso;
    private String fecha;
    private double horas;
    private String tipoActividad;
    private long timestamp;

    public SesionEstudio() {
    }

    public SesionEstudio(String curso, String fecha, double horas, String tipoActividad, long timestamp) {
        this.curso = curso;
        this.fecha = fecha;
        this.horas = horas;
        this.tipoActividad = tipoActividad;
        this.timestamp = timestamp;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getHoras() {
        return horas;
    }

    public void setHoras(double horas) {
        this.horas = horas;
    }

    public String getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(String tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
