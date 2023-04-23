package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityEnviarMensajeBinding;
import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.MensajeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnviarMensaje extends AppCompatActivity {
    ActivityEnviarMensajeBinding bindingEnvMens;
    Usuario usuarioIntent;
    ArrayList<Mensaje> recibidos;
    ArrayList<Mensaje> enviados;
    ArrayList<ArrayList<String>> correosSpinner;
    String[] centrosSpinner;
    int posicionCentro;
    int posicionCorreo;
    boolean selectedCentro = false;
    boolean selectedEmpleado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingEnvMens = ActivityEnviarMensajeBinding.inflate(getLayoutInflater());
        View viewEnvMens = bindingEnvMens.getRoot();
        setContentView(viewEnvMens);
        Bundle bundleEnvMens = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundleEnvMens.getSerializable("usuario", Usuario.class);
            correosSpinner =  bundleEnvMens.getSerializable("CorreosSpinner", ArrayList.class);
            recibidos = (ArrayList<Mensaje>)  bundleEnvMens.getSerializable("mensajesRecibidos");
            enviados = (ArrayList<Mensaje>) bundleEnvMens.getSerializable("mensajesEnviados");
        }
        posicionCentro = Integer.valueOf(bundleEnvMens.get("posicionCentro").toString());
        posicionCorreo = Integer.valueOf(bundleEnvMens.get("posicionCorreo").toString());
        centrosSpinner = bundleEnvMens.getStringArray("CentrosSpinner");
        bindingEnvMens.txtEmpleadoEnvMens.setText(usuarioIntent.getNombreUsuario() + " " + usuarioIntent.getApellidosUsuario());
        bindingEnvMens.spinnerCentroTrabajoEnvMens.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, centrosSpinner));
        bindingEnvMens.spinnerCentroTrabajoEnvMens.setSelection((Integer) bundleEnvMens.get("posicionCentro"));
        bindingEnvMens.spinnerCorreosEnvMens.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get((Integer) bundleEnvMens.get("posicionCentro"))));
        bindingEnvMens.spinnerCorreosEnvMens.setSelection((Integer) bundleEnvMens.get("posicionCorreo"));
        bindingEnvMens.spinnerCentroTrabajoEnvMens.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedCentro){
                    selectedCentro = true;
                    return;
                }
                bindingEnvMens.spinnerCorreosEnvMens.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bindingEnvMens.spinnerCorreosEnvMens.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!selectedEmpleado) {
                    selectedEmpleado = true;
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        bindingEnvMens.btnEnviarEnvMens.setOnClickListener(v -> {
            Mensaje mensaje = modeloHorario(usuarioIntent, bindingEnvMens);
            enviarMensaje(mensaje);
        });
        bindingEnvMens.btnVolverEnvMens.setOnClickListener(v -> {
            Intent intentVolver = new Intent(EnviarMensaje.this, MensajesPerfil.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            intentVolver.putExtra("posicionCentro", posicionCentro);
            intentVolver.putExtra("posicionCorreo", posicionCorreo);
            intentVolver.putExtra("CorreosSpinner", correosSpinner);
            intentVolver.putExtra("CentrosSpinner", centrosSpinner);
            intentVolver.putExtra("mensajesRecibidos", recibidos);
            intentVolver.putExtra("mensajesEnviados", enviados);
            startActivity(intentVolver);
        });
    }

    private void enviarMensaje(Mensaje mensaje) {
        MensajeService mensajeService = Apis.getMensajeService();
        Call<Void> call = mensajeService.crearMensaje(mensaje);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(EnviarMensaje.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                ObtenerEnviados(usuarioIntent);
                ObtenerRecibidos(usuarioIntent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EnviarMensaje.this, "Mensaje no enviado", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private Mensaje modeloHorario(Usuario usuarioIntent, ActivityEnviarMensajeBinding bindingEnvMens) {
        Mensaje mensaje = new Mensaje();
        mensaje.setAsunto(bindingEnvMens.spinnerAsuntoEnvMens.getSelectedItem().toString());
        mensaje.setDe(usuarioIntent.getCorreoUsuario());
        mensaje.setPara(bindingEnvMens.spinnerCorreosEnvMens.getSelectedItem().toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mensaje.setFecha(LocalDate.now().toString());
            mensaje.setHora(LocalTime.now().toString());
        }
        mensaje.setCentroDe(usuarioIntent.getLugarTrabajo());
        mensaje.setCentroPara(bindingEnvMens.spinnerCentroTrabajoEnvMens.getSelectedItem().toString());
        mensaje.setNomEmpresa(usuarioIntent.getEmpresaUsuario());
        mensaje.setContenido(bindingEnvMens.editMensajeEnvMens.getText().toString());
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