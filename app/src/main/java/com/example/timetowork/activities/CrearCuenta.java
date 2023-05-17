package com.example.timetowork.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityCrearCuentaBinding;
import com.example.timetowork.models.Empresa;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearCuenta extends AppCompatActivity {
    UsuarioService usuarioService;
    ActivityCrearCuentaBinding binding;
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrearCuentaBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View view = binding.getRoot(); //referencia a la vista raíz
        setContentView(view); // para que sea la vista activa en la pantalla

        String text = "Estoy de acuerdo con los términos de uso"; //Texto que le pasaremos al SpannableString
        SpannableString spannableString = new SpannableString(text); // creamos la cadena de texto que va a ser clicable gracias a la clase SpannableString

        ClickableSpan clickableSpan = new ClickableSpan() { // añadimos el metodo al clicable
            @Override
            public void onClick(@NonNull View view) { //Definimos que se hará al clicar el texto clicable
                Intent viewIntent = new Intent(Intent.ACTION_VIEW); //Al hacer clic haremos un intent hacia la vista que asignemos
                viewIntent.setData(Uri.parse("https://policies.google.com/terms?hl=es")); //la vista se dirigirá al URI definido
                startActivity(viewIntent);
            }
        };

        spannableString.setSpan(clickableSpan,25,40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE ); //indicamos que va a realizar con el clicableSpan y desde donde hasta donde va a ser clicable
        binding.checkBoxTerminos.setText(spannableString); //le añadimos el texto clicable
        binding.checkBoxTerminos.setMovementMethod(LinkMovementMethod.getInstance()); //indicamos como debe comportarse el textview en funciones de posicionamiento del cursor, desplazamiento y selección

        binding.btnCrearCuenta.setOnClickListener(v -> { //Botón crear cuenta, crearemos una cuenta para una empresa nueva y el usuario administrador para la empresa creada, acción al hacer clic
            if(binding.checkBoxTerminos.isChecked()){ //Se realizarán las acciones si se han aceptado los terminos clicando en el checkbox
                crearCuentaEmpresa(modeloEmpresa()); //crearemos una cuenta para una empresa nueva
                crearCuentaUsuario(modeloUsuario()); //crearemos un usuario administrador para la empresa creada
                Intent intentCrear = new Intent(CrearCuenta.this, MainActivity.class); //hacemos un intent hacia el activity MainActivity
                startActivity(intentCrear);
            }
        });
    }


    private Empresa modeloEmpresa(){ //Devuelve un objeto Empresa
        Empresa empresa = new Empresa(); //Creamos un objeto empresa
        empresa.setCIF(String.valueOf(binding.editCIF.getText())); //Fijamos el CIF de la empresa que se ha escrito en el EditText
        empresa.setNombreEmpresa(String.valueOf(binding.editNomEmpresa.getText())); //Fijamos el nombre de la empresa que se ha escrito en el EditText
        empresa.setTelefono(Integer.parseInt(String.valueOf(binding.editTelefonoEmp.getText()))); //Fijamos el telefono de la empresa que se ha escrito en el EditText
        empresa.setNombreAdmin(String.valueOf(binding.editNombAdmin.getText())); //Fijamos el nombre del administrador de la cuenta de la empresa que se ha escrito en el EditText
        empresa.setPais(String.valueOf(binding.editPais.getText())); //Fijamos el País de la empresa que se ha escrito en el EditText
        empresa.setProvincia(String.valueOf(binding.editProvincia.getText())); //Fijamos la provincia de la empresa que se ha escrito en el EditText
        empresa.setCiudad(String.valueOf(binding.editCiudad.getText())); //Fijamos la ciudad de la empresa que se ha escrito en el EditText
        return empresa; //Devuelve el objeto Empresa
    }
    private void crearCuentaEmpresa(Empresa empresa) { //Creamos una cuenta para la empresa pasada
        usuarioService = Apis.getUsuarioService();
        Call<Void> call = usuarioService.crearEmpresa(empresa); //hacemos una llamada a la Api para que cree una cuenta o registro de la empresa en la base de datos
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    private Usuario modeloUsuario(){ //Devuelve un objeto usuario
        Usuario usuario = new Usuario(); //Creamos un objeto usuario
        usuario.setNombreUsuario(String.valueOf(binding.editNombAdmin.getText())); //Fijamos el nombre del usuario administrador que se ha escrito en el EditText
        usuario.setApellidosUsuario(null); //Fijamos el apellido del usuario administrador que en este caso será null
        usuario.setTelefono(Integer.parseInt(String.valueOf(binding.editTelefonoEmp.getText()))); //Fijamos el telefono del usuario administrador que se ha escrito en el EditText
        usuario.setDireccion(binding.editCiudad.getText() +", "+binding.editProvincia.getText()+", "+binding.editPais.getText()+"."); //Fijamos la dirección del centro de trabajo del usuario administrador que se ha escrito en el EditText
        usuario.setEmpresaUsuario(String.valueOf(binding.editNomEmpresa.getText())); //Fijamos el nombre de la empresa del usuario administrador que se ha escrito en el EditText
        usuario.setLugarTrabajo(String.valueOf(binding.editCiudad.getText())); //Fijamos el centro de trabajo del usuario administrador que se ha escrito en el EditText
        usuario.setFechaNacimiento(dateFormat.format(new Date())); //Fijamos la fecha de creación del usuario administrador
        usuario.setCorreoUsuario(String.valueOf(binding.editCorreo.getText())); //Fijamos el correo del usuario administrador que se ha escrito en el EditText
        usuario.setContrasena(String.valueOf(binding.editContrasena.getText())); //Fijamos la contraseña del usuario administrador que se ha escrito en el EditText
        usuario.setEsAdmin(true); //Fijamos como verdadero que sea usuario administrador
        return usuario; //Devolvemos el objeto usuario creado
    }

    private void crearCuentaUsuario(Usuario usuario) { //Creamos una cuenta usuario para el objeto usuario pasado
        usuarioService = Apis.getUsuarioService();
        Call<Void> call= usuarioService.crearUsuario(usuario); //hacemos una llamada a la Api para que cree una cuenta o registro del usuario administrador en la base de datos
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(CrearCuenta.this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(CrearCuenta.this,"Cuenta no creada", Toast.LENGTH_SHORT).show();
            }
        });
    }


}