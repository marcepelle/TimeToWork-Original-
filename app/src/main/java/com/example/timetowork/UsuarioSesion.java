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
import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.HorarioService;
import com.example.timetowork.utils.MensajeService;
import com.example.timetowork.utils.UsuarioService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

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
    ArrayList<ArrayList<String>> correoUsuarios;

    ArrayList<Mensaje> mensajesRecibidos;
    ArrayList<Mensaje> mensajesEnviados;
    String[] centrosUsuarios;
    int posicionCentro;
    int posicionCorreo;

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
        CorreoCentroUsuariosParaSpinner(usuarioIntent);
        ObtenerRecibidos(usuarioIntent);
        ObtenerEnviados(usuarioIntent);
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
            Log.d("ResIntent", correoUsuarios.toString());
            intentHorarios.putExtra("usuario", usuarioIntent);
            intentHorarios.putExtra("usuarioSpinner", usuarioIntent);
            intentHorarios.putExtra("mes", 0);
            intentHorarios.putExtra("posicionCentro", posicionCentro);
            intentHorarios.putExtra("posicionEmpleado", posicionCorreo);
            intentHorarios.putExtra("CorreosSpinner", correoUsuarios);
            intentHorarios.putExtra("CentrosSpinner", centrosUsuarios);
            startActivity(intentHorarios);
        });
        bindingSesion.btnMensajes.setOnClickListener(v -> {
            Intent intentMensajes = new Intent(UsuarioSesion.this, MensajesPerfil.class);
            intentMensajes.putExtra("usuario", usuarioIntent);
            intentMensajes.putExtra("posicionCentro", posicionCentro);
            intentMensajes.putExtra("posicionEmpleado", posicionCorreo);
            intentMensajes.putExtra("CorreosSpinner", correoUsuarios);
            intentMensajes.putExtra("CentrosSpinner", centrosUsuarios);
            intentMensajes.putExtra("mensajesRecibidos", mensajesRecibidos);
            intentMensajes.putExtra("mensajesEnviados", mensajesEnviados);
            startActivity(intentMensajes);

        });
        bindingSesion.btnCerrarSesionUsrProfile.setOnClickListener(v -> {
            Intent intentCerrarSesion = new Intent(UsuarioSesion.this, MainActivity.class);
            startActivity(intentCerrarSesion);
        });
    }

    private void ObtenerEnviados(Usuario usuarioIntent) {
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getEnviados(usuarioIntent);
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){
                    mensajesEnviados = response.body();
                    return;
                }
                mensajesEnviados = new ArrayList<Mensaje>();
            }

            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {

            }
        });
    }

    private void ObtenerRecibidos(Usuario usuarioIntent) {
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getRecibidos(usuarioIntent);
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){
                    mensajesRecibidos = response.body();
                    return;
                }
                mensajesRecibidos= new ArrayList<Mensaje>();
            }

            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {

            }
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

    private void CorreoCentroUsuariosParaSpinner(Usuario usuario) {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<ArrayList<Usuario>> call = usuarioService.listarUsuarios(usuario);
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                if(response.body().size()!=0) {
                    listarCentroYCorreos(response.body(), usuario);
                    return;
                }
                centrosUsuarios = new String[]{"vacio"};
                correoUsuarios = new ArrayList<ArrayList<String>>();
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Lista Correos no Obtenida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listarCentroYCorreos(ArrayList<Usuario> response, Usuario usuario) {
        String [] arrayAuxCentros = new String[response.size()];
        Log.d("ResBody", response.toString());
        for (int i = 0; i < response.size(); i++) {
            arrayAuxCentros[i] = response.get(i).getLugarTrabajo();
        }
        centrosUsuarios = Arrays.stream(arrayAuxCentros).distinct().toArray(String[]::new);
        correoUsuarios = new ArrayList<ArrayList<String>>();

        for(int i=0;i<centrosUsuarios.length;i++){
            ArrayList<String> arrayAuxCorreos = new ArrayList<>();
            int contador = 0;
            if(centrosUsuarios[i].equals(usuario.getLugarTrabajo())){
                posicionCentro = i;
            }
            for (int j = 0; j < response.size(); j++) {
                if(centrosUsuarios[i].equals(response.get(j).getLugarTrabajo())) {
                    Log.d("ResCorreoCentro", "cen: " + centrosUsuarios[i] + "res: " + response.get(j).getLugarTrabajo());
                    arrayAuxCorreos.add(response.get(j).getCorreoUsuario());
                    if(response.get(j).getCorreoUsuario().equals(usuario.getCorreoUsuario())){
                        posicionCorreo = contador;
                    }
                    contador++;
                }
            }
            Log.d("ResCorreoCentro", arrayAuxCorreos.toString());
            correoUsuarios.add(arrayAuxCorreos);
        }
        Log.d("ResCorreoCentro", correoUsuarios.toString());
    }
}