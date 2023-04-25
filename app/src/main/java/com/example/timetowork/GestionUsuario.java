package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.databinding.ActivityGestionUsuarioBinding;
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

public class GestionUsuario extends AppCompatActivity {
    ActivityGestionUsuarioBinding bindinGesUs;
    Usuario usuarioIntent;
    Usuario usuarioGestionado;
    Boolean usuarioConseguido;

    ArrayList<Integer> anios;
    ArrayList<Horario> horarios = new ArrayList<Horario>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindinGesUs = ActivityGestionUsuarioBinding.inflate(getLayoutInflater());
        View viewGesUs = bindinGesUs.getRoot();
        setContentView(viewGesUs);
        Bundle bundleLisUs = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundleLisUs.getSerializable("usuario", Usuario.class);
            usuarioGestionado = bundleLisUs.getSerializable("usuarioGestionado", Usuario.class);
            usuarioConseguido=true;
        }
        else{
            usuarioIntent = new Usuario();
            usuarioGestionado = new Usuario();
            usuarioConseguido=false;
        }
        fijarEdits(bindinGesUs, usuarioGestionado);
        Obtenerhorarios(usuarioGestionado);
        bindinGesUs.btnEstablecerDatosGesUs.setOnClickListener(v -> {
            if(!emptyEdits(bindinGesUs)) {
                usuarioGestionado.setNombreUsuario(String.valueOf(bindinGesUs.editNombEmpleadoGesUs.getText()));
                usuarioGestionado.setApellidosUsuario(String.valueOf(bindinGesUs.editApellidosGesUs.getText()));
                usuarioGestionado.setTelefono(Integer.valueOf(String.valueOf(bindinGesUs.editTelefonoGesUs.getText())));
                usuarioGestionado.setDireccion(String.valueOf(bindinGesUs.editDireccionGesUs.getText()));
                usuarioGestionado.setLugarTrabajo(String.valueOf(bindinGesUs.editCentroDeTrabajoGesUs.getText()));
                usuarioGestionado.setFechaNacimiento(String.valueOf(bindinGesUs.editFechaNacimientoGesUs.getText()));
                usuarioGestionado.setCorreoUsuario(String.valueOf(bindinGesUs.editCorreoGesUs.getText()));
                usuarioGestionado.setContrasena(String.valueOf(bindinGesUs.editContrasenaGesUs.getText()));
                actualizarUsuario(usuarioGestionado, usuarioIntent);
            }
            else{
                Toast.makeText(this, "Debes rellenar los espacios", Toast.LENGTH_SHORT).show();
            }
        });
        bindinGesUs.btnEliminarUsGesUs.setOnClickListener(v -> {
            Log.d("Borrandobtn", "eliminarUsuario: " + usuarioGestionado.getNombreUsuario());
            eliminarUsuario(usuarioGestionado);
            Intent borrarIntent  = new Intent(GestionUsuario.this, ListadoUsuarios.class);
            borrarIntent.putExtra("usuario", usuarioIntent);
            startActivity(borrarIntent);
        });
        bindinGesUs.btnInformeUsGesUs3.setOnClickListener(v -> {
            Intent intentInforme = new Intent(GestionUsuario.this, InformeEmpleado.class);
            intentInforme.putExtra("usuario", usuarioIntent);
            intentInforme.putExtra("usuarioGestionado", usuarioGestionado);
            intentInforme.putExtra("horarios", horarios);
            intentInforme.putExtra("anios", anios);
            startActivity(intentInforme);
        });
        bindinGesUs.btnVolverGesUs.setOnClickListener(v -> {
            Intent intentVolver = new Intent(GestionUsuario.this, ListadoUsuarios.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    private boolean emptyEdits(ActivityGestionUsuarioBinding binding) {
        if(bindinGesUs.editNombEmpleadoGesUs.getText().toString().isEmpty()||bindinGesUs.editApellidosGesUs.getText().toString().isEmpty()||bindinGesUs.editTelefonoGesUs.getText().toString().isEmpty()||bindinGesUs.editDireccionGesUs.getText().toString().isEmpty()||bindinGesUs.editCentroDeTrabajoGesUs.getText().toString().isEmpty()||bindinGesUs.editFechaNacimientoGesUs.getText().toString().isEmpty()||bindinGesUs.editCorreoGesUs.getText().toString().isEmpty()||bindinGesUs.editContrasenaGesUs.getText().toString().isEmpty()||bindinGesUs.editRepetirContraGesUs.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    private void eliminarUsuario(Usuario usuarioBorrar) {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Void> call = usuarioService.borrarUsuario(usuarioBorrar);
        Log.d("Borrando", "eliminarUsuario: " + usuarioBorrar.getNombreUsuario());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(GestionUsuario.this, "Usuario Borrado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(GestionUsuario.this, "Usuario No borrado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fijarEdits(ActivityGestionUsuarioBinding binding, Usuario usuarioGestionado) {
        bindinGesUs = binding;
        bindinGesUs.editNombEmpleadoGesUs.setText(usuarioGestionado.getNombreUsuario());
        bindinGesUs.editApellidosGesUs.setText(usuarioGestionado.getApellidosUsuario());
        bindinGesUs.editTelefonoGesUs.setText(String.valueOf(usuarioGestionado.getTelefono()));
        bindinGesUs.editDireccionGesUs.setText(usuarioGestionado.getDireccion());
        bindinGesUs.editCentroDeTrabajoGesUs.setText(usuarioGestionado.getLugarTrabajo());
        bindinGesUs.editFechaNacimientoGesUs.setText(usuarioGestionado.getFechaNacimiento());
        bindinGesUs.editCorreoGesUs.setText(usuarioGestionado.getCorreoUsuario());
        bindinGesUs.editContrasenaGesUs.setText(usuarioGestionado.getContrasena());
        bindinGesUs.editRepetirContraGesUs.setText(usuarioGestionado.getContrasena());
    }

    private void actualizarUsuario(Usuario usuarioGestionado, Usuario usuarioIntent) {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.actualizarUsuario(usuarioGestionado);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Toast.makeText(GestionUsuario.this, "Usuario Actualizado", Toast.LENGTH_SHORT).show();
                Intent actualizarIntent  = new Intent(GestionUsuario.this, GestionUsuario.class);
                actualizarIntent.putExtra("usuario", usuarioIntent);
                actualizarIntent.putExtra("usuarioGestionado", response.body()); //habiendo implementado la interfaz serializable puedo pasar un objeto a otra activity
                Log.d("ResUsuario", "Usuario id:" + response.body().getIdUsuario() + response.body().getNombreUsuario());
                startActivity(actualizarIntent);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(GestionUsuario.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
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
            Log.d("GesionUsuario", anios.toString());
            if(anios.isEmpty()){
                    anios.add(LocalDate.now().getYear());
            }
        }
    }
}