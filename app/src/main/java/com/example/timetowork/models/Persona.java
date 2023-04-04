package com.example.timetowork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Persona {
    @SerializedName("id")// anotacion que indica que debe ser serializado y serializado con el nombre del atributo JSON que se indica
    @Expose//expose permite serializar o deserializar el objeto, por defecto es true
    private int id;
    @SerializedName("nombre")
    @Expose
    private String nombres;
    @SerializedName("apellido")
    @Expose
    private String apellido;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellido + '\'' +
                '}';
    }
}
