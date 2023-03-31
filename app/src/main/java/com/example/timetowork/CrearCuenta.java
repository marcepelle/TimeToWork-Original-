package com.example.timetowork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import com.example.timetowork.databinding.ActivityCrearCuentaBinding;

public class CrearCuenta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.timetowork.databinding.ActivityCrearCuentaBinding binding = ActivityCrearCuentaBinding.inflate(getLayoutInflater()); //obtenemos la instancia del binding para obtener acceso a cada vista del activity
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
    }
}