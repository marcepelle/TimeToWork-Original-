package com.example.timetowork.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearCuenta extends AppCompatActivity {
    UsuarioService usuarioService;
    ActivityCrearCuentaBinding binding;

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
            if(!binding.checkBoxTerminos.isChecked()){ //Si no se han aceptado los terminos clicando en el checkbox
                Toast.makeText(this, "Debe aceptar los términos de uso", Toast.LENGTH_SHORT).show();
                return;
            }
            if(emptyEdits()){ //Si los edits están vacíos
                Toast.makeText(this, "Debe rellenar todos los espacios", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!comprobarCIF(binding.editCIF.getText().toString())){ //Si el CIF no está bien estructurado
                Toast.makeText(this, "El CIF debe contener: 1 letra y 8 dígitos (Ej.: B01234567 o B-01234567)", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!comprobarCorreo(binding.editCorreo.getText().toString())){ //Si el correo no está bien estructurado
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!(binding.editContrasena.getText().toString()==binding.editRepetirContra.getText().toString())){ //si la contraseña no se ha escrito bien las dos veces
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            crearCuentaEmpresa(modeloEmpresa()); //crearemos una cuenta para una empresa nueva
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
        Call<Integer> call = usuarioService.crearEmpresa(empresa); //hacemos una llamada a la Api para que cree una cuenta o registro de la empresa en la base de datos
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body()==1){
                    crearCuentaUsuario(modeloUsuario()); //crearemos un usuario administrador para la empresa creada
                    Intent intentCrear = new Intent(CrearCuenta.this, MainActivity.class); //hacemos un intent hacia el activity MainActivity
                    startActivity(intentCrear);
                }else{
                    Toast.makeText(CrearCuenta.this,"Cuenta no creada, Empresa o CIF ya existentes", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(CrearCuenta.this,"Cuenta no creada", Toast.LENGTH_SHORT).show();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            usuario.setFechaNacimiento(LocalDate.now().toString()); //Fijamos la fecha de creación del usuario administrador
        }
        usuario.setCorreoUsuario(String.valueOf(binding.editCorreo.getText())); //Fijamos el correo del usuario administrador que se ha escrito en el EditText
        usuario.setContrasena(String.valueOf(binding.editContrasena.getText())); //Fijamos la contraseña del usuario administrador que se ha escrito en el EditText
        usuario.setEsAdmin(true); //Fijamos como verdadero que sea usuario administrador
        return usuario; //Devolvemos el objeto usuario creado
    }

    private void crearCuentaUsuario(Usuario usuario) { //Creamos una cuenta usuario para el objeto usuario pasado
        usuarioService = Apis.getUsuarioService();
        Call<Integer> call= usuarioService.crearUsuario(usuario); //hacemos una llamada a la Api para que cree una cuenta o registro del usuario administrador en la base de datos
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body()==1){
                    Toast.makeText(CrearCuenta.this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CrearCuenta.this,"Cuenta no creada, correo existente", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                    Toast.makeText(CrearCuenta.this,"Cuenta no creada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean emptyEdits() { //comprobamos que ningún editext este vacío
        if(binding.editNomEmpresa.getText().toString().isEmpty()||binding.editCIF.getText().toString().isEmpty()||binding.editNombAdmin.getText().toString().isEmpty()||binding.editPais.getText().toString().isEmpty()||binding.editProvincia.getText().toString().isEmpty()||binding.editCiudad.getText().toString().isEmpty()||binding.editTelefonoEmp.getText().toString().isEmpty()||binding.editCorreo.getText().toString().isEmpty()||binding.editContrasena.getText().toString().isEmpty()||binding.editRepetirContra.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    private boolean comprobarCIF(String cif){ //Comprobamos mediante expresiones regulares que la cadena pasada sea una estructura valida para un CIF
        Pattern patron = Pattern.compile("([A-Z])(-)?([0-9]{8})$"); //Definimos el patron a comprobar
        Matcher coincide = patron.matcher(cif); //Le pasamos la cadena al interpretador de patrones
        if(coincide.matches()){ //si coincide devolveremos true
            return true;
        }else{
            return false;
        }
    }
    private boolean comprobarCorreo(String correo){ //Comprobamos mediante expresiones regulares que la cadena pasada sea una estructura valida para un correo
        Pattern patron = Pattern.compile("^[A-Za-z0-9-_]+(\\.[A-Za-z0-9-_]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"); //Definimos el patron a comprobar
        Matcher coincide = patron.matcher(correo); //Le pasamos la cadena al interpretador de patrones
        if(coincide.matches()){ //si coincide devolveremos true
            return true;
        }else{ // en caso contrario false
            return false;
        }
    }

}