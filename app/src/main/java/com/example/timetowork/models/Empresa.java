package com.example.timetowork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Empresa implements Serializable { //Clase Empresa que implementa la interfaz serializable para poder pasar objetos entre activities
    @SerializedName("idEmpresa") //indicamos el nombre del campo de la respuesta JSON
    @Expose //permite la serialización o deserialización JSON
    private int idEmpresa;
    @SerializedName("cif")
    @Expose
    private String CIF;
    @SerializedName("nombreEmpresa")
    @Expose
    private String nombreEmpresa;
    @SerializedName("telefono")
    @Expose
    private int telefono;
    @SerializedName("nombreadmin")
    @Expose
    private String nombreAdmin;
    @SerializedName("pais")
    @Expose
    private String pais;
    @SerializedName("provincia")
    @Expose
    private String provincia;
    @SerializedName("ciudad")
    @Expose
    private String ciudad;
    @SerializedName("usuarios")
    @Expose
    private List<Usuario> usuarios;

    //Constructores
    public Empresa() {
    }

    public Empresa(int idEmpresa, String CIF, String nombreEmpresa, int telefono, String nombreAdmin, String pais, String provincia, String ciudad, List<Usuario> usuarios) {
        this.idEmpresa = idEmpresa;
        this.CIF = CIF;
        this.nombreEmpresa = nombreEmpresa;
        this.telefono = telefono;
        this.nombreAdmin = nombreAdmin;
        this.pais = pais;
        this.provincia = provincia;
        this.ciudad = ciudad;
        this.usuarios = usuarios;
    }

    //Getters y Setters
    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    //Método toString
    @Override
    public String toString() {
        return "Empresa{" +
                "idEmpresa=" + idEmpresa +
                ", CIF='" + CIF + '\'' +
                ", nombreEmpresa='" + nombreEmpresa + '\'' +
                ", telefono=" + telefono +
                ", nombreAdmin='" + nombreAdmin + '\'' +
                ", pais='" + pais + '\'' +
                ", provincia='" + provincia + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", usuarios=" + usuarios +
                '}';
    }
}
