package com.example.timetowork.activities.mensajes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.timetowork.activities.UsuarioSesion;
import com.example.timetowork.adapters.MensajesAdapter;
import com.example.timetowork.databinding.ActivityMensajesPerfilBinding;
import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

public class MensajesPerfil extends AppCompatActivity {

    
    ActivityMensajesPerfilBinding bindingMens;
    ArrayList<Mensaje> recibidos = new ArrayList<Mensaje>();
    ArrayList<Mensaje> enviados = new ArrayList<Mensaje>();
    ArrayList<ArrayList<String>> correosSpinner = new ArrayList<>();
    String[] centrosSpinner;

    int posicionCentro;
    int posicionCorreo;
    Usuario usuarioIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindingMens = ActivityMensajesPerfilBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View viewMens = bindingMens.getRoot(); //referencia a la vista raíz
        setContentView(viewMens); //para que sea la vista activa en la pantalla

        Bundle bundleMens = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        usuarioIntent = (Usuario) bundleMens.getSerializable("usuario"); //usuario de la sesión
        correosSpinner = (ArrayList<ArrayList<String>>) bundleMens.getSerializable("CorreosSpinner"); //obtenemos el listado de correos por centro(ordenados del mismo modo que el array centrosSpinner)
        centrosSpinner = bundleMens.getStringArray("CentrosSpinner"); //recogemos lo valores del array que contiene los datos de los centros
        recibidos = (ArrayList<Mensaje>)  bundleMens.getSerializable("mensajesRecibidos"); //Obtenemos el listado de mensajes recibidos
        enviados = (ArrayList<Mensaje>) bundleMens.getSerializable("mensajesEnviados"); //Obtenemos el listado de mensajes envíados
        posicionCentro = Integer.valueOf(bundleMens.get("posicionCentro").toString()); //posición del item seleccionado en el spinner de los centros de trabajo
        posicionCorreo = Integer.valueOf(bundleMens.get("posicionCorreo").toString()); //posición del item seleccionado en el spinner de los correos de los usuarios


        bindingMens.listaMensajesMens.setLayoutManager(new LinearLayoutManager(this)); //fijamos el layout que organizará las vistas para el RecyclerView listaMensajes

        bindingMens.btnRecibidosMens.setOnClickListener(v -> { //Botón Recibidos, rellenaremos con el listado de recibidos el recyclerView, acción al hacer clic
            bindingMens.listaMensajesMens.setAdapter(new MensajesAdapter(this, recibidos, usuarioIntent, correosSpinner, centrosSpinner)); //fijamos el adaptador del recyclerview con los mensajes recibidos
        });

        bindingMens.btnEnviadosMens.setOnClickListener(v -> { //Botón Envíados, rellenaremos con el listado de envíados el recyclerView, acción al hacer clic
            bindingMens.listaMensajesMens.setAdapter(new MensajesAdapter(this, enviados, usuarioIntent, correosSpinner, centrosSpinner)); //fijamos el adaptador del recyclerview con los mensajes envíados
        });

        bindingMens.floatingActionButton.setOnClickListener(v -> { //Botón flotante, hacemos un intent al activity EnviarMensaje para poder envíar un mensaje nuevo, acción al hacer clic
            Intent intentMens = new Intent(MensajesPerfil.this, EnviarMensaje.class);
            intentMens.putExtra("usuario", usuarioIntent);
            intentMens.putExtra("posicionCentro", posicionCentro);
            intentMens.putExtra("posicionCorreo", posicionCorreo);
            intentMens.putExtra("CorreosSpinner", correosSpinner);
            intentMens.putExtra("CentrosSpinner", centrosSpinner);
            startActivity(intentMens);
        });

        bindingMens.btnVolverMens.setOnClickListener(v -> { //Botón volver, hacemos un intent para volver al activity UsuarioSesion, acción al hacer clic
            Intent intentVolver = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intentVolver = new Intent(MensajesPerfil.this, UsuarioSesion.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }
}