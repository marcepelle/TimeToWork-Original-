package com.example.timetowork.activities.horarios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.timetowork.activities.UsuarioSesion;
import com.example.timetowork.adapters.HorarioMesAdapter;
import com.example.timetowork.databinding.ActivityHorarioSelectBinding;
import com.example.timetowork.models.CorreoContrasena;
import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.HorarioService;
import com.example.timetowork.utils.UsuarioService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    ArrayList<Integer> anios;

    int posicionAnios;
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
            correosSpinner = (ArrayList<ArrayList<String>>) bundleHorSel.getSerializable("CorreosSpinner", ArrayList.class);
            anios =(ArrayList<Integer>) bundleHorSel.getSerializable("anios", ArrayList.class);
            if(anios.isEmpty()){
                anios.add(LocalDate.now().getYear());
            }
        }
        centrosSpinner = bundleHorSel.getStringArray("CentrosSpinner");
        posicionAnios = (Integer) bundleHorSel.get("posicionAnios");
        Log.d("ControlHorSel", "Usuario spinner: " + usuarioSpinner);
        obtenerUsuario(usuarioSpinner.getCorreoUsuario());
        Log.d("ControlHorSel2", "Usuario spinner: " + usuarioSpinner);
        Log.d("ControlHorSel2", "Usuario anios " + posicionAnios + ": " + anios.toString());
        bindingHorSel.spinnerAnioHorSel.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, anios));
        bindingHorSel.spinnerAnioHorSel.setSelection(posicionAnios);
        Log.d("ControlHorSel3", "Usuario anios " + posicionAnios + ": " + bindingHorSel.spinnerAnioHorSel.getSelectedItem());
        Obtenerhoraios(usuarioSpinner);
        Log.d("ControlHorSel3", "Usuario anios " + posicionAnios + ": " + bindingHorSel.spinnerAnioHorSel.getSelectedItem());
        bindingHorSel.spinnerCentroTrabajoHorSel.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, centrosSpinner));
        Log.d("ControlHorSel3", "Usuario anios " + posicionAnios + ": " + bindingHorSel.spinnerAnioHorSel.getSelectedItem());
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
            intentSpinner.putExtra("posicionAnios", bindingHorSel.spinnerAnioHorSel.getSelectedItemPosition());
            intentSpinner.putExtra("anios", anios);
            intentSpinner.putExtra("mes", bindingHorSel.spinnerMesHorSel.getSelectedItemPosition());
            startActivity(intentSpinner);
        });
        bindingHorSel.btnFijarJornadaHorSel.setOnClickListener(v -> {
            Intent intentFijar = new Intent(HorarioSelect.this, FijarJornada.class);
            intentFijar.putExtra("usuario", usuarioIntent);
            intentFijar.putExtra("usuarioSpinner", usuarioSpinner);
            intentFijar.putExtra("CorreosSpinner", correosSpinner);
            intentFijar.putExtra("CentrosSpinner", centrosSpinner);
            intentFijar.putExtra("posicionCentro", bindingHorSel.spinnerCentroTrabajoHorSel.getSelectedItemPosition());
            intentFijar.putExtra("posicionEmpleado", bindingHorSel.spinnerCorreosHorSel.getSelectedItemPosition());
            intentFijar.putExtra("posicionAnios", bindingHorSel.spinnerAnioHorSel.getSelectedItemPosition());
            intentFijar.putExtra("anios", anios);
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
                listarAnios(horarios);
                Log.d("Horarios", "obteniendo horarios tamaño " + response.body().size());
                HorarioMesAdapter horarioMesAdapter;
                int anio = 2023;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    anio = LocalDate.now().getYear();
                }
                if(bindingHorSel.spinnerAnioHorSel.getSelectedItem()==null){ // si no existen años en el spinner le pasamos el año actual
                    Log.d("Horarios", "Año1: " + bindingHorSel.spinnerAnioHorSel.getSelectedItem());
                     horarioMesAdapter = new HorarioMesAdapter(HorarioSelect.this, bindingHorSel.spinnerMesHorSel.getSelectedItem().toString(), bindingHorSel.spinnerMesHorSel.getSelectedItemPosition(), anio,horarios);
                }else { //si existen años en el spinner le pasamos el valor del spinner
                    Log.d("Horarios", "Año2: " + bindingHorSel.spinnerAnioHorSel.getSelectedItem());
                     horarioMesAdapter = new HorarioMesAdapter(HorarioSelect.this, bindingHorSel.spinnerMesHorSel.getSelectedItem().toString(), bindingHorSel.spinnerMesHorSel.getSelectedItemPosition(),Integer.valueOf(bindingHorSel.spinnerAnioHorSel.getSelectedItem().toString()),horarios);
                }
                Log.d("BaselineSpinner", "Baseline: " + bindingHorSel.spinnerMesHorSel.getSelectedItemPosition());
                bindingHorSel.listaHorarios.setAdapter(horarioMesAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Horario>> call, Throwable t) {

            }
        });
    }

    private void listarAnios(ArrayList<Horario> horarios) {
        ArrayList<Integer> arrayAuxAnios = new ArrayList<Integer>();
        for (int i = 0; i < horarios.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                arrayAuxAnios.add(LocalDate.parse(horarios.get(i).getFecha()).getYear());
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anios = (ArrayList<Integer>) arrayAuxAnios.stream().distinct().collect(Collectors.toList());
            Log.d("HorSel", anios.toString());
        }
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
                Obtenerhoraios(usuarioSpinner);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }
}