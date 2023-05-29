package com.example.timetowork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CorreoContrasena { //Clase correoContrasena
    @SerializedName("correo")  //indicamos el nombre del campo de la respuesta JSON
    @Expose  //permite la serialización o deserialización JSON
    private String correo;
    @SerializedName("password")
    @Expose
    private String password;

    //Constructores
    public CorreoContrasena() {
    }

    public CorreoContrasena(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    //Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //Método toString
    @Override
    public String toString() {
        return "CorreoContrasena{" +
                "correo='" + correo + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
