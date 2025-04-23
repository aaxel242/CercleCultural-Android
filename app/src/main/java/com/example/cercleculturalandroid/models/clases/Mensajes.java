package com.example.cercleculturalandroid.models.clases;

import java.util.Date;

public class Mensajes {
    private int id;
    private int usuari_id;
    private String nom_usuari;
    private String missatge;
    private Date dataEnviament; // Ahora usa Date en vez de String

    // Constructor vacío (requerido para Retrofit/GSON)
    public Mensajes() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUsuari_id() {
        return usuari_id;
    }
    public void setUsuari_id(int usuari_id) {
        this.usuari_id = usuari_id;
    }

    public String getNom_usuari() {
        return nom_usuari;
    }
    public void setNom_usuari(String nom_usuari) {
        this.nom_usuari = nom_usuari;
    }

    public String getMissatge() {
        return missatge;
    }
    public void setMissatge(String missatge) {
        this.missatge = missatge;
    }

    public Date getDataEnviament() {
        return dataEnviament;
    }
    public void setDataEnviament(Date dataEnviament) {
        this.dataEnviament = dataEnviament;
    }
}
