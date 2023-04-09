package com.example.timetowork;

import android.content.Intent;
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

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        com.example.timetowork.databinding.ActivityMainBinding bindingMain = ActivityMainBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
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
            Usuario userLogged = null;
            try {
                userLogged = usuarioLoggueado(correoContrasena);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(userLogged!=null){
            Intent editUsrProfileIntent  = new Intent(MainActivity.this, UsuarioPerfil.class);
            editUsrProfileIntent.putExtra("usuario", userLogged); //habiendo implementado la interfaz serializable puedo pasar un objeto a otra activity
            startActivity(editUsrProfileIntent);
            Toast.makeText(this, "Sesión Iniciada", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Correo o contraseña incorrectas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Usuario usuarioLoggueado(CorreoContrasena correoContrasena) throws IOException {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.loginUsuario(correoContrasena);
        Response<Usuario> response = call.execute();
        if(response.isSuccessful()){
            return response.body();
        }
        return null;
    }

}