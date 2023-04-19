package com.example.timetowork;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityUsuarioSesionBinding;
import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.HorarioService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.O)
public class UsuarioSesion extends AppCompatActivity {
    LocalDate fecha;
    LocalTime hora;
    private String horaEntrada, horaSalida, currentDate, currentTime;
    ActivityUsuarioSesionBinding bindingSesion;
    private boolean fichadoEntrada, fichadoSalida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        fecha = LocalDate.now();
        currentDate =" " + fecha; //obteniendo la fecha actual con el formato especificado
        bindingSesion.textFechaUsrProfile.append(currentDate); //añadimos la fecha al textview
        bindingSesion.btnPerfil.setOnClickListener(v -> {
            Intent intentPerfil;
            if(usuarioIntent.isEsAdmin()) {
                Log.d("EsAdmin", "EsAdmin: " + usuarioIntent.isEsAdmin());
                intentPerfil = new Intent(UsuarioSesion.this, PerfilAdmin.class);
            }
            else{
                Log.d("EsAdmin", "EsAdmin: " + usuarioIntent.isEsAdmin());
                intentPerfil = new Intent(UsuarioSesion.this, PerfilEmpleado.class);
            }
            intentPerfil.putExtra("usuario", usuarioIntent);
            startActivity(intentPerfil);
        });
        obtenerFicha(usuarioIntent);
        bindingSesion.btnEntradaUsrProfile.setOnClickListener(v -> { //evento al clicar el boton de fichar entrada
            if (!fichadoEntrada) { //si ya se ha fichado la entrada no se puede volver a fichar
                ficharEntrada(usuarioIntent);
            }
        });
        bindingSesion.btnSalidaUsrProfile.setOnClickListener(v -> { //evento al clicar el boton de fichar salida
            if(!fichadoSalida) { //si ya se ha fichado la salida no se puede volver a fichar
                ficharSalida(usuarioIntent);
            }
        });
        bindingSesion.btnHorario.setOnClickListener(v -> {
            Intent intentHorarios = new Intent(UsuarioSesion.this, HorarioSelect.class);
            intentHorarios.putExtra("usuario", usuarioIntent);
            intentHorarios.putExtra("mes", 0);
            startActivity(intentHorarios);

        });
        bindingSesion.btnMensajes.setOnClickListener(v -> {

        });
        bindingSesion.btnCerrarSesionUsrProfile.setOnClickListener(v -> {
            Intent intentCerrarSesion = new Intent(UsuarioSesion.this, MainActivity.class);
            startActivity(intentCerrarSesion);
        });
    }
    private void obtenerFicha(Usuario usuario){
        Horario horario = modeloHorario(usuario);
        HorarioService horarioService = Apis.getHorarioService();
        Call<ArrayList<Horario>> call = horarioService.obtenerFichar(horario);
        call.enqueue(new Callback<ArrayList<Horario>>() {
            @Override
            public void onResponse(Call<ArrayList<Horario>> call, Response<ArrayList<Horario>> response) {
                if(response.body().size()!=0) {
                    Log.d("Ficha", "Hora Entrada: " + response.body().get(0).getFichaEntrada() + " Hora Salida: " + response.body().get(0).getFichaSalida());
                    bindingSesion.textHoraInUsrProfile.setText("Hora Entrada: " + response.body().get(0).getFichaEntrada());
                    if (response.body().get(0).getFichaEntrada() == null) {
                        fichadoEntrada = false;
                    } else {
                        fichadoEntrada = true;
                    }
                    bindingSesion.textHoraOutUsrProfile.setText("Hora Salida: " + response.body().get(0).getFichaSalida());
                    if (response.body().get(0).getFichaSalida() == null) {
                        fichadoSalida = false;
                    } else {
                        fichadoSalida = true;
                    }
                }else {
                    Log.d("Ficha", "Horario no conseguido");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Horario>> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this,"Horario no conseguido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ficharSalida(Usuario usuario) {
        hora = LocalTime.now();
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm:ss");
        fecha = LocalDate.now();
        horaSalida =" " + hora.format(formatoHora);
        bindingSesion.textHoraOutUsrProfile.setText("Hora Salida: " + horaSalida); //añadimos la hora de salida al textview
        fichadoSalida = true;
        Horario horario = modeloHorario(usuario);
        HorarioService horarioService = Apis.getHorarioService();
        Call<Void> call = horarioService.ficharSalida(horario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(UsuarioSesion.this, "Fichado salida", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Fallo al fichar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Horario modeloHorario(Usuario usuario) {
        hora = LocalTime.now();
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm:ss");
        fecha = LocalDate.now();
        Horario horario = new Horario();
        horario.setEmpleado(usuario.getNombreUsuario() + " " +usuario.getApellidosUsuario());
        horario.setCorreoEmpleado(usuario.getCorreoUsuario());
        horario.setCentroTrabajo(usuario.getLugarTrabajo());
        horario.setUsuario_fk(usuario);
        horario.setFecha(fecha.toString());
        horario.setFichaEntrada(String.valueOf(hora));
        horario.setFichaSalida(String.valueOf(hora));
        return horario;
    }

    private void ficharEntrada(Usuario usuario) {
        hora = LocalTime.now();
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm:ss");
        fecha = LocalDate.now();
        horaEntrada =" " + hora.format(formatoHora);
        bindingSesion.textHoraInUsrProfile.setText("Hora Entrada: " + horaEntrada); //añadimos la hora de entrada al textview
        fichadoEntrada= true;
        Horario horario = modeloHorario(usuario);
        HorarioService horarioService = Apis.getHorarioService();
        Call<Void> call = horarioService.ficharEntrada(horario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(UsuarioSesion.this, "Fichado entrada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Fallo al fichar", Toast.LENGTH_SHORT).show();
            }
        });

    }
}