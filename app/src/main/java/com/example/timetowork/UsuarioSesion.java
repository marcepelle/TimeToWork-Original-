package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityUsuarioSesionBinding;
import com.example.timetowork.models.Usuario;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UsuarioSesion extends AppCompatActivity {
    //DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss z"); permite formatear y analizar fechas con patrones definidos por el usuario para el formato de fecha y hora
    DateFormat dateFormat = DateFormat.getDateInstance();
    //DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss z"); //permite formatear y analizar fechas con patrones definidos por el usuario para el formato de fecha y hora
    DateFormat timeFormat = DateFormat.getTimeInstance();
    private String horaEntrada, horaSalida, currentDate, currentTime;
    ActivityUsuarioSesionBinding bindingSesion;
    private boolean fichadoEntrada, fichadoSalida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_sesion);
        bindingSesion = ActivityUsuarioSesionBinding.inflate(getLayoutInflater());
        View view = bindingSesion.getRoot();
        setContentView(view);
        Bundle intentObtenido = getIntent().getExtras();
        Usuario usuarioIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = intentObtenido.getSerializable("usuario", Usuario.class);
        }
        else {
            usuarioIntent = new Usuario();
            Toast.makeText(this, "Sin sesion, Version Android desactualizada", Toast.LENGTH_SHORT).show();
        }
        bindingSesion.textTitleUsrProfile.append(usuarioIntent.getNombreUsuario());
        currentDate =" " + dateFormat.format(new Date()); //obteniendo la fecha actual con el formato especificado
        bindingSesion.textFechaUsrProfile.append(currentDate); //añadimos la fecha al textview
        bindingSesion.btnEntradaUsrProfile.setOnClickListener(v -> { //evento al clicar el boton de fichar entrada
           if (!fichadoEntrada) { //si ya se ha fichado la entrada no se puede volver a fichar
               ficharEntrada();
           }
        });
        bindingSesion.btnSalidaUsrProfile.setOnClickListener(v -> { //evento al clicar el boton de fichar salida
            if(!fichadoSalida) { //si ya se ha fichado la salida no se puede volver a fichar
                ficharSalida();
            }
        });
        bindingSesion.btnPerfil.setOnClickListener(v -> {
            Intent intentPerfil;
            if(usuarioIntent.isEsAdmin()) {
                intentPerfil = new Intent(UsuarioSesion.this, PerfilAdmin.class);
            }
            else{
                intentPerfil = new Intent(UsuarioSesion.this, PerfilEmpleado.class);
            }
            intentPerfil.putExtra("usuario", usuarioIntent);
            startActivity(intentPerfil);
        });
        bindingSesion.btnHorario.setOnClickListener(v -> {
            //Intent intentHorarios = new Intent(UsuarioPerfil.this, Horarios.class);
            //startActivity(intentHorarios);

        });
    }

    private void ficharSalida() {
        currentTime =" " + timeFormat.format(new Date()); //obteniendo la hora actual con el formato especificado
        horaSalida =" " + currentTime;
        bindingSesion.textHoraOutUsrProfile.append(horaSalida); //añadimos la hora de salida al textview
        fichadoSalida = true;
    }

    private void ficharEntrada() {
        currentTime = timeFormat.format(new Date()); //obteniendo la hora actual con el formato especificado
        horaEntrada =" " + currentTime;
        bindingSesion.textHoraInUsrProfile.append(horaEntrada); //añadimos la hora de entrada al textview
        fichadoEntrada= true;
    }
}