package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.timetowork.databinding.ActivityHorarioSelectBinding;
import com.example.timetowork.models.CorreoContrasena;
import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.HorarioService;
import com.example.timetowork.utils.UsuarioService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HorarioSelect extends AppCompatActivity {
    ArrayList<Horario> horarios = new ArrayList<Horario>();
    ActivityHorarioSelectBinding bindingHorSel;
    ArrayList<ArrayList<String>> correosSpinner = new ArrayList<>();
    String[] centrosSpinner;
    Usuario usuarioIntent;
    Usuario usuarioSpinner;

    Boolean selectedCentro = false;
    Boolean selectedEmpleado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingHorSel = ActivityHorarioSelectBinding.inflate(getLayoutInflater());
        View viewHorSel = bindingHorSel.getRoot();
        setContentView(viewHorSel);
        bindingHorSel.listaHorarios.setLayoutManager(new LinearLayoutManager(this));
        Bundle bundleHorSel = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundleHorSel.getSerializable("usuario", Usuario.class);
            usuarioSpinner = bundleHorSel.getSerializable("usuarioSpinner", Usuario.class);
        }
        else{
            usuarioIntent = new Usuario();
            usuarioSpinner = new Usuario();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            correosSpinner = (ArrayList<ArrayList<String>>) bundleHorSel.getSerializable("CorreosSpinner");
        }
        Obtenerhoraios(usuarioSpinner);
        centrosSpinner = bundleHorSel.getStringArray("CentrosSpinner");
        bindingHorSel.spinnerCentroTrabajoHorSel.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, centrosSpinner));
        bindingHorSel.spinnerCentroTrabajoHorSel.setSelection((Integer) bundleHorSel.get("posicionCentro"));
        bindingHorSel.spinnerCorreosHorSel.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get((Integer) bundleHorSel.get("posicionCentro"))));
        bindingHorSel.spinnerCorreosHorSel.setSelection((Integer) bundleHorSel.get("posicionEmpleado"));
        bindingHorSel.spinnerCentroTrabajoHorSel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedCentro){
                    selectedCentro = true;
                    return;
                }
                bindingHorSel.spinnerCorreosHorSel.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(!usuarioIntent.isEsAdmin()){
            bindingHorSel.spinnerCorreosHorSel.setEnabled(false);
            bindingHorSel.spinnerCentroTrabajoHorSel.setEnabled(false);
            bindingHorSel.btnFijarJornadaHorSel.setEnabled(false);
            bindingHorSel.btnFijarJornadaHorSel.setVisibility(View.INVISIBLE);
        }
        bindingHorSel.spinnerCorreosHorSel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedEmpleado){
                    selectedEmpleado = true;
                    return;
                }
                obtenerUsuario(bindingHorSel.spinnerCorreosHorSel.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bindingHorSel.spinnerMesHorSel.setSelection(Integer.valueOf(String.valueOf(bundleHorSel.getSerializable("mes"))));
        bindingHorSel.btnVerMesHorSel.setOnClickListener(v -> {
            Intent intentSpinner = new Intent(HorarioSelect.this, HorarioSelect.class);
            intentSpinner.putExtra("usuario", usuarioIntent);
            intentSpinner.putExtra("usuarioSpinner", usuarioSpinner);
            intentSpinner.putExtra("CorreosSpinner", correosSpinner);
            intentSpinner.putExtra("CentrosSpinner", centrosSpinner);
            intentSpinner.putExtra("posicionCentro", bindingHorSel.spinnerCentroTrabajoHorSel.getSelectedItemPosition());
            intentSpinner.putExtra("posicionEmpleado", bindingHorSel.spinnerCorreosHorSel.getSelectedItemPosition());
            intentSpinner.putExtra("mes", bindingHorSel.spinnerMesHorSel.getSelectedItemPosition());
            startActivity(intentSpinner);
        });
        bindingHorSel.btnFijarJornadaHorSel.setOnClickListener(v -> {
            Intent intentFijar = new Intent(HorarioSelect.this, FijarJornada.class);
            intentFijar.putExtra("usuario", usuarioIntent);
            intentFijar.putExtra("usuarioSpinner", usuarioIntent);
            intentFijar.putExtra("CorreosSpinner", correosSpinner);
            intentFijar.putExtra("CentrosSpinner", centrosSpinner);
            intentFijar.putExtra("posicionCentro", bindingHorSel.spinnerCentroTrabajoHorSel.getSelectedItemPosition());
            intentFijar.putExtra("posicionEmpleado", bindingHorSel.spinnerCorreosHorSel.getSelectedItemPosition());
            startActivity(intentFijar);
        });
        bindingHorSel.btnVolverHorSel.setOnClickListener(v -> {
            Intent intentVolver = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    private void obtenerUsuario(String correo) { //quitar postion
        CorreoContrasena correoContrasena = new CorreoContrasena();
        correoContrasena.setCorreo(correo);
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.obtenerUsuario(correoContrasena);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                usuarioSpinner = response.body();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }
}