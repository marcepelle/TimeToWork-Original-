package com.example.timetowork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class Horario {
        @SerializedName("idHorario")
        @Expose
        private int idHorario;
        @SerializedName("empleado")
        @Expose
        private String empleado = null;
        @SerializedName("correoEmpleado")
        @Expose
        private String correoEmpleado = null;
        @SerializedName("fecha")
        @Expose
        private String fecha;
        @SerializedName("centroTrabajo")
        @Expose
        private String centroTrabajo = null;
        @SerializedName("horaEntrada")
        @Expose
        private String horaEntrada;
        @SerializedName("horaSalida")
        @Expose
        private String horaSalida;
        @SerializedName("fichaEntrada")
        @Expose
        private String fichaEntrada;
        @SerializedName("fichaSalida")
        @Expose
        private String fichaSalida;
        @SerializedName("usuario_fk")
        @Expose
        private Usuario usuario_fk;

        public Horario() {
        }

    public Horario(int idHorario, String empleado, String correoEmpleado, String fecha, String centroTrabajo, String horaEntrada, String horaSalida, String fichaEntrada, String fichaSalida, Usuario usuario_fk) {
        this.idHorario = idHorario;
        this.empleado = empleado;
        this.correoEmpleado = correoEmpleado;
        this.fecha = fecha;
        this.centroTrabajo = centroTrabajo;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.fichaEntrada = fichaEntrada;
        this.fichaSalida = fichaSalida;
        this.usuario_fk = usuario_fk;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getCorreoEmpleado() {
        return correoEmpleado;
    }

    public void setCorreoEmpleado(String correoEmpleado) {
        this.correoEmpleado = correoEmpleado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCentroTrabajo() {
        return centroTrabajo;
    }

    public void setCentroTrabajo(String centroTrabajo) {
        this.centroTrabajo = centroTrabajo;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getFichaEntrada() {
        return fichaEntrada;
    }

    public void setFichaEntrada(String fichaEntrada) {
        this.fichaEntrada = fichaEntrada;
    }

    public String getFichaSalida() {
        return fichaSalida;
    }

    public void setFichaSalida(String fichaSalida) {
        this.fichaSalida = fichaSalida;
    }

    public Usuario getUsuario_fk() {
        return usuario_fk;
    }

    public void setUsuario_fk(Usuario usuario_fk) {
        this.usuario_fk = usuario_fk;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "idHorario=" + idHorario +
                ", empleado='" + empleado + '\'' +
                ", correoEmpleado='" + correoEmpleado + '\'' +
                ", fecha='" + fecha + '\'' +
                ", centroTrabajo='" + centroTrabajo + '\'' +
                ", horaEntrada='" + horaEntrada + '\'' +
                ", horaSalida='" + horaSalida + '\'' +
                ", fichaEntrada='" + fichaEntrada + '\'' +
                ", fichaSalida='" + fichaSalida + '\'' +
                '}';
    }
}
