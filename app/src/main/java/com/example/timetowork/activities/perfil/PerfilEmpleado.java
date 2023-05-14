package com.example.timetowork.activities.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.activities.UsuarioSesion;
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

        bindingPerfilEmpleado = ActivityPerfilEmpleadoBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View view = bindingPerfilEmpleado.getRoot(); //referencia a la vista raíz
        setContentView(view); // para que sea la vista activa en la pantalla

        Bundle bundlePerEmpin = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        usuarioIntent = (Usuario) bundlePerEmpin.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent

        Obtenerhorarios(usuarioIntent);

        fijarEditTexts(); //rellenamos la información en los EditText con los datos del Usuario de la sesión

        bindingPerfilEmpleado.btnEstabDatosPerEmp.setOnClickListener(v -> { //Botón establecer datos, llamamos al método modeloUsuario y al método actualizarUsuario, acción al hacer clic
            modeloUsuario();
            actualizarUsuario(usuarioIntent);
        });

        bindingPerfilEmpleado.btnInformeHorasPerEmp.setOnClickListener(v -> { //Botón Informe Empleado, intent hacia el activity InformeEmpleado, acción al hacer clic
            Intent intentInforme = new Intent(PerfilEmpleado.this, InformeEmpleado.class);
            intentInforme.putExtra("usuario", usuarioIntent);
            intentInforme.putExtra("usuarioGestionado", usuarioIntent);
            intentInforme.putExtra("horarios", horarios);
            intentInforme.putExtra("anios", anios);
            startActivity(intentInforme);
        });

        bindingPerfilEmpleado.btnVolverPerEmp.setOnClickListener(v -> { //Botón volver, volvemos al activity UsuarioSesion, acción al hacer clic
            Intent intentVolver = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intentVolver = new Intent(PerfilEmpleado.this, UsuarioSesion.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    private void modeloUsuario() { //cambiamos los datos del usuario con la información de los EditText
        usuarioIntent.setNombreUsuario(String.valueOf(bindingPerfilEmpleado.editNombAdminPerEmp.getText()));
        usuarioIntent.setCorreoUsuario(String.valueOf(bindingPerfilEmpleado.editCorreoPerEmp.getText()));
        usuarioIntent.setTelefono(Integer.valueOf(String.valueOf(bindingPerfilEmpleado.editTelefonoPerEmp.getText())));
        usuarioIntent.setLugarTrabajo(String.valueOf(bindingPerfilEmpleado.editCentroTrabajoPerEmp.getText()));
    }

    private void fijarEditTexts() { //fijamos la información del usuario en los EditText
        bindingPerfilEmpleado.editNombAdminPerEmp.setText(usuarioIntent.getNombreUsuario());
        bindingPerfilEmpleado.editCorreoPerEmp.setText(usuarioIntent.getCorreoUsuario());
        bindingPerfilEmpleado.editCentroTrabajoPerEmp.setText(usuarioIntent.getLugarTrabajo());
        bindingPerfilEmpleado.editTelefonoPerEmp.setText(String.valueOf(usuarioIntent.getTelefono()));
    }

    private void actualizarUsuario(Usuario usuario) { //actualizamos el usuario en la base de datos y actualizamos el activity haciendo un intent
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

    private void Obtenerhorarios(Usuario usuario) { //obtenemos el listado de horarios para el usuario que le pasemos y rellenamos el array anios
        HorarioService horarioService = Apis.getHorarioService();
        Call<ArrayList<Horario>> call = horarioService.getHorarios(usuario);
        call.enqueue(new Callback<ArrayList<Horario>>() {
            @Override
            public void onResponse(Call<ArrayList<Horario>> call, Response<ArrayList<Horario>> response) {
                horarios.addAll(response.body()); //añade todos los objetos horarios de la respuesta del array de la llamada a la Api
                listarAnios(horarios); //rellenamos el array anios
            }

            @Override
            public void onFailure(Call<ArrayList<Horario>> call, Throwable t) {

            }
        });
    }

    private void listarAnios(ArrayList<Horario> horarios) { //Listando los años de los que dispone historial de horarios el trabajador para poder rellenarlos en el spinner del activity de informe
        ArrayList<Integer> arrayAuxAnios = new ArrayList<Integer>();
        for (int i = 0; i < horarios.size(); i++) { //recorremos la lista de horarios del usuario gestionado y recogemos del campo fecha el año y lo añadimos en el array auxiliar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                arrayAuxAnios.add(LocalDate.parse(horarios.get(i).getFecha()).getYear());
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            anios = (ArrayList<Integer>) arrayAuxAnios.stream().distinct().collect(Collectors.toList()); //gracias al metodo stream que permite trabajar con el modelo de datos de una colección, conseguimos los años que sean distintos del array
            if(anios.isEmpty()){ //si el array está vacío devolvemos al menos el año actual
                anios.add(LocalDate.now().getYear());
            }
        }
    }
}