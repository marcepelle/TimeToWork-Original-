package com.example.timetowork;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timetowork.databinding.ActivityMainBinding;

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
            //Log.d("Hello","Button clicked");
        });

        bindingMain.textForgetPsswd.setOnClickListener(v -> {
            Intent editForgotPsswdIntent  = new Intent(MainActivity.this, ReestablecerContrasena.class);
            startActivity(editForgotPsswdIntent);
            Toast.makeText(this, "Solicitar Contraseña", Toast.LENGTH_SHORT).show();
        });

        bindingMain.btnIniciarSesion.setOnClickListener(v -> {
            Intent editUsrProfileIntent  = new Intent(MainActivity.this, UsuarioPerfil.class);
            startActivity(editUsrProfileIntent);
            Toast.makeText(this, "Sesión Iniciada", Toast.LENGTH_SHORT).show();
        });
    }
}