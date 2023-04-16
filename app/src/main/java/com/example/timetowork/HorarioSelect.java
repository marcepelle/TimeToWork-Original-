package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.timetowork.databinding.ActivityHorarioSelectBinding;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

public class HorarioSelect extends AppCompatActivity {

    ActivityHorarioSelectBinding bindingHorSel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingHorSel = ActivityHorarioSelectBinding.inflate(getLayoutInflater());
        View viewHorSel = bindingHorSel.getRoot();
        setContentView(viewHorSel);
        bindingHorSel.listaHorarios.setLayoutManager(new LinearLayoutManager(this));
        Bundle bundleHorSel = getIntent().getExtras();
        Usuario usuarioIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundleHorSel.getSerializable("usuario", Usuario.class);
        }
        else{
            usuarioIntent = new Usuario();
        }
        bindingHorSel.spinnerMesHorSel.setSelection(Integer.valueOf(String.valueOf(bundleHorSel.get("mes"))));
        HorarioMesAdapter horarioMesAdapter = new HorarioMesAdapter(this, bindingHorSel.spinnerMesHorSel.getSelectedItem().toString(), bindingHorSel.spinnerMesHorSel.getSelectedItemPosition(),2023, new ArrayList<String>());
        Log.d("BaselineSpinner", "Baseline: " + bindingHorSel.spinnerMesHorSel.getSelectedItemPosition());
        bindingHorSel.listaHorarios.setAdapter(horarioMesAdapter);
        bindingHorSel.btnVerMesHorSel.setOnClickListener(v -> {
            Intent intentSpinner = new Intent(HorarioSelect.this, HorarioSelect.class);
            intentSpinner.putExtra("usuario", usuarioIntent);
            intentSpinner.putExtra("mes", bindingHorSel.spinnerMesHorSel.getSelectedItemPosition());
            startActivity(intentSpinner);
        });
        bindingHorSel.btnVolverHorSel.setOnClickListener(v -> {
            Intent intentVolver = new Intent(HorarioSelect.this, UsuarioSesion.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }
}