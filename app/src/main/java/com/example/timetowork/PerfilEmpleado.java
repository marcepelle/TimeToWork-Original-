package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityPerfilEmpleadoBinding;
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

public class PerfilEmpleado extends AppCompatActivity {

    ActivityPerfilEmpleadoBinding bindingPerfilEmpleado;
    Usuario usuarioIntent;
    ArrayList<Integer> anios;
    ArrayList<Horario> horarios = new ArrayList<Horario>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingPerfilEmpleado = ActivityPerfilEmpleadoBinding.inflate(getLayoutInflater());
        View view = bindingPerfilEmpleado.getRoot();
        setContentView(view);
        Bundle bundlePerEmpin = getIntent().getExtras();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundlePerEmpin.getSerializable("usuario", Usuario.class);
        }
        else{
            usuarioIntent = new Usuario();
        }
        Obtenerhorarios(usuarioIntent);
        bindingPerfilEmpleado.editNombAdminPerEmp.setText(usuarioIntent.getNombreUsuario());
        bindingPerfilEmpleado.editCorreoPerEmp.setText(usuarioIntent.getCorreoUsuario());
        bindingPerfilEmpleado.editCentroTrabajoPerEmp.setText(usuarioIntent.getLugarTrabajo());
        bindingPerfilEmpleado.editTelefonoPerEmp.setText(String.valueOf(usuarioIntent.getTelefono()));

        bindingPerfilEmpleado.btnEstabDatosPerEmp.setOnClickListener(v -> {
            usuarioIntent.setNombreUsuario(String.valueOf(bindingPerfilEmpleado.editNombAdminPerEmp.getText()));
            usuarioIntent.setCorreoUsuario(String.valueOf(bindingPerfilEmpleado.editCorreoPerEmp.getText()));
            usuarioIntent.setTelefono(Integer.valueOf(String.valueOf(bindingPerfilEmpleado.editTelefonoPerEmp.getText())));
            usuarioIntent.setLugarTrabajo(String.valueOf(bindingPerfilEmpleado.editCentroTrabajoPerEmp.getText()));
            actualizarUsuario(usuarioIntent);
        });
        bindingPerfilEmpleado.btnInformeHorasPerEmp.setOnClickListener(v -> {
            Intent intentInforme = new Intent(PerfilEmpleado.this, InformeEmpleado.class);
            intentInforme.putExtra("usuario", usuarioIntent);
            intentInforme.putExtra("usuarioGestionado", usuarioIntent);
            intentInforme.putExtra("horarios", horarios);
            intentInforme.putExtra("anios", anios);
            startActivity(intentInforme);
        });

        bindingPerfilEmpleado.btnVolverPerEmp.setOnClickListener(v -> {
            Intent intentVolver = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intentVolver = new Intent(PerfilEmpleado.this, UsuarioSesion.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    private void actualizarUsuario(Usuario usuario) {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.actualizarUsuario(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Toast.makeText(PerfilEmpleado.this, "Usuario Actualizado", Toast.LENGTH_SHORT).show();
                Intent actualizarIntent  = new Intent(PerfilEmpleado.this, PerfilEmpleado.class);
                actualizarIntent.putExtra("usuario", response.body()); //habiendo implementado la interfaz serializable puedo pasar un objeto a otra activity
                Log.d("ResUsuario", "Usuario id:" + response.body().getIdUsuario() + response.body().getNombreUsuario());
                startActivity(actualizarIntent);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilEmpleado.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Obtenerhorarios(Usuario usuario) {
        HorarioService horarioService = Apis.getHorarioService();
        Call<ArrayList<Horario>> call = horarioService.getHorarios(usuario);
        call.enqueue(new Callback<ArrayList<Horario>>() {
            @Override
            public void onResponse(Call<ArrayList<Horario>> call, Response<ArrayList<Horario>> response) {
                horarios.addAll(response.body());
                listarAnios(horarios);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            anios = (ArrayList<Integer>) arrayAuxAnios.stream().distinct().collect(Collectors.toList());
            if(anios.isEmpty()){
                anios.add(LocalDate.now().getYear());
            }
        }
    }
}