package com.example.timetowork.activities.mensajes;

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

import com.example.timetowork.R;
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

        bindingResMens = ActivityResponderMensajeBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View viewResMens = bindingResMens.getRoot(); //referencia a la vista raíz
        setContentView(viewResMens); // para que sea la vista activa en la pantalla

        Bundle bundleResMens = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        usuarioIntent = (Usuario) bundleResMens.getSerializable("usuario"); //usuario de la sesión
        mensajeIntent = (Mensaje) bundleResMens.getSerializable("mensaje"); //Mensaje recibido por intent al que se va a responder
        correosSpinner = (ArrayList<ArrayList<String>>) bundleResMens.getSerializable("CorreosSpinner"); //obtenemos el listado de correos por centro(ordenados del mismo modo que el array centrosSpinner)
        centrosSpinner = bundleResMens.getStringArray("CentrosSpinner"); //recogemos lo valores del array que contiene los datos de los centros

        ObtenerEnviados(usuarioIntent); //Obtenemos lo mensajes envíados para el usuario pasado
        ObtenerRecibidos(usuarioIntent); //Obtenemos lo mensajes recibidos para el usuario pasado

        bindingResMens.txtFechaRespMens.setText("Fecha: " + mensajeIntent.getFecha()); //Insertamos en el TextView la fecha del mensaje a responder
        bindingResMens.txtDeRespMens.setText("De: " + mensajeIntent.getDe()); //Insertamos en el TextView la persona que envía el mensaje a responder
        bindingResMens.txtParaRespMens.setText("Para: " + mensajeIntent.getPara()); //Insertamos en el TextView la persona que recibe el mensaje a responder
        bindingResMens.txtAsuntoRespMens.setText("Asunto: " + mensajeIntent.getAsunto()); //Insertamos en el TextView el asunto del mensaje a responder
        bindingResMens.editMensajeEnvMens.setText(mensajeIntent.getContenido()); //Insertamos en el EditText el contenido del mensaje a responder

        bindingResMens.btnContestarRespMens.setOnClickListener(v -> { //Botón Contestar, abrimos un dialogo para contestar el mensaje, acción al hacer clic
            AlertDialog dialog = createSimpleDialog(); //Definimos un objeto de tipo AlertDialog y le asignamos una instancia a través del método createSimpleDialog
            dialog.show(); //Mostramos el dialogo en el activity
        });
        bindingResMens.btnVolverRespMens.setOnClickListener(v -> { //Botón volver, volvemos al activity MensajesPerfil, acción al hacer clic
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

    public AlertDialog createSimpleDialog() { //Devuelve un objeto AlertDialog para poder responder el mensaje en cuestión
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Con un elemento Builder podremos definir las partes de la creación de un objeto de clase AlertDialog
        LayoutInflater inflater = this.getLayoutInflater(); //Obtenemos el layout donde se mostrará el dialogo
        View viewAlert = inflater.inflate(R.layout.dialog_responder_mensaje, null); //Creamos la vista en el Layout pasandole por parámetro el Layout que se va a mostrar en la vista
        EditText editMensaje = (EditText) viewAlert.findViewById(R.id.EditMensaje); //Obtenemos el EdiText de la vista del dialogo donde irá el contenido de la respuesta del mensaje
        builder.setView(viewAlert) //En la vista del dialogo...
                .setTitle("Responder Mensaje") //Fijamos el título mostrado en el dialogo
                .setPositiveButton("Envíar", (dialog, which) -> { //En caso de que el usuario pulse enviar...
                            Mensaje mensaje = modeloMensaje(usuarioIntent, mensajeIntent); //Crearemos un objeto mensaje que será la contestación al mensaje
                            mensaje.setContenido("Respuesta a correo en fecha: " + mensajeIntent.getFecha() + "Hora: " + mensajeIntent.getHora() + "\n" + "Mensaje: " + editMensaje.getText().toString()); //Fijamos el contenido del mensaje
                            enviarMensaje(mensaje); //Enviamos el mensaje
                        })
                .setNegativeButton("Cancelar", (dialog, which) -> { //En caso de que el usuario pulse cancelar no se hará nada
                        });

        return builder.create(); //Devolvemos el objeto AlertDialog creandolo con el builder
    }

    private void enviarMensaje(Mensaje mensaje) { //Envíamos el mensaje pasado por parámetro
        MensajeService mensajeService = Apis.getMensajeService();
        Call<Void> call = mensajeService.crearMensaje(mensaje); //Hacemos una llamada a la Api para que inserte en la base de datos el mensaje pasado
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ResponderMensaje.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                ObtenerEnviados(usuarioIntent);  //Obtenemos lo mensajes envíados para el usuario pasado
                ObtenerRecibidos(usuarioIntent); //Obtenemos lo mensajes recibidos para el usuario pasado
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResponderMensaje.this, "Mensaje no enviado", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private Mensaje modeloMensaje(Usuario usuarioIntent, Mensaje mensajeIntent) { //Devuelve un objeto mensaje del usuario pasado y el mensaje pasado que es el mensaje que se va responder
        Mensaje mensaje = new Mensaje(); //Creamos un objeto mensaje
        mensaje.setAsunto(mensajeIntent.getAsunto()); //fijamos el asunto que será el del mesnaje que se va a responder
        mensaje.setDe(usuarioIntent.getCorreoUsuario()); //fijamos el correo de la persona que va a enviar el mensaje
        mensaje.setPara(mensajeIntent.getDe()); //fijamos el correo de la persona que va a recibir el mensaje
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mensaje.setFecha(LocalDate.now().toString()); //fijamos la fecha de envío
            mensaje.setHora(LocalTime.now().toString()); //fijamos la hora de envío
        }
        mensaje.setCentroDe(usuarioIntent.getLugarTrabajo()); //fijamos el centro de trabajo de la persona que envía el mensaje
        mensaje.setCentroPara(mensajeIntent.getCentroDe()); //fijamos el centro de trabajo de la persona que recibe el mensaje
        mensaje.setNomEmpresa(usuarioIntent.getEmpresaUsuario()); //fijamos el nombre de la empresa del usuario que envía el mensaje
        mensaje.setUsuario_fk(usuarioIntent); //fijamos el usuario que envía el mensaje
        return mensaje; //devolvemos el mensaje
    }

    private void ObtenerEnviados(Usuario usuarioIntent) { //Obtenemos la lista de mensajes envíados para el usuario pasado
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getEnviados(usuarioIntent); //hacemos una llamada a la Api para obtener el listado de mensajes envíados
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){ //Si la respuesta no está vacía
                    enviados = response.body(); //Rellenamos el ArrayList de mensajes envíados con la respuesta de la llamada a la Api
                    return;
                }
                enviados = new ArrayList<Mensaje>();  //Si está vacía inicializamos el Arraylist
            }
            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {
            }
        });
    }

    private void ObtenerRecibidos(Usuario usuarioIntent) { //Obtenemos la lista de mensajes recibidos para el usuario pasado
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getRecibidos(usuarioIntent); //hacemos una llamada a la Api para obtener el listado de mensajes recibidos
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){ //Si la respuesta no está vacía
                    recibidos = response.body(); //Rellenamos el ArrayList de mensajes recibidos con la respuesta de la llamada a la Api
                    return;
                }
                recibidos = new ArrayList<Mensaje>();  //Si está vacía inicializamos el Arraylist
            }
            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {
            }
        });
    }
}