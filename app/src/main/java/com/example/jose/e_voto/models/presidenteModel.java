package com.example.jose.e_voto.models;

/**
 * Created by Jose on 04/07/2016.
 */
public class presidenteModel {
    private String nombre;
    private String partido;
    private String foto;
    private String propuestas;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPropuestas() {
        return propuestas;
    }

    public void setPropuestas(String propuestas) {
        this.propuestas = propuestas;
    }
}
