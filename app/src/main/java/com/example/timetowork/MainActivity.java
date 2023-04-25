package com.example.timetowork;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timetowork.databinding.ActivityMainBinding;
import com.example.timetowork.models.CorreoContrasena;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding bindingMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        bindingMain = ActivityMainBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View view = bindingMain.getRoot();//referencia a la vista raíz
        setContentView(view); // para que sea la vista activa en la pantalla

        bindingMain.btnNuevaEmpresa.setOnClickListener(v -> {
            Intent btnNewEmpIntent = new Intent(MainActivity.this, CrearCuenta.class); // intención que tiene de realizar un cambio de actividad
            startActivity(btnNewEmpIntent); // inicia una instancia de CrearCuenta
            Toast.makeText(this, "Crear Nueva Empresa", Toast.LENGTH_SHORT).show();
        });

        bindingMain.textForgetPsswd.setOnClickListener(v -> {
            Intent editForgotPsswdIntent  = new Intent(MainActivity.this, ReestablecerContrasena.class);
            startActivity(editForgotPsswdIntent);
            Toast.makeText(this, "Solicitar Contraseña", Toast.LENGTH_SHORT).show();
        });

        bindingMain.btnIniciarSesion.setOnClickListener(v -> {
            CorreoContrasena correoContrasena = new CorreoContrasena();
            correoContrasena.setCorreo(String.valueOf(bindingMain.editUser.getText()));
            correoContrasena.setPassword(String.valueOf(bindingMain.editPassword.getText()));
            if(Build.VERSION.SDK_INT>=33) {
                usuarioLoggueado(correoContrasena);
            }
            else{
                Toast.makeText(this, "No se puede realizar la acción", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("RestrictedApi")
    private void usuarioLoggueado(CorreoContrasena correoContrasena)  {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.loginUsuario(correoContrasena);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.body() != null) {
                    Toast.makeText(MainActivity.this, "Sesión Iniciada", Toast.LENGTH_SHORT).show();
                    Intent editUsrProfileIntent  = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        editUsrProfileIntent = new Intent(MainActivity.this, UsuarioSesion.class);
                    }
                    editUsrProfileIntent.putExtra("usuario", response.body()); //habiendo implementado la interfaz serializable puedo pasar un objeto a otra activity
                    startActivity(editUsrProfileIntent);
                    Log.d("ResUsuario", "Usuario id:" + response.body());

                } else {
                    // Si la autenticación falló, mostrar un mensaje de error
                    Log.d("ResUsuario", "Usuario id:" + response.body());
                    Toast.makeText(MainActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.d("ResUsuario", "fallo al intentar el usuario ");
                Toast.makeText(MainActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
            }
        });
    }
}