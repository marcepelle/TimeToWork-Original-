package com.example.timetowork.activities.horarios;

import androidx.annotation.RequiresApi;
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

@RequiresApi(api = Build.VERSION_CODES.O)
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

        bindingHorSel = ActivityHorarioSelectBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View viewHorSel = bindingHorSel.getRoot(); //referencia a la vista raíz
        setContentView(viewHorSel); // para que sea la vista activa en la pantalla

        bindingHorSel.listaHorarios.setLayoutManager(new LinearLayoutManager(this)); //fijamos el layout que organizará las vistas para el RecyclerView listaHorarios

        Bundle bundleHorSel = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        usuarioIntent =(Usuario) bundleHorSel.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent
        usuarioSpinner =(Usuario) bundleHorSel.getSerializable("usuarioSpinner"); //obtenemos el usuario que se usará para consultar en el spinner
        correosSpinner = (ArrayList<ArrayList<String>>) bundleHorSel.getSerializable("CorreosSpinner"); //obtenemos el listado de correos por centro(ordenados del mismo modo que el array centrosSpinner)
        anios =(ArrayList<Integer>) bundleHorSel.getSerializable("anios");//obtenemos los años que existen para los horarios del usuario
        centrosSpinner = bundleHorSel.getStringArray("CentrosSpinner"); //obtenemos los centros de la empresa del usuario de la sesión
        posicionAnios = (Integer) bundleHorSel.get("posicionAnios"); //obtenemos la posición del año que se mostrará en el spinner


        bindingHorSel.spinnerAnioHorSel.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, anios));//fijamos el adaptador para mostrar la información de los años disponibles en el spinner
        bindingHorSel.spinnerAnioHorSel.setSelection(posicionAnios); //fijamos el item seleccionado del spinner del año pasandole la posición

        Obtenerhorarios(usuarioSpinner); //obtenemos los horarios para el usuario seleccionado en el spinner

        bindingHorSel.spinnerCentroTrabajoHorSel.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, centrosSpinner)); //fijamos el adaptador para mostrar la información de los centros disponibles en el spinner
        bindingHorSel.spinnerCentroTrabajoHorSel.setSelection((Integer) bundleHorSel.get("posicionCentro")); //fijamos el item seleccionado del spinner del centro pasandole la posición

        bindingHorSel.spinnerCorreosHorSel.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get((Integer) bundleHorSel.get("posicionCentro")))); //fijamos el adaptador para mostrar la información de los correos disponibles en el spinner
        bindingHorSel.spinnerCorreosHorSel.setSelection((Integer) bundleHorSel.get("posicionEmpleado")); //fijamos el item seleccionado del spinner de los correos pasandole la posición

        if(!usuarioIntent.isEsAdmin()){  //si el usuario de la sesión no es administrador...
            //impedimos que pueda consultar otros empleados inhabilitando los spinners
            bindingHorSel.spinnerCorreosHorSel.setEnabled(false);
            bindingHorSel.spinnerCentroTrabajoHorSel.setEnabled(false);
            //impedimos también poder fijar horarios inhabilitando el botón Fijar Jornada y lo ponemos como no visible
            bindingHorSel.btnFijarJornadaHorSel.setEnabled(false);
            bindingHorSel.btnFijarJornadaHorSel.setVisibility(View.INVISIBLE);
        }

        bindingHorSel.spinnerCentroTrabajoHorSel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Spinner de los centros de trabajo, al cambiar la selección del spinner cambiaremos el adaptador del spinner de los correos para que muestre los correos del centro seleccionado
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedCentro){ //impedimos que la primera vez que se inicie el activity se ejecute, al estar ya fijado el adaptador
                    selectedCentro = true;
                    return;
                }
                bindingHorSel.spinnerCorreosHorSel.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get(position))); //la posición del ArrayList correosSpinner será la misma seleccionada en el spinner de los centros de trabajo, mostrando la lista de correos para ese centro
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bindingHorSel.spinnerCorreosHorSel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Spinner de los correos del centro seleccionado, al cambiar la selección del spinner obtendremos el usuario para la variable usuarioSpinner
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedEmpleado){ //impedimos que la primera vez que se inicie el activity se ejecute, al estar ya fijado el usuario
                    selectedEmpleado = true;
                    return;
                }
                obtenerUsuario(bindingHorSel.spinnerCorreosHorSel.getSelectedItem().toString()); //obtnemos el usuario pasandole el correo que se ha seleccionado en el spinner
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bindingHorSel.spinnerMesHorSel.setSelection(Integer.valueOf(String.valueOf(bundleHorSel.getSerializable("mes")))); //fijamos la selección del spinner del mes

        bindingHorSel.btnVerMesHorSel.setOnClickListener(v -> { //Botón ver mes, hacemos un intent para recargar el activity y ver el listado de horarios del usuario para el mes seleccionado, acción al hacer clic
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

        bindingHorSel.btnFijarJornadaHorSel.setOnClickListener(v -> { //Botón fijar jornada, hacemos un intent para ir al activity FijarJornada, acción al hacer clic
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
        bindingHorSel.btnVolverHorSel.setOnClickListener(v -> { //Botón volver, hacemos un intent al activity UsuarioSesion, acción al hacer clic
            Intent intentVolver = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intentVolver = new Intent(HorarioSelect.this, UsuarioSesion.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    private void Obtenerhorarios(Usuario usuarioIntent) { //obtenemos el listado de horarios para el usuario pasado
        HorarioService horarioService = Apis.getHorarioService();
        Call<ArrayList<Horario>> call = horarioService.getHorarios(usuarioIntent.getCorreoUsuario()); //hacemos una llamada a la Api para que nos devuelva el listado de horarios para el usuario pasado
        call.enqueue(new Callback<ArrayList<Horario>>() {
            @Override
            public void onResponse(Call<ArrayList<Horario>> call, Response<ArrayList<Horario>> response) {
                horarios.addAll(response.body()); //rellenamos la variable horarios con la respuesta de la solicitud a la Api
                listarAnios(horarios); //listamos los años que existen en los horarios obtenidos
                HorarioMesAdapter horarioMesAdapter = new HorarioMesAdapter(HorarioSelect.this, bindingHorSel.spinnerMesHorSel.getSelectedItem().toString(), bindingHorSel.spinnerMesHorSel.getSelectedItemPosition(),Integer.valueOf(bindingHorSel.spinnerAnioHorSel.getSelectedItem().toString()),horarios); //creamos el adaptador para el recyclerView pasandole por parámetro el Layout del activity, el mes seleccionado en el spinner de los meses, la posición del mes en el spinner, el  año seleccionado en el spinner de los años y el listado de horarios del usuario consultado
                bindingHorSel.listaHorarios.setAdapter(horarioMesAdapter); //establecemos el adapter para el recyclerview listaHorarios
            }
            @Override
            public void onFailure(Call<ArrayList<Horario>> call, Throwable t) {
            }
        });
    }

    private void listarAnios(ArrayList<Horario> horarios) { //creamos un array auxiliar que almacenará los años de la lista de horarios
        ArrayList<Integer> arrayAuxAnios = new ArrayList<Integer>();
        for (int i = 0; i < horarios.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                arrayAuxAnios.add(LocalDate.parse(horarios.get(i).getFecha()).getYear()); //obtenemos el año del horario iterado
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anios = (ArrayList<Integer>) arrayAuxAnios.stream().distinct().collect(Collectors.toList()); //almacenamos en anios los valores distintos que hemos obtenido en el array auxiliar gracias a stream
            Log.d("HorSel", anios.toString());
        }
        if(anios.isEmpty()){ //si anios esta vacío añadimos al menos el año actual
            anios.add(LocalDate.now().getYear());
        }
    }

    private void obtenerUsuario(String correo) { //obtenemos el usuario para la variable usuarioSpinner que corresponde con el correo seleccionado en el spinner
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.obtenerUsuario(correo); //hacemos una llamada a la Api para obtener el usuario que corresponde con el correo pasado
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                usuarioSpinner = response.body(); //la respuesta de la llamada a la Api se la asignamos a la variable usuarioSpinner
                Obtenerhorarios(usuarioSpinner); //obtenemos los horarios del usuario del spinner
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
            }
        });
    }
}