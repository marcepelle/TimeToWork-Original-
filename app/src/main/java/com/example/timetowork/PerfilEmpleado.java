package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityPerfilEmpleadoBinding;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilEmpleado extends AppCompatActivity {

    ActivityPerfilEmpleadoBinding bindingPerfilEmpleado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingPerfilEmpleado = ActivityPerfilEmpleadoBinding.inflate(getLayoutInflater());
        View view = bindingPerfilEmpleado.getRoot();
        setContentView(view);
        Bundle bundlePerEmpin = getIntent().getExtras();
        Usuario usuarioIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundlePerEmpin.getSerializable("usuario", Usuario.class);
        }
        else{
            usuarioIntent = new Usuario();
        }
        bindingPerfilEmpleado.editNombAdminPerEmp.setText(usuarioIntent.getNombreUsuario());
        bindingPerfilEmpleado.editCorreoPerEmp.setText(usuarioIntent.getCorreoUsuario());
        bindingPerfilEmpleado.editCentroTrabajoPerEmp.setText(usuarioIntent.getLugarTrabajo());
        bindingPerfilEmpleado.editTelefonoPerEmp.setText(String.valueOf(usuarioIntent.getTelefono()));

        bindingPerfilEmpleado.btnEstabDatosPerEmp.setOnClickListener(v -> {
            usuarioIntent.setNombreUsuario(String.valueOf(bindingPerfilEmpleado.editNombAdminPerEmp.getText()));
            usuarioIntent.setCorreoUsuario(String.valueOf(bindingPerfilEmpleado.editCorreoPerEmp.getText()));
            usuarioIntent.setTelefono(Integer.valueOf(String.valueOf(bindingPerfilEmpleado.editTelefonoPerEmp.getText())));
            usuarioIntent.setLugarTrabajo(String.valueOf(bindingPerfilEmpleado.editCentroTrabajoPerEmp.getText()));
            actualizarUsuario(usuarioIntent);
        });
        bindingPerfilEmpleado.btnInformeHorasPerEmp.setOnClickListener(v -> {
            /*
            Intent intentGestionUusario = new Intent(PerfilEmpleado.this, );
            intentGestionUusario.putExtra("usuario", usuarioIntent);
            startActivity(intentGestionUusario);
             */
        });

        bindingPerfilEmpleado.btnVolverPerEmp.setOnClickListener(v -> {
            Intent intentVolver = new Intent(PerfilEmpleado.this, UsuarioSesion.class);
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
                Toast.makeText(PerfilEmpleado.this, "Usuario Actualizado", Toast.LENGTH_SHORT).show();
                Intent actualizarIntent  = new Intent(PerfilEmpleado.this, PerfilEmpleado.class);
                actualizarIntent.putExtra("usuario", response.body()); //habiendo implementado la interfaz serializable puedo pasar un objeto a otra activity
                Log.d("ResUsuario", "Usuario id:" + response.body().getIdUsuario() + response.body().getNombreUsuario());
                startActivity(actualizarIntent);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilEmpleado.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}