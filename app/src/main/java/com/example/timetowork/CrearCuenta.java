package com.example.timetowork;

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
        binding = ActivityCrearCuentaBinding.inflate(getLayoutInflater()); //obtenemos la instancia del binding para obtener acceso a cada vista del activity
        View view = binding.getRoot();
        setContentView(view);
        String text = "Estoy de acuerdo con los términos de uso";
        SpannableString spannableString = new SpannableString(text); // creamos la cadena de texto que va a ser clicable

        ClickableSpan clickableSpan = new ClickableSpan() { // añadimos el metodo al clicable
            @Override
            public void onClick(@NonNull View view) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.setData(Uri.parse("https://policies.google.com/terms?hl=es"));
                startActivity(viewIntent);
            }
        };

        spannableString.setSpan(clickableSpan,25,40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE ); //indicamos donde va a ser clicable
        binding.checkBoxTerminos.setText(spannableString); //le añadimos el texto clicable
        binding.checkBoxTerminos.setMovementMethod(LinkMovementMethod.getInstance());

        binding.btnCrearCuenta.setOnClickListener(v -> {
            if(binding.checkBoxTerminos.isChecked()){
                crearCuentaEmpresa(modeloEmpresa());
                crearCuentaUsuario(modeloUsuario());
                Intent intentCrear = new Intent(CrearCuenta.this, MainActivity.class);
                startActivity(intentCrear);
            }
        });
    }


    private Empresa modeloEmpresa(){
        Empresa empresa = new Empresa();
        empresa.setCIF(String.valueOf(binding.editCIF.getText()));
        empresa.setNombreEmpresa(String.valueOf(binding.editNomEmpresa.getText()));
        empresa.setTelefono(Integer.parseInt(String.valueOf(binding.editTelefonoEmp.getText())));
        empresa.setNombreAdmin(String.valueOf(binding.editNombAdmin.getText()));
        empresa.setPais(String.valueOf(binding.editPais.getText()));
        empresa.setProvincia(String.valueOf(binding.editProvincia.getText()));
        empresa.setCiudad(String.valueOf(binding.editCiudad.getText()));
        return empresa;
    }
    private void crearCuentaEmpresa(Empresa empresa) {
        usuarioService = Apis.getUsuarioService();
        Call<Void> call = usuarioService.crearEmpresa(empresa);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                Log.d("Exito", "Exito al crear cuenta Empresa");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Fallo", t.getMessage());
            }
        });
    }

    private Usuario modeloUsuario(){
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(String.valueOf(binding.editNombAdmin.getText()));
        usuario.setApellidosUsuario(null);
        usuario.setTelefono(Integer.parseInt(String.valueOf(binding.editTelefonoEmp.getText())));
        usuario.setDireccion(binding.editCiudad.getText() +", "+binding.editProvincia.getText()+", "+binding.editPais.getText()+".");
        usuario.setEmpresaUsuario(String.valueOf(binding.editNomEmpresa.getText()));
        usuario.setLugarTrabajo(String.valueOf(binding.editCiudad.getText()));
        usuario.setFechaNacimiento(dateFormat.format(new Date()));
        usuario.setCorreoUsuario(String.valueOf(binding.editCorreo.getText()));
        usuario.setContrasena(String.valueOf(binding.editContrasena.getText()));
        usuario.setEsAdmin(true);
        return usuario;
    }

    private void crearCuentaUsuario(Usuario usuario) {
        usuarioService = Apis.getUsuarioService();
        Call<Void> call= usuarioService.crearUsuario(usuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(CrearCuenta.this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                    Log.d("Exito", "Exito al crear cuenta Usuario");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(CrearCuenta.this,"Cuenta no creada", Toast.LENGTH_SHORT).show();
                    Log.e("Fallo", t.getMessage());
            }
        });
    }


}