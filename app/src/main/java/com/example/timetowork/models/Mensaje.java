package com.example.timetowork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Mensaje implements Serializable {
    @SerializedName("idMensaje")
    @Expose
    private int idMensaje;

    @SerializedName("de")
    @Expose
    private String de;

    @SerializedName("para")
    @Expose
    private String para;

    @SerializedName("fecha")
    @Expose
    private String fecha;

    @SerializedName("hora")
    @Expose
    private String hora;

    @SerializedName("nomEmpresa")
    @Expose
    private String nomEmpresa;

    @SerializedName("centroDe")
    @Expose
    private String centroDe;

    @SerializedName("centroPara")
    @Expose
    private String centroPara;

    @SerializedName("asunto")
    @Expose
    private String asunto;

    @SerializedName("contenido")
    @Expose
    private String contenido;

    @SerializedName("vistoDe")
    @Expose
    private boolean vistoDe;

    @SerializedName("vistoPara")
    @Expose
    private boolean vistoPara;

    @SerializedName("usuario_fk")
    @Expose
    private Usuario usuario_fk;

    public Mensaje() {
    }

    public Mensaje(int idMensaje, String de, String para, String fecha, String hora, String nomEmpresa, String centroDe, String centroPara, String asunto, String contenido, boolean vistoDe, boolean vistoPara, Usuario usuario_fk) {
        this.idMensaje = idMensaje;
        this.de = de;
        this.para = para;
        this.fecha = fecha;
        this.hora = hora;
        this.nomEmpresa = nomEmpresa;
        this.centroDe = centroDe;
        this.centroPara = centroPara;
        this.asunto = asunto;
        this.contenido = contenido;
        this.vistoDe = vistoDe;
        this.vistoPara = vistoPara;
        this.usuario_fk = usuario_fk;
    }

    public int getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNomEmpresa() {
        return nomEmpresa;
    }

    public void setNomEmpresa(String nomEmpresa) {
        this.nomEmpresa = nomEmpresa;
    }

    public String getCentroDe() {
        return centroDe;
    }

    public void setCentroDe(String centroDe) {
        this.centroDe = centroDe;
    }

    public String getCentroPara() {
        return centroPara;
    }

    public void setCentroPara(String centroPara) {
        this.centroPara = centroPara;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isVistoDe() {
        return vistoDe;
    }

    public void setVistoDe(boolean vistoDe) {
        this.vistoDe = vistoDe;
    }

    public boolean isVistoPara() {
        return vistoPara;
    }

    public void setVistoPara(boolean vistoPara) {
        this.vistoPara = vistoPara;
    }

    public Usuario getUsuario_fk() {
        return usuario_fk;
    }

    public void setUsuario_fk(Usuario usuario_fk) {
        this.usuario_fk = usuario_fk;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "idMensaje=" + idMensaje +
                ", de='" + de + '\'' +
                ", para='" + para + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", nomEmpresa='" + nomEmpresa + '\'' +
                ", centroDe='" + centroDe + '\'' +
                ", centroPara='" + centroPara + '\'' +
                ", asunto='" + asunto + '\'' +
                ", contenido='" + contenido + '\'' +
                ", vistoDe=" + vistoDe +
                ", vistoPara=" + vistoPara +
                ", usuario_fk=" + usuario_fk +
                '}';
    }
}
