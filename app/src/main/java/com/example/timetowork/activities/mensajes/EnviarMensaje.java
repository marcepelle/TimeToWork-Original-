package com.example.timetowork.activities.mensajes;

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

        bindingEnvMens = ActivityEnviarMensajeBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View viewEnvMens = bindingEnvMens.getRoot(); //referencia a la vista raíz
        setContentView(viewEnvMens); //para que sea la vista activa en la pantalla

        Bundle bundleEnvMens = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        usuarioIntent = (Usuario) bundleEnvMens.getSerializable("usuario"); //usuario de la sesión
        correosSpinner = (ArrayList<ArrayList<String>>) bundleEnvMens.getSerializable("CorreosSpinner"); //obtenemos el listado de correos por centro(ordenados del mismo modo que el array centrosSpinner)
        posicionCentro = Integer.valueOf(bundleEnvMens.get("posicionCentro").toString()); //posición del item seleccionado en el spinner de los centros de trabajo
        posicionCorreo = Integer.valueOf(bundleEnvMens.get("posicionCorreo").toString()); //posición del item seleccionado en el spinner de los correos de los usuarios
        centrosSpinner = bundleEnvMens.getStringArray("CentrosSpinner"); //recogemos lo valores del array que contiene los datos de los centros

        ObtenerEnviados(usuarioIntent); //Obtenemos lo mensajes envíados para el usuario pasado
        ObtenerRecibidos(usuarioIntent); //Obtenemos lo mensajes recibidos para el usuario pasado

        bindingEnvMens.txtEmpleadoEnvMens.setText((usuarioIntent.isEsAdmin())?usuarioIntent.getNombreUsuario() : usuarioIntent.getNombreUsuario() + " " + usuarioIntent.getApellidosUsuario()); //Insertamos en el TextView el nombre y los apellidos del usuario de la sesión que va a ser quien envie el mensaje

        bindingEnvMens.spinnerCentroTrabajoEnvMens.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, centrosSpinner)); //fijamos el adaptador para mostrar la información de los centros disponibles en el spinner
        bindingEnvMens.spinnerCentroTrabajoEnvMens.setSelection(posicionCentro); //fijamos el item seleccionado del spinner del centro pasandole la posición

        bindingEnvMens.spinnerCorreosEnvMens.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get(posicionCentro))); //fijamos el adaptador para mostrar la información de los correos disponibles en el spinner
        bindingEnvMens.spinnerCorreosEnvMens.setSelection(posicionCorreo); //fijamos el item seleccionado del spinner de los correos pasandole la posición
        bindingEnvMens.spinnerCentroTrabajoEnvMens.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //cada vez que se cambie el item seleccionado del centro de trabajo cambiaran los datos del spinner de los correos de los empleados
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedCentro){ //impedimos que la primera vez que se inicie el activity se ejecute, al estar ya fijado el adaptador
                    selectedCentro = true;
                    return;
                }
                bindingEnvMens.spinnerCorreosEnvMens.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get(position))); //la posición del ArrayList correosSpinner será la misma seleccionada en el spinner de los centros de trabajo, mostrando la lista de correos para ese centro
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bindingEnvMens.btnEnviarEnvMens.setOnClickListener(v -> { //Botón envíar Mensaje, el usuario de la sesión envviará un mensaje, acción al hacer clic
            Mensaje mensaje = modeloMensaje(usuarioIntent); //creamos un objeto mensaje
            enviarMensaje(mensaje); //Enviamos el mensaje pasado por parámetro
        });

        bindingEnvMens.btnVolverEnvMens.setOnClickListener(v -> { //Botón volver, volvemos al activity MensajesPerfil, acción al hacer clic
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

    private void enviarMensaje(Mensaje mensaje) { //Envíamos el mensaje pasado por parámetro
        MensajeService mensajeService = Apis.getMensajeService();
        Call<Void> call = mensajeService.crearMensaje(mensaje); //Hacemos una llamada a la Api para que inserte en la base de datos el mensaje pasado
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(EnviarMensaje.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                ObtenerEnviados(usuarioIntent); //Obtenemos lo mensajes envíados para el usuario pasado
                ObtenerRecibidos(usuarioIntent); //Obtenemos lo mensajes recibidos para el usuario pasado
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EnviarMensaje.this, "Mensaje no enviado", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private Mensaje modeloMensaje(Usuario usuarioIntent) { //Devuelve un objeto mensaje del usuario pasado
        Mensaje mensaje = new Mensaje(); //Creamos un objeto mensaje
        mensaje.setAsunto(bindingEnvMens.spinnerAsuntoEnvMens.getSelectedItem().toString()); //Fijamos el asunto con el item seleccionado en el spinner de los asuntos
        mensaje.setDe(usuarioIntent.getCorreoUsuario()); //fijamos el correo de la persona que va a enviar el mensaje
        mensaje.setPara(bindingEnvMens.spinnerCorreosEnvMens.getSelectedItem().toString()); //Fijamos el correo con el item seleccionado en el spinner de los correos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mensaje.setFecha(LocalDate.now().toString()); //fijamos la fecha de envío
            mensaje.setHora(LocalTime.now().toString()); //fijamos la hora de envío
        }
        mensaje.setCentroDe(usuarioIntent.getLugarTrabajo()); //fijamos el centro de trabajo de la persona que envía el mensaje
        mensaje.setCentroPara(bindingEnvMens.spinnerCentroTrabajoEnvMens.getSelectedItem().toString()); //fijamos el centro de trabajo con el item seleccionado en el spinner de los centros de trabajo
        mensaje.setNomEmpresa(usuarioIntent.getEmpresaUsuario()); //fijamos el nombre de la empresa del usuario que envía el mensaje
        mensaje.setContenido(bindingEnvMens.editMensajeEnvMens.getText().toString());  //Fijamos el contenido del mensaje con la información que se ha escrito en el EditText
        mensaje.setUsuario_fk(usuarioIntent); //fijamos el usuario que envía el mensaje
        return mensaje; //devolvemos el mensaje
    }

    private void ObtenerEnviados(Usuario usuarioIntent) { //Obtenemos la lista de mensajes envíados para el usuario pasado
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getEnviados(usuarioIntent.getCorreoUsuario()); //hacemos una llamada a la Api para obtener el listado de mensajes envíados
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){ //Si la respuesta no está vacía
                    enviados = response.body(); //Rellenamos el ArrayList de mensajes envíados con la respuesta de la llamada a la Api
                    return;
                }
                enviados = new ArrayList<Mensaje>(); //Si está vacía inicializamos el Arraylist
            }
            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {
            }
        });
    }

    private void ObtenerRecibidos(Usuario usuarioIntent) { //Obtenemos la lista de mensajes recibidos para el usuario pasado
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getRecibidos(usuarioIntent.getCorreoUsuario()); //hacemos una llamada a la Api para obtener el listado de mensajes recibidos
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){ //Si la respuesta no está vacía
                    recibidos = response.body(); //Rellenamos el ArrayList de mensajes recibidos con la respuesta de la llamada a la Api
                    return;
                }
                recibidos = new ArrayList<Mensaje>(); //Si está vacía inicializamos el Arraylist
            }
            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {
            }
        });
    }
}