package com.example.futbol_connection_notes;

public class Foro {
    private String nombre;
    private int imagenResourceId;

    public Foro(String nombre, int imagenResourceId) {
        this.nombre = nombre;
        this.imagenResourceId = imagenResourceId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getImagenResourceId() {
        return imagenResourceId;
    }
}
