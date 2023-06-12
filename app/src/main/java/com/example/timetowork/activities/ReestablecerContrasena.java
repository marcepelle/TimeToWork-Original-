package com.example.timetowork.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.R;
import com.example.timetowork.databinding.ActivityReestablecerContrasenaBinding;

public class ReestablecerContrasena extends AppCompatActivity {

    ActivityReestablecerContrasenaBinding bindingReestablecer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingReestablecer = ActivityReestablecerContrasenaBinding.inflate(getLayoutInflater());
        View viewReestablecer = bindingReestablecer.getRoot();
        setContentView(viewReestablecer);

        bindingReestablecer.btnSolicitarResPwd.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show();
            Intent intentVolver = new Intent(ReestablecerContrasena.this, MainActivity.class);
            startActivity(intentVolver);
        });
    }
}