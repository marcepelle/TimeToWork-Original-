package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

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
        bindingMens = ActivityMensajesPerfilBinding.inflate(getLayoutInflater());
        View viewMens = bindingMens.getRoot();
        setContentView(viewMens);
        Bundle bundleMens = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundleMens.getSerializable("usuario", Usuario.class);
            correosSpinner =  bundleMens.getSerializable("CorreosSpinner", ArrayList.class);
            recibidos = (ArrayList<Mensaje>)  bundleMens.getSerializable("mensajesRecibidos");
            enviados = (ArrayList<Mensaje>) bundleMens.getSerializable("mensajesEnviados");
        }
        centrosSpinner = bundleMens.getStringArray("CentrosSpinner");
        bindingMens.listaMensajesMens.setLayoutManager(new LinearLayoutManager(this));
        bindingMens.btnRecibidosMens.setOnClickListener(v -> {
            bindingMens.listaMensajesMens.setAdapter(new MensajesAdapter(this, recibidos, usuarioIntent));
        });
        bindingMens.btnEnviadosMens.setOnClickListener(v -> {
            bindingMens.listaMensajesMens.setAdapter(new MensajesAdapter(this, enviados, usuarioIntent));
        });
        bindingMens.floatingActionButton.setOnClickListener(v -> {
            Intent intentMens = new Intent(MensajesPerfil.this, EnviarMensaje.class);
            intentMens.putExtra("usuario", usuarioIntent);
            intentMens.putExtra("posicionCentro", posicionCentro);
            intentMens.putExtra("posicionEmpleado", posicionCorreo);
            intentMens.putExtra("CorreosSpinner", correosSpinner);
            intentMens.putExtra("CentrosSpinner", centrosSpinner);
            intentMens.putExtra("mensajesRecibidos", recibidos);
            intentMens.putExtra("mensajesEnviados", enviados);
            startActivity(intentMens);
        });
        bindingMens.btnVolverMens.setOnClickListener(v -> {
            Intent intentVolver = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intentVolver = new Intent(MensajesPerfil.this, UsuarioSesion.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }
}