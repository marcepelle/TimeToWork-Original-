package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.timetowork.databinding.ActivityUsuarioPerfilBinding;
import com.example.timetowork.models.Usuario;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UsuarioPerfil extends AppCompatActivity {
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); //permite formatear y analizar fechas con patrones definidos por el usuario para el formato de fecha y hora
    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss z"); //permite formatear y analizar fechas con patrones definidos por el usuario para el formato de fecha y hora
    private String horaEntrada, horaSalida, currentDate, currentTime;
    ActivityUsuarioPerfilBinding bindingPerfil;
    private boolean fichadoEntrada, fichadoSalida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_perfil);
        bindingPerfil = ActivityUsuarioPerfilBinding.inflate(getLayoutInflater());
        View view = bindingPerfil.getRoot();
        setContentView(view);
        Bundle intentObtenido = getIntent().getExtras();
        String idUsuario = intentObtenido.getString("id");
        bindingPerfil.textTitleUsrProfile.append(idUsuario);
        currentDate =" " + dateFormat.format(new Date()); //obteniendo la fecha actual con el formato especificado
        bindingPerfil.textFechaUsrProfile.append(currentDate); //añadimos la fecha al textview
        bindingPerfil.btnEntradaUsrProfile.setOnClickListener(v -> { //evento al clicar el boton de fichar entrada
           if (!fichadoEntrada) { //si ya se ha fichado la entrada no se puede volver a fichar
               ficharEntrada();
           }
        });
        bindingPerfil.btnSalidaUsrProfile.setOnClickListener(v -> { //evento al clicar el boton de fichar salida
            if(!fichadoSalida) { //si ya se ha fichado la salida no se puede volver a fichar
                ficharSalida();
            }
        });
        bindingPerfil.btnHorario.setOnClickListener(v -> {
            //Intent intentHorarios = new Intent(UsuarioPerfil.this, Horarios.class);
            //startActivity(intentHorarios);
        });
    }

    private void ficharSalida() {
        currentTime =" " + timeFormat.format(new Date()); //obteniendo la hora actual con el formato especificado
        horaSalida =" " + currentTime;
        bindingPerfil.textHoraOutUsrProfile.append(horaSalida); //añadimos la hora de salida al textview
        fichadoSalida = true;
    }

    private void ficharEntrada() {
        currentTime = timeFormat.format(new Date()); //obteniendo la hora actual con el formato especificado
        horaEntrada =" " + currentTime;
        bindingPerfil.textHoraInUsrProfile.append(horaEntrada); //añadimos la hora de entrada al textview
        fichadoEntrada= true;
    }
}