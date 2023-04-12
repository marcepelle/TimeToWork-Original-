package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


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
        bindingPerfilAdmin = ActivityPerfilAdminBinding.inflate(getLayoutInflater());
        View view = bindingPerfilAdmin.getRoot();
        setContentView(view);
        Bundle bundlePerAdmin = getIntent().getExtras();
        Usuario usuarioIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundlePerAdmin.getSerializable("usuario", Usuario.class);
        }
        else{
            usuarioIntent = new Usuario();
        }
        bindingPerfilAdmin.editNombAdminPerAdm.setText(usuarioIntent.getNombreUsuario());
        bindingPerfilAdmin.editCorreoPerAdm.setText(usuarioIntent.getCorreoUsuario());
        bindingPerfilAdmin.editCentroTrabajoPerAdm.setText(usuarioIntent.getLugarTrabajo());
        bindingPerfilAdmin.editTelefonoPerAdm.setText(String.valueOf(usuarioIntent.getTelefono()));

        bindingPerfilAdmin.btnEstabDatosPerAdm.setOnClickListener(v -> {
            usuarioIntent.setNombreUsuario(String.valueOf(bindingPerfilAdmin.editNombAdminPerAdm.getText()));
            usuarioIntent.setCorreoUsuario(String.valueOf(bindingPerfilAdmin.editCorreoPerAdm.getText()));
            usuarioIntent.setTelefono(Integer.valueOf(String.valueOf(bindingPerfilAdmin.editTelefonoPerAdm.getText())));
            usuarioIntent.setLugarTrabajo(String.valueOf(bindingPerfilAdmin.editCentroTrabajoPerAdm.getText()));
            actualizarUsuario(usuarioIntent);
        });
        bindingPerfilAdmin.btnGestionUsuariosPerAdm.setOnClickListener(v -> {
            Intent intentGestionUusario = new Intent(PerfilAdmin.this, GestionUsuarios.class);
            intentGestionUusario.putExtra("usuario", usuarioIntent);
            startActivity(intentGestionUusario);
        });

        bindingPerfilAdmin.btnVolverPerAdm.setOnClickListener(v -> {
            Intent intentVolver = new Intent(PerfilAdmin.this, UsuarioSesion.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    private void actualizarUsuario(Usuario usuario) {
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