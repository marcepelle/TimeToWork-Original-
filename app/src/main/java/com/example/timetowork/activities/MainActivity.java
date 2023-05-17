package com.example.timetowork.activities;

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

        bindingMain = ActivityMainBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View view = bindingMain.getRoot();//referencia a la vista raíz
        setContentView(view); // para que sea la vista activa en la pantalla

        bindingMain.btnNuevaEmpresa.setOnClickListener(v -> { //Botón Nueva Empresa, haremos un intent hacia el activity CrearCuenta donde se podrá crear una nueva cuenta para una empresa
            Intent btnNewEmpIntent = new Intent(MainActivity.this, CrearCuenta.class);
            startActivity(btnNewEmpIntent);
        });

        bindingMain.textForgetPsswd.setOnClickListener(v -> { //TextView clicable de olvido su contraseña, hacemos un intent hacia el activity ReestablecerContrasena, acción al hacer clic
            Intent editForgotPsswdIntent  = new Intent(MainActivity.this, ReestablecerContrasena.class);
            startActivity(editForgotPsswdIntent);
        });

        bindingMain.btnIniciarSesion.setOnClickListener(v -> { //Botón iniciar sesión, intentamos iniciar sesión con el correo y contraseña pasada
            CorreoContrasena correoContrasena = new CorreoContrasena(); //creamos un objeto correoContraena
            correoContrasena.setCorreo(String.valueOf(bindingMain.editUser.getText())); //fijamos el correo que se ha escrito en el EditText
            correoContrasena.setPassword(String.valueOf(bindingMain.editPassword.getText())); //fijamos la contraseña que se ha escrito en el EditText
            usuarioLoggueado(correoContrasena); //Intentamos inicar sesión pasandole el objeto correoContrasena
        });

    }

    private void usuarioLoggueado(CorreoContrasena correoContrasena)  { //Intentamos inicar sesión pasandole el objeto correoContrasena
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.loginUsuario(correoContrasena); //Hacemos una llamada a la Api para que compruebe en la base de datos si el correo y la contraseña son correctos
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.body() != null) { //Si la respuesta no devuelve null y contiene un objeto Usuario
                    Toast.makeText(MainActivity.this, "Sesión Iniciada", Toast.LENGTH_SHORT).show();
                    Intent editUsrProfileIntent  = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        editUsrProfileIntent = new Intent(MainActivity.this, UsuarioSesion.class); //hacemos un intent hacia el activity UsuarioSesión
                    }
                    editUsrProfileIntent.putExtra("usuario", response.body()); //le pasamos el usuario que nos ha devuelto la llamada a la Api
                    startActivity(editUsrProfileIntent);
                } else { // Si la autenticación falló, mostrará un mensaje de error
                    Toast.makeText(MainActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}