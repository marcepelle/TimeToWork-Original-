package com.example.timetowork.activities.perfil;

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
import com.google.gson.Gson;

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
    ArrayList<Integer> anios;
    ArrayList<Horario> horarios = new ArrayList<Horario>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindinGesUs = ActivityGestionUsuarioBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View viewGesUs = bindinGesUs.getRoot(); //referencia a la vista raíz
        setContentView(viewGesUs); // para que sea la vista activa en la pantalla

        Bundle bundleLisUs = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        usuarioIntent = (Usuario) bundleLisUs.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent
        usuarioGestionado = (Usuario) bundleLisUs.getSerializable("usuarioGestionado"); //obtenemos el usuario a gestionar que se ha clicado en la lista del anterior activity

        fijarEdits(usuarioGestionado); //rellenamos los editext del activity
        Obtenerhorarios(usuarioGestionado); //obtención de los horarios del usuario gestionado

        bindinGesUs.btnEstablecerDatosGesUs.setOnClickListener(v -> { //botón establecer datos, actualiza los datos del usuario gestionado, acción al hacer click
            if(!emptyEdits()) {
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
        bindinGesUs.btnEliminarUsGesUs.setOnClickListener(v -> { //botón eliminar usuario, elimina el usuario y vuelve al activty ListadoUsuarios, acción al hacer click
            Log.d("Borrandobtn", "eliminarUsuario: " + usuarioGestionado.getNombreUsuario());
            eliminarUsuario(usuarioGestionado);
            Intent borrarIntent  = new Intent(GestionUsuario.this, ListadoUsuarios.class);
            borrarIntent.putExtra("usuario", usuarioIntent);
            startActivity(borrarIntent);
        });
        bindinGesUs.btnInformeUsGesUs3.setOnClickListener(v -> { //botón informe, intent hacia el activity InformeEmpleado, acción al hacer click
            Intent intentInforme = new Intent(GestionUsuario.this, InformeEmpleado.class);
            intentInforme.putExtra("usuario", usuarioIntent);
            intentInforme.putExtra("usuarioGestionado", usuarioGestionado);
            intentInforme.putExtra("horarios", horarios);
            intentInforme.putExtra("anios", anios);
            startActivity(intentInforme);
        });
        bindinGesUs.btnVolverGesUs.setOnClickListener(v -> { //botón volver, vuelve al activity anterior, acción al hacer click
            Intent intentVolver = new Intent(GestionUsuario.this, ListadoUsuarios.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    private boolean emptyEdits() { //comprobamos que ningún editext este vacío
        if(bindinGesUs.editNombEmpleadoGesUs.getText().toString().isEmpty()||bindinGesUs.editApellidosGesUs.getText().toString().isEmpty()||bindinGesUs.editTelefonoGesUs.getText().toString().isEmpty()||bindinGesUs.editDireccionGesUs.getText().toString().isEmpty()||bindinGesUs.editCentroDeTrabajoGesUs.getText().toString().isEmpty()||bindinGesUs.editFechaNacimientoGesUs.getText().toString().isEmpty()||bindinGesUs.editCorreoGesUs.getText().toString().isEmpty()||bindinGesUs.editContrasenaGesUs.getText().toString().isEmpty()||bindinGesUs.editRepetirContraGesUs.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    private void eliminarUsuario(Usuario usuarioBorrar) { //eliminar usuario de la base de datos
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

    private void fijarEdits(Usuario usuarioGestionado) { //rellenamos los editext del activity con la información del usuario
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

    private void actualizarUsuario(Usuario usuarioGestionado, Usuario usuarioIntent) { //actualizamos el usuario en la base de datos y recargamos el activity para obtener los datos nuevos
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.actualizarUsuario(usuarioGestionado);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Toast.makeText(GestionUsuario.this, "Usuario Actualizado", Toast.LENGTH_SHORT).show();
                Intent actualizarIntent  = new Intent(GestionUsuario.this, GestionUsuario.class);
                actualizarIntent.putExtra("usuario", usuarioIntent);
                actualizarIntent.putExtra("usuarioGestionado", response.body());
                Log.d("ResUsuario", "Usuario id:" + response.body().getIdUsuario() + response.body().getNombreUsuario());
                startActivity(actualizarIntent);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(GestionUsuario.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
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
        for (int i = 0; i < horarios.size(); i++) {  //recorremos la lista de horarios del usuario gestionado y recogemos del campo fecha el año y lo añadimos en el array auxiliar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                arrayAuxAnios.add(LocalDate.parse(horarios.get(i).getFecha()).getYear());
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            anios = (ArrayList<Integer>) arrayAuxAnios.stream().distinct().collect(Collectors.toList());//gracias al metodo stream que permite trabajar con el modelo de datos de una colección, conseguimos los años que sean distintos del array
            Log.d("GesionUsuario", anios.toString());
            if(anios.isEmpty()){ //si el array está vacío devolvemos al menos el año actual
                    anios.add(LocalDate.now().getYear());
            }
        }
    }
}