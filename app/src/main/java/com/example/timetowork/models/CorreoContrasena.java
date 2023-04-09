package com.example.timetowork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CorreoContrasena {
    @SerializedName("correo")
    @Expose
    private String correo;
    @SerializedName("password")
    @Expose
    private String password;

    public CorreoContrasena() {
    }

    public CorreoContrasena(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

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

    @Override
    public String toString() {
        return "CorreoContrasena{" +
                "correo='" + correo + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
