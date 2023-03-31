package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
            Toast.makeText(this, "Clicando", Toast.LENGTH_SHORT).show();
            Log.d("Hello","Button clicked");
        });
    }
}