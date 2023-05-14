package com.example.timetowork.activities.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.R;
import com.example.timetowork.databinding.ActivityGestionUsuarioBinding;
import com.example.timetowork.databinding.ActivityNuevoEmpleadoBinding;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevoEmpleado extends AppCompatActivity {

    ActivityNuevoEmpleadoBinding bindingNuevEmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindingNuevEmp = ActivityNuevoEmpleadoBinding.inflate(getLayoutInflater());
        View viewNuevEmp = bindingNuevEmp.getRoot();
        setContentView(viewNuevEmp);

        Bundle bundleNuevEmp = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        Usuario usuarioIntent =(Usuario) bundleNuevEmp.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent

        bindingNuevEmp.btnCrearCuentaNuevEmp.setOnClickListener(v -> { //Botón crear cuenta nueva, si los EditText no están vacíos llamamos al metodo crearCuentaUsuario y hacemos un intent hacia el activity ListadoUsuarios, acción al ahcer click
                if(!emptyEdits()) {
                    crearCuentaUsuario(modeloEmpleado(usuarioIntent));
                    Intent intentListUs = new Intent(NuevoEmpleado.this, ListadoUsuarios.class);
                    intentListUs.putExtra("usuario", usuarioIntent);
                    startActivity(intentListUs);
                }
                else{
                    Toast.makeText(this, "Debes rellenar los espacios", Toast.LENGTH_SHORT).show();
                }
        });
        bindingNuevEmp.btnVolverNuevEmp.setOnClickListener(v -> { //Botón volver, volvemos al activity ListadoUsuarios, acción al hacer click
            Intent intentVolver = new Intent(NuevoEmpleado.this, ListadoUsuarios.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });

    }

    private boolean emptyEdits() { //método que revisa si los editText estan vacíos
        if(bindingNuevEmp.editNombEmpleadoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editApellidosNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editTelefonoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editDireccionNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editCentroDeTrabajoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editFechaNacimientoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editCorreoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editContrasenaNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editRepetirContraNuevEmp.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    private Usuario modeloEmpleado(Usuario usuarioAdmin){ //método que devuelve un objeto Usuario con los datos que contienen los EditText
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(String.valueOf(bindingNuevEmp.editNombEmpleadoNuevEmp.getText()));
        usuario.setApellidosUsuario(String.valueOf(bindingNuevEmp.editApellidosNuevEmp.getText()));
        usuario.setTelefono(Integer.parseInt(String.valueOf(bindingNuevEmp.editTelefonoNuevEmp.getText())));
        usuario.setDireccion(String.valueOf(bindingNuevEmp.editDireccionNuevEmp.getText()));
        usuario.setEmpresaUsuario(String.valueOf(usuarioAdmin.getEmpresaUsuario())); //la empresa será la misma del Admin
        usuario.setLugarTrabajo(String.valueOf(bindingNuevEmp.editCentroDeTrabajoNuevEmp.getText()));
        usuario.setFechaNacimiento(String.valueOf(bindingNuevEmp.editFechaNacimientoNuevEmp.getText()));
        usuario.setCorreoUsuario(String.valueOf(bindingNuevEmp.editCorreoNuevEmp.getText()));
        usuario.setContrasena(String.valueOf(bindingNuevEmp.editContrasenaNuevEmp.getText()));
        usuario.setEsAdmin(false);
        return usuario;
    }
    private void crearCuentaUsuario(Usuario usuario) { //creamos un Usuario en la base de datos, pasandole por parámetro un objeto Usuario
        UsuarioService usuarioService = Apis.getUsuarioService();
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