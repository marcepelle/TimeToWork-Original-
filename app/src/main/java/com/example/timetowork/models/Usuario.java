package com.example.timetowork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class Usuario implements Serializable {
    @SerializedName("idUsuario")
    @Expose
    private int idUsuario;
    @SerializedName("nombreUsuario")
    @Expose
    private String nombreUsuario = null;
    @SerializedName("apellidosUsuario")
    @Expose
    private String apellidosUsuario = null;
    @SerializedName("telefono")
    @Expose
    private int telefono;
    @SerializedName("direccion")
    @Expose
    private String direccion=null;
    @SerializedName("empresaUsuario")
    @Expose
    private String empresaUsuario = null;
    @SerializedName("lugarTrabajo")
    @Expose
    private String lugarTrabajo = null;
    @SerializedName("fechaNacimiento")
    @Expose
    private String fechaNacimiento = null;
    @SerializedName("correoUsuario")
    @Expose
    private String correoUsuario = null;
    @SerializedName("contrasena")
    @Expose
    private String contrasena = null;
    @SerializedName("esAdmin")
    @Expose
    private boolean esAdmin = false;
    @SerializedName("empresa_fk")
    @Expose
    private Empresa empresa_fk;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombreUsuario, String apellidosUsuario, int telefono, String direccion, String empresaUsuario, String lugarTrabajo, String fechaNacimiento, String correoUsuario, String contrasena, boolean esAdmin, Empresa empresa_fk) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.apellidosUsuario = apellidosUsuario;
        this.telefono = telefono;
        this.direccion = direccion;
        this.empresaUsuario = empresaUsuario;
        this.lugarTrabajo = lugarTrabajo;
        this.fechaNacimiento = fechaNacimiento;
        this.correoUsuario = correoUsuario;
        this.contrasena = contrasena;
        this.esAdmin = esAdmin;
        this.empresa_fk = empresa_fk;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidosUsuario() {
        return apellidosUsuario;
    }

    public void setApellidosUsuario(String apellidosUsuario) {
        this.apellidosUsuario = apellidosUsuario;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmpresaUsuario() {
        return empresaUsuario;
    }

    public void setEmpresaUsuario(String empresaUsuario) {
        this.empresaUsuario = empresaUsuario;
    }

    public String getLugarTrabajo() {
        return lugarTrabajo;
    }

    public void setLugarTrabajo(String lugarTrabajo) {
        this.lugarTrabajo = lugarTrabajo;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public Empresa getEmpresa_fk() {
        return empresa_fk;
    }

    public void setEmpresa_fk(Empresa empresa_fk) {
        this.empresa_fk = empresa_fk;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", apellidosUsuario='" + apellidosUsuario + '\'' +
                ", telefono=" + telefono +
                ", direccion='" + direccion + '\'' +
                ", empresaUsuario='" + empresaUsuario + '\'' +
                ", lugarTrabajo='" + lugarTrabajo + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", correoUsuario='" + correoUsuario + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", esAdmin=" + esAdmin +
                '}';
    }
}
