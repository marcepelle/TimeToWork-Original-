package com.example.timetowork.activities.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.timetowork.activities.UsuarioSesion;
import com.example.timetowork.databinding.ActivityPerfilAdminBinding;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilAdmin extends AppCompatActivity {

    ActivityPerfilAdminBinding bindingPerfilAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindingPerfilAdmin = ActivityPerfilAdminBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View view = bindingPerfilAdmin.getRoot(); //referencia a la vista raíz
        setContentView(view); // para que sea la vista activa en la pantalla

        Bundle bundlePerAdmin = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        Usuario usuarioIntent = (Usuario) bundlePerAdmin.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent

        fijarEditTexts(usuarioIntent); //rellenamos la información en los EditText con los datos del Usuario de la sesión

        bindingPerfilAdmin.btnEstabDatosPerAdm.setOnClickListener(v -> { //Botón establecer datos, llamamos al método modeloUsuario y al método actualizarUsuario, acción al hacer clic
            modeloUsuario(usuarioIntent);
            actualizarUsuario(usuarioIntent);
        });
        bindingPerfilAdmin.btnGestionUsuariosPerAdm.setOnClickListener(v -> { //Botón gestión usuarios, hacemos un  intent al activity ListadoUsuarios, acción al hacer clic
            Intent intentGestionUusario = new Intent(PerfilAdmin.this, ListadoUsuarios.class);
            intentGestionUusario.putExtra("usuario", usuarioIntent);
            startActivity(intentGestionUusario);
        });

        bindingPerfilAdmin.btnVolverPerAdm.setOnClickListener(v -> { //Botón volver, volvemos al activity UsuarioSesion, acción al hacer clic
            Intent intentVolver = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intentVolver = new Intent(PerfilAdmin.this, UsuarioSesion.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    private void fijarEditTexts(Usuario usuarioIntent) { //fijamos la información del usuario en los EditText
        bindingPerfilAdmin.editNombAdminPerAdm.setText(usuarioIntent.getNombreUsuario());
        bindingPerfilAdmin.editCorreoPerAdm.setText(usuarioIntent.getCorreoUsuario());
        bindingPerfilAdmin.editCentroTrabajoPerAdm.setText(usuarioIntent.getLugarTrabajo());
        bindingPerfilAdmin.editTelefonoPerAdm.setText(String.valueOf(usuarioIntent.getTelefono()));
    }

    private void modeloUsuario(Usuario usuarioIntent) { //cambiamos los datos del usuario con la información de los EditText
        usuarioIntent.setNombreUsuario(String.valueOf(bindingPerfilAdmin.editNombAdminPerAdm.getText()));
        usuarioIntent.setCorreoUsuario(String.valueOf(bindingPerfilAdmin.editCorreoPerAdm.getText()));
        usuarioIntent.setTelefono(Integer.valueOf(String.valueOf(bindingPerfilAdmin.editTelefonoPerAdm.getText())));
        usuarioIntent.setLugarTrabajo(String.valueOf(bindingPerfilAdmin.editCentroTrabajoPerAdm.getText()));
    }

    private void actualizarUsuario(Usuario usuario) { //actualizamos el usuario en la base de datos y actualizamos el activity haciendo un intent
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.actualizarUsuario(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Toast.makeText(PerfilAdmin.this, "Usuario Actualizado", Toast.LENGTH_SHORT).show();
                Intent actualizarIntent  = new Intent(PerfilAdmin.this, PerfilAdmin.class);
                actualizarIntent.putExtra("usuario", response.body()); //habiendo implementado la interfaz serializable puedo pasar un objeto a otra activity
                Log.d("ResUsuario", "Usuario id:" + response.body().getIdUsuario() + response.body().getNombreUsuario());
                startActivity(actualizarIntent);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilAdmin.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}