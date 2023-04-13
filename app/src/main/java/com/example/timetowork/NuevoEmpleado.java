package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityNuevoEmpleadoBinding;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevoEmpleado extends AppCompatActivity {

    ActivityNuevoEmpleadoBinding bindingNuevEmp;
    UsuarioService usuarioService;
    boolean userConseguido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_empleado);
        bindingNuevEmp = ActivityNuevoEmpleadoBinding.inflate(getLayoutInflater());
        View viewNuevEmp = bindingNuevEmp.getRoot();
        setContentView(viewNuevEmp);
        Bundle bundleNuevEmp = getIntent().getExtras();
        Usuario usuarioIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundleNuevEmp.getSerializable("usuario", Usuario.class);
            userConseguido = true;
        }
        else {
            usuarioIntent= new Usuario();
            userConseguido = false;
        }
        bindingNuevEmp.btnCrearCuentaNuevEmp.setOnClickListener(v -> {
            if (userConseguido) {
                crearCuentaUsuario(modeloEmpleado(usuarioIntent));

            }
            else{
                Toast.makeText(this, "Version android insuficiente", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Usuario modeloEmpleado(Usuario usuarioAdmin){
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(String.valueOf(bindingNuevEmp.editNombEmpleadoNuevEmp.getText()));
        usuario.setApellidosUsuario(String.valueOf(bindingNuevEmp.editApellidosNuevEmp.getText()));
        usuario.setTelefono(Integer.parseInt(String.valueOf(bindingNuevEmp.editTelefonoNuevEmp.getText())));
        usuario.setDireccion(String.valueOf(bindingNuevEmp.editDireccionNuevEmp.getText()));
        usuario.setEmpresaUsuario(String.valueOf(usuarioAdmin.getEmpresaUsuario()));
        usuario.setLugarTrabajo(String.valueOf(bindingNuevEmp.editCentroDeTrabajoNuevEmp.getText()));
        usuario.setFechaNacimiento(String.valueOf(bindingNuevEmp.editFechaNacimientoNuevEmp.getText()));
        usuario.setCorreoUsuario(String.valueOf(bindingNuevEmp.editCorreoNuevEmp.getText()));
        usuario.setContrasena(String.valueOf(bindingNuevEmp.editContrasenaNuevEmp.getText()));
        usuario.setEsAdmin(false);
        return usuario;
    }
    private void crearCuentaUsuario(Usuario usuario) {
        usuarioService = Apis.getUsuarioService();
        Call<Void> call= usuarioService.crearUsuario(usuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(NuevoEmpleado.this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                    Log.d("Exito", "Exito al crear cuenta Usuario");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(NuevoEmpleado.this,"Cuenta no creada", Toast.LENGTH_SHORT).show();
                Log.e("Fallo", t.getMessage());
            }
        });
    }
}