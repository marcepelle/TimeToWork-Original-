package com.example.timetowork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Empresa {
    @SerializedName("CIF")
    @Expose
    private String CIF;
    @SerializedName("nombreEmpresa")
    @Expose
    private String nombreEmpresa;
    @SerializedName("telefono")
    @Expose
    private int telefono;
    @SerializedName("nombreAdmin")
    @Expose
    private String nombreAdmin;
    @SerializedName("pais")
    @Expose
    private String pais;
    @SerializedName("provincia")
    @Expose
    private String provincia;
    @SerializedName("Ciudad")
    @Expose
    private String ciudad;

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCIF() {
        return CIF;
    }

    public void setCIF(String CIF) {
        this.CIF = CIF;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }
    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }


    public String getNombreAdmin() {
        return nombreAdmin;
    }

    public void setNombreAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    @Override
    public String toString() {
        return "Empresa{CIF='" + CIF + '\'' +
                ", nombreEmpresa='" + nombreEmpresa + '\'' +
                ", telefono='" + telefono + '\'' +
                ", nombreadmin='" + nombreAdmin + '\'' +
                ", pais='" + pais + '\'' +
                ", provincia='" + provincia + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }

}
