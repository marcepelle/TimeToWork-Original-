package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.timetowork.databinding.ActivityHorarioSelectBinding;
import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.HorarioService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HorarioSelect extends AppCompatActivity {
    ArrayList<Horario> horarios = new ArrayList<Horario>();
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
        Obtenerhoraios(usuarioIntent);
        bindingHorSel.spinnerMesHorSel.setSelection(Integer.valueOf(String.valueOf(bundleHorSel.get("mes"))));
        bindingHorSel.btnVerMesHorSel.setOnClickListener(v -> {
            Intent intentSpinner = new Intent(HorarioSelect.this, HorarioSelect.class);
            intentSpinner.putExtra("usuario", usuarioIntent);
            intentSpinner.putExtra("mes", bindingHorSel.spinnerMesHorSel.getSelectedItemPosition());
            startActivity(intentSpinner);
        });
        bindingHorSel.btnFijarJornadaHorSel.setOnClickListener(v -> {
            Intent intentFijar = new Intent(HorarioSelect.this, FijarJornada.class);
            intentFijar.putExtra("usuario", usuarioIntent);
            intentFijar.putExtra("usuarioSpinner", usuarioIntent);
            intentFijar.putExtra("posicionItem", 0);
            startActivity(intentFijar);
        });
        bindingHorSel.btnVolverHorSel.setOnClickListener(v -> {
            Intent intentVolver = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intentVolver = new Intent(HorarioSelect.this, UsuarioSesion.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    private void Obtenerhoraios(Usuario usuarioIntent) {
        HorarioService horarioService = Apis.getHorarioService();
        Call<ArrayList<Horario>> call = horarioService.getHorarios(usuarioIntent);
        call.enqueue(new Callback<ArrayList<Horario>>() {
            @Override
            public void onResponse(Call<ArrayList<Horario>> call, Response<ArrayList<Horario>> response) {
                horarios.addAll(response.body());
                Log.d("Horarios", "obteniendo horarios tamaño" + response.body().size());
                HorarioMesAdapter horarioMesAdapter = new HorarioMesAdapter(HorarioSelect.this, bindingHorSel.spinnerMesHorSel.getSelectedItem().toString(), bindingHorSel.spinnerMesHorSel.getSelectedItemPosition(),2023,horarios);
                Log.d("BaselineSpinner", "Baseline: " + bindingHorSel.spinnerMesHorSel.getSelectedItemPosition());
                bindingHorSel.listaHorarios.setAdapter(horarioMesAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Horario>> call, Throwable t) {

            }
        });
    }
}