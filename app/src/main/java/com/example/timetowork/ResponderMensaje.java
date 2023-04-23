package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityResponderMensajeBinding;
import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.MensajeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResponderMensaje extends AppCompatActivity {
    ActivityResponderMensajeBinding bindingResMens;
    Usuario usuarioIntent;
    Mensaje mensajeIntent;

    ArrayList<ArrayList<String>> correosSpinner = new ArrayList<>();
    String[] centrosSpinner;

    ArrayList<Mensaje> recibidos;
    ArrayList<Mensaje> enviados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingResMens = ActivityResponderMensajeBinding.inflate(getLayoutInflater());
        View viewResMens = bindingResMens.getRoot();
        setContentView(viewResMens);
        Bundle bundleResMens = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundleResMens.getSerializable("usuario", Usuario.class);
            mensajeIntent = bundleResMens.getSerializable("mensaje", Mensaje.class);
            correosSpinner =  bundleResMens.getSerializable("CorreosSpinner", ArrayList.class);
        }
        centrosSpinner = bundleResMens.getStringArray("CentrosSpinner");
        ObtenerEnviados(usuarioIntent);
        ObtenerRecibidos(usuarioIntent);
        bindingResMens.txtFechaRespMens.setText("Fecha: " + mensajeIntent.getFecha());
        bindingResMens.txtDeRespMens.setText("De: " + mensajeIntent.getDe());
        bindingResMens.txtParaRespMens.setText("Para: " + mensajeIntent.getPara());
        bindingResMens.txtAsuntoRespMens.setText("Asunto: " + mensajeIntent.getAsunto());
        bindingResMens.editMensajeEnvMens.setText(mensajeIntent.getContenido());
        bindingResMens.btnContestarRespMens.setOnClickListener(v -> {
            AlertDialog dialog = createSimpleDialog();
            dialog.show();
        });
        bindingResMens.btnVolverRespMens.setOnClickListener(v -> {
            Intent intentVolver = new Intent(ResponderMensaje.this, MensajesPerfil.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            intentVolver.putExtra("posicionCentro", 0);
            intentVolver.putExtra("posicionCorreo", 0);
            intentVolver.putExtra("CorreosSpinner", correosSpinner);
            intentVolver.putExtra("CentrosSpinner", centrosSpinner);
            intentVolver.putExtra("mensajesRecibidos", recibidos);
            intentVolver.putExtra("mensajesEnviados", enviados);
            startActivity(intentVolver);
        });


    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View viewAlert = inflater.inflate(R.layout.dialog_responder_mensaje, null);
        EditText editMensaje = (EditText) viewAlert.findViewById(R.id.EditMensaje);
        builder.setView(viewAlert)
                .setTitle("Responder Mensaje")
                .setPositiveButton("Envíar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Mensaje mensaje = modeloMensaje(usuarioIntent, bindingResMens, mensajeIntent);
                                mensaje.setContenido("Respuesta a correo en fecha: " + mensajeIntent.getFecha() + "Hora: " + mensajeIntent.getHora() + "/n" + "Mensaje: " + editMensaje.getText().toString());
                                enviarMensaje(mensaje);
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    private void enviarMensaje(Mensaje mensaje) {
        MensajeService mensajeService = Apis.getMensajeService();
        Call<Void> call = mensajeService.crearMensaje(mensaje);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ResponderMensaje.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                ObtenerEnviados(usuarioIntent);
                ObtenerRecibidos(usuarioIntent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResponderMensaje.this, "Mensaje no enviado", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private Mensaje modeloMensaje(Usuario usuarioIntent, ActivityResponderMensajeBinding bindingResMens, Mensaje mensajeIntent) {
        Mensaje mensaje = new Mensaje();
        mensaje.setAsunto(mensajeIntent.getAsunto());
        mensaje.setDe(usuarioIntent.getCorreoUsuario());
        mensaje.setPara(mensajeIntent.getDe());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mensaje.setFecha(LocalDate.now().toString());
            mensaje.setHora(LocalTime.now().toString());
        }
        mensaje.setCentroDe(usuarioIntent.getLugarTrabajo());
        mensaje.setCentroPara(mensajeIntent.getCentroDe());
        mensaje.setNomEmpresa(usuarioIntent.getEmpresaUsuario());
        mensaje.setUsuario_fk(usuarioIntent);
        return mensaje;
    }

    private void ObtenerEnviados(Usuario usuarioIntent) {
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getEnviados(usuarioIntent);
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){
                    enviados = response.body();
                    return;
                }
                enviados = new ArrayList<Mensaje>();
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
                    recibidos = response.body();
                    return;
                }
                recibidos = new ArrayList<Mensaje>();
            }

            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {

            }
        });
    }
}