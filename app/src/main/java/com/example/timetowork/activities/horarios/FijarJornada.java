package com.example.timetowork.activities.horarios;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;


import com.example.timetowork.databinding.ActivityFijarJornadaBinding;
import com.example.timetowork.models.CorreoContrasena;
import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.HorarioService;
import com.example.timetowork.utils.UsuarioService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FijarJornada extends AppCompatActivity {
    ActivityFijarJornadaBinding bindingFijar;
    ArrayList<Usuario> usuarios;
    ArrayList<ArrayList<String>> correosSpinner = new ArrayList<>();
    String[] centrosSpinner;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ArrayList<Integer> anios;
    Boolean selectedCentro = false;
    Boolean selectedEmpleado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindingFijar = ActivityFijarJornadaBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View viewFijar = bindingFijar.getRoot(); //referencia a la vista raíz
        setContentView(viewFijar); // para que sea la vista activa en la pantalla

        Bundle bundle = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        Usuario usuarioIntent= (Usuario) bundle.getSerializable("usuario"); //usuario sesión
        Usuario usuarioSpinner = (Usuario) bundle.getSerializable("usuarioSpinner"); //usuario a tratar los datos
        correosSpinner = (ArrayList<ArrayList<String>>) bundle.getSerializable("CorreosSpinner"); //obtenemos el listado de correos por centro(ordenados del mismo modo que el array centrosSpinner)
        anios =(ArrayList<Integer>) bundle.getSerializable("anios"); //obtenemos los años que existen para los horarios del usuario
        centrosSpinner = bundle.getStringArray("CentrosSpinner"); //recogemos lo valores del array que contiene los datos de los centros

        bindingFijar.txtSelecEmpleadoFijJor.setText("Empleado: " + usuarioSpinner.getNombreUsuario() + " " + usuarioSpinner.getApellidosUsuario()); //establecemos en el TextView el empleado al que se le va a fijar la jornada

        bindingFijar.spinnerCentroTrabajoFijJor.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, centrosSpinner)); //fijamos el adaptador para mostrar la información de los centros disponibles en el spinner
        bindingFijar.spinnerCentroTrabajoFijJor.setSelection((Integer) bundle.get("posicionCentro")); //fijamos el item seleccionado del spinner del centro pasandole la posición

        bindingFijar.spinnerCorreosFijJor.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get((Integer) bundle.get("posicionCentro")))); //fijamos el adaptador para mostrar la información de los correos disponibles en el spinner
        bindingFijar.spinnerCorreosFijJor.setSelection((Integer) bundle.get("posicionEmpleado")); //fijamos el item seleccionado del spinner de los correos pasandole la posición
        bindingFijar.spinnerCentroTrabajoFijJor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //cada vez que se cambie el item seleccionado del centro de trabajo cambiaran los datos del spinner de los correos de los empleados
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedCentro){ //impedimos que la primera vez que se inicie el activity se ejecute, al estar ya fijado el adaptador
                    selectedCentro = true;
                    return;
                }
                bindingFijar.spinnerCorreosFijJor.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get(position))); //la posición del ArrayList correosSpinner será la misma seleccionada en el spinner de los centros de trabajo, mostrando la lista de correos para ese centro
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        bindingFijar.spinnerCorreosFijJor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Spinner de los correos del centro seleccionado, al cambiar la selección del spinner obtendremos el usuario para la variable usuarioSpinner
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedEmpleado){ //impedimos que la primera vez que se inicie el activity se ejecute, al estar ya fijado el usuario
                    selectedEmpleado = true;
                    return;
                }
                obtenerUsuario(bindingFijar.spinnerCorreosFijJor.getSelectedItem().toString(), usuarioIntent); //obtnemos el usuario al que se le va a gestionar la jornada pasandole el correo que se ha seleccionado en el spinner
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        bindingFijar.editDesdeFijJor.setOnClickListener(v -> { //EditText fecha desde
            fijarFechaEnEdit(bindingFijar.editDesdeFijJor.getId()); //fijamos la fecha con DatePickerDialog en el EditText que se ha pasado por parámetro indicando su id
        });
        bindingFijar.editHastaFijJor.setOnClickListener(v -> { //EditText fecha Hasta
            fijarFechaEnEdit(bindingFijar.editHastaFijJor.getId()); //fijamos la fecha con DatePickerDialog en el EditText que se ha pasado por parámetro indicando su id
        });
        bindingFijar.editHoraEntradaFijJor.setOnClickListener(v -> { //EditText hora entrada
            fijarHoraEnEdit(bindingFijar.editHoraEntradaFijJor.getId()); //fijamos la hora con TimePickerDialog en el EditText que se ha pasado por parámetro indicando su id
        });
        bindingFijar.editHoraSalidaFijJor.setOnClickListener(v -> { //EditText hora salida
            fijarHoraEnEdit(bindingFijar.editHoraSalidaFijJor.getId()); //fijamos la hora con TimePickerDialog en el EditText que se ha pasado por parámetro indicando su id
        });
        bindingFijar.btnFijarJornadaFijJor.setOnClickListener(v -> { //Botón Fijar jornada, creamos los horarios del trabajador seleccionado en las fechas, horas y días fijadas
            bindingFijar.btnFijarJornadaFijJor.setClickable(false); //Al hacer clic bloqueamos el botón para no repetir la acción varias veces
            fijarJornada(usuarioSpinner, bindingFijar); //Fijamos la jornada del Usuario que hemos seleccionado en el spinner
            bindingFijar.btnFijarJornadaFijJor.setClickable(true); //Habilitamos el Botón fijar jornada
        });
        bindingFijar.btnEliminarJornadaFijJor.setOnClickListener(v -> { //Botón Eliminar jornada, eliminamos los horarios del trabajador seleccionado en las fechas, horas y días fijadas
            bindingFijar.btnFijarJornadaFijJor.setClickable(false); //Al hacer clic bloqueamos el botón para no repetir la acción varias veces
            eliminarJornada(usuarioSpinner, bindingFijar); //Eliminamos la jornada del Usuario que hemos seleccionado en el spinner
            bindingFijar.btnFijarJornadaFijJor.setClickable(true); //Habilitamos el Botón eliminar jornada
        });
        bindingFijar.btnVolverFijJor.setOnClickListener(v -> { //Botón volver, hacemos un intent hacia el activity HorarioSelect, acción al hacer clic
            Intent intentVolver = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intentVolver = new Intent(FijarJornada.this, HorarioSelect.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            intentVolver.putExtra("usuarioSpinner", usuarioSpinner);
            intentVolver.putExtra("CorreosSpinner", correosSpinner);
            intentVolver.putExtra("CentrosSpinner", centrosSpinner);
            intentVolver.putExtra("posicionCentro", bindingFijar.spinnerCentroTrabajoFijJor.getSelectedItemPosition());
            intentVolver.putExtra("posicionEmpleado", bindingFijar.spinnerCorreosFijJor.getSelectedItemPosition());
            intentVolver.putExtra("posicionAnios", 0);
            intentVolver.putExtra("anios", anios);
            intentVolver.putExtra("mes", 0);
            startActivity(intentVolver);
        });
    }
    private void eliminarJornada(Usuario usuarioSpinner, ActivityFijarJornadaBinding binding){ //eliminamos los horarios del trabajador seleccionado en las fechas, horas y días fijadas
        LocalDate desde = null;
        LocalDate hasta = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            desde = LocalDate.parse(binding.editDesdeFijJor.getText()); //Obtenemos en un LocalDate la fecha Desde que ha fijado el usuario de la sesión
            hasta = LocalDate.parse(binding.editHastaFijJor.getText()); //Obtenemos en un LocalDate la fecha Hasta que ha fijado el usuario de la sesión
        }
        for(int i=0; i<=diasEntreFechas(desde,hasta); i++){ //Recorreremos desde la fecha seleccionada hasta la fecha seleccionada para eliminar los horarios
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                switch (desde.plusDays(i).getDayOfWeek().toString()){ //Con el método plusDays de LocalDate le sumamos a la fecha el contador de la iteración para ir recorriendo las fechas y la condición comprobada en el switch será el nombre del día que se está iterando
                    case "MONDAY": // En caso de que el día iterado sea lunes
                        if (binding.CheckLunesFijJor.isChecked()){ //si el Checkbox de lunes se ha seleccionado se eliminará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            eliminarHorario(horario); //le pasamos al método eliminarHorario el horario a eliminar
                        }
                        break;
                    case "TUESDAY": //En caso de que el día iterado sea Martes
                        if (binding.CheckMartesFijJor.isChecked()){ //si el Checkbox de Martes se ha seleccionado se eliminará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            eliminarHorario(horario); //le pasamos al método eliminarHorario el horario a eliminar
                        }
                        break;
                    case "WEDNESDAY": //En caso de que el día iterado sea Miercoles
                        if (binding.CheckMiercolesFijJor.isChecked()){ //si el Checkbox de Miercoles se ha seleccionado se eliminará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            eliminarHorario(horario); //le pasamos al método eliminarHorario el horario a eliminar
                        }
                        break;
                    case "THURSDAY": //En caso de que el día iterado sea Jueves
                        if (binding.CheckJuevesFijJor.isChecked()){ //si el Checkbox de Jueves se ha seleccionado se eliminará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            eliminarHorario(horario); //le pasamos al método eliminarHorario el horario a eliminar
                        }
                        break;
                    case "FRIDAY": //En caso de que el día iterado sea Viernes
                        if (binding.CheckViernesFijJor.isChecked()){ //si el Checkbox de Viernes se ha seleccionado se eliminará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            eliminarHorario(horario); //le pasamos al método eliminarHorario el horario a eliminar
                        }
                        break;
                    case "SATURDAY": //En caso de que el día iterado sea Sabado
                        if (binding.CheckSabadoFijJor.isChecked()){ //si el Checkbox de Sabado se ha seleccionado se eliminará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            eliminarHorario(horario); //le pasamos al método eliminarHorario el horario a eliminar
                        }
                        break;
                    case "SUNDAY": //En caso de que el día iterado sea Domingo
                        if (binding.CheckDomingoFijJor.isChecked()){ //si el Checkbox de Domingo se ha seleccionado se eliminará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            eliminarHorario(horario); //le pasamos al método eliminarHorario el horario a eliminar
                        }
                }
            }
        }
        Toast.makeText(this, "Horarios Eliminados", Toast.LENGTH_SHORT).show();
    }
    private void eliminarHorario(Horario horario) { //Eliminar el horario pasado por parámetro en la base de datos
        HorarioService horarioService = Apis.getHorarioService();
        Call<Void> call = horarioService.eliminarHorarios(horario); //hacemos una llamada a la Api para que elimine en la base de datos el horario pasado
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("Horario", "Horario Eliminado" + horario.getFecha().toString());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FijarJornada.this, "Horario no eliminado", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fijarJornada(Usuario usuarioSpinner, ActivityFijarJornadaBinding binding){ //creamos los horarios del trabajador seleccionado en las fechas, horas y días fijadas
        LocalDate desde = null;
        LocalDate hasta = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            desde = LocalDate.parse(binding.editDesdeFijJor.getText()); //Obtenemos en un LocalDate la fecha Desde que ha fijado el usuario de la sesión
            hasta = LocalDate.parse(binding.editHastaFijJor.getText()); //Obtenemos en un LocalDate la fecha Hasta que ha fijado el usuario de la sesión
        }
        for(int i=0; i<=diasEntreFechas(desde,hasta); i++){ //Recorreremos desde la fecha seleccionada hasta la fecha seleccionada para crear los horarios, la función diasEntreFechas devuelve la candtidad de días entre la fecha desde y la fecha hasta
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                switch (desde.plusDays(i).getDayOfWeek().toString()){ //Con el método plusDays de LocalDate le sumamos a la fecha el contador de la iteración para ir recorriendo las fechas y la condición comprobada en el switch será el nombre del día que se está iterando
                    case "MONDAY": // En caso de que el día iterado sea lunes
                        if (binding.CheckLunesFijJor.isChecked()){ //si el Checkbox de lunes se ha seleccionado se creará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            crearHorario(horario); //le pasamos al método crearHorario el horario a crear
                        }
                        break;
                    case "TUESDAY": // En caso de que el día iterado sea Martes
                        if (binding.CheckMartesFijJor.isChecked()){ //si el Checkbox de Martes se ha seleccionado se creará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            crearHorario(horario); //le pasamos al método crearHorario el horario a crear
                        }
                        break;
                    case "WEDNESDAY": //En caso de que el día iterado sea Miercoles
                        if (binding.CheckMiercolesFijJor.isChecked()){  //si el Checkbox de Miercoles se ha seleccionado se creará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            crearHorario(horario); //le pasamos al método crearHorario el horario a crear
                        }
                        break;
                    case "THURSDAY": //En caso de que el día iterado sea Jueves
                        if (binding.CheckJuevesFijJor.isChecked()){ //si el Checkbox de Jueves se ha seleccionado se creará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            crearHorario(horario); //le pasamos al método crearHorario el horario a crear
                        }
                        break;
                    case "FRIDAY": //En caso de que el día iterado sea Viernes
                        if (binding.CheckViernesFijJor.isChecked()){ //si el Checkbox de Viernes se ha seleccionado se creará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            crearHorario(horario); //le pasamos al método crearHorario el horario a crear
                        }
                        break;
                    case "SATURDAY": //En caso de que el día iterado sea Sabado
                        if (binding.CheckSabadoFijJor.isChecked()){ //si el Checkbox de Sabado se ha seleccionado se creará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            crearHorario(horario); //le pasamos al método crearHorario el horario a crear
                        }
                        break;
                    case "SUNDAY": //En caso de que el día iterado sea Domingo
                        if (binding.CheckDomingoFijJor.isChecked()){ //si el Checkbox de Domingo se ha seleccionado se creará el horario de la fecha iterada
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding); //Creamos un objeto horario para la fecha iterada y el usuario seleccionado en el spinner
                            crearHorario(horario); //le pasamos al método crearHorario el horario a crear
                        }
                }
            }
        }
        Toast.makeText(this, "Horarios Fijados", Toast.LENGTH_SHORT).show();
    }
    private void crearHorario(Horario horario) { //Crea el horario pasado por parámetro en la base de datos
        HorarioService horarioService = Apis.getHorarioService();
        Call<Void> call = horarioService.crearHorario(horario); //hacemos una llamada a la Api para que cree en la base de datos el horario pasado
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("Horario", "Horario Creado" + horario.getFecha().toString());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FijarJornada.this, "Horario no creado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerUsuario(String correo, Usuario usuarioIntent) { //Obtenemos el usuario al que se le va a gestionar la jornada
        CorreoContrasena correoContrasena = new CorreoContrasena();
        correoContrasena.setCorreo(correo); //obtenemos un objeto de correoContrasena y le establecemos el atributo correo el correo pasado por parámetro para la llamada a la Api
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.obtenerUsuario(correoContrasena); //hacemos una llamada a la Api para obtener el usuario pasandole un objeto correoContrasena que contendrá el correo que se ha seleccionado en el spinner de los correos
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) { //en la respuesta de la solicitud a la Api hacemos un intent al mismo activity para actualizarlo con el nuevo usuario al que se le va a gestionar la jornada
                Intent intentObtenerU = new Intent(FijarJornada.this, FijarJornada.class);
                intentObtenerU.putExtra("usuario", usuarioIntent);
                intentObtenerU.putExtra("usuarioSpinner", response.body()); //usuario obtenido al que se le va gestionar la jornada
                intentObtenerU.putExtra("CorreosSpinner", correosSpinner);
                intentObtenerU.putExtra("CentrosSpinner", centrosSpinner);
                intentObtenerU.putExtra("posicionCentro", bindingFijar.spinnerCentroTrabajoFijJor.getSelectedItemPosition());
                intentObtenerU.putExtra("posicionEmpleado", bindingFijar.spinnerCorreosFijJor.getSelectedItemPosition());
                intentObtenerU.putExtra("anios", anios);
                startActivity(intentObtenerU);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private Horario modeloHorario(Usuario usuario, LocalDate fecha, ActivityFijarJornadaBinding binding) { //método que devuelve un objeto horario pasandole la fecha que tendrá el horario y de que usuario va a ser
        Horario horario = new Horario();
        horario.setEmpleado(usuario.getNombreUsuario() + " " + usuario.getApellidosUsuario()); //nombre y apellidos del usuario
        horario.setCorreoEmpleado(usuario.getCorreoUsuario()); //correo del usuario
        horario.setCentroTrabajo(usuario.getLugarTrabajo()); //centro de trabajo del usuario
        horario.setUsuario_fk(usuario); //objeto usuario para la clave foranea
        horario.setFecha((fecha.toString())); //fecha del horario
        horario.setHoraEntrada((String.valueOf(binding.editHoraEntradaFijJor.getText()))); //establecemos la hora de entrada para el horario con la hora que hay en el editText
        horario.setHoraSalida((String.valueOf(binding.editHoraSalidaFijJor.getText()))); //establecemos la hora de salida para el horario con la hora que hay en el editText
        return horario; //devolvemos el horario creado
    }

    private int diasEntreFechas(LocalDate desde, LocalDate hasta){ //la función diasEntreFechas devuelve la candtidad de días entre la fecha desde y la fecha hasta
        int diferenciaDias = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Period periodoEntreFechas = Period.between(desde, hasta); //Gracias a un objeto Period podemos calcular con el método between el periodo de tiempo entre dos fechas
            diferenciaDias = periodoEntreFechas.getDays(); //obtenemos la cantidad de días sobre el periodo de tiempo obtenido
        }
        Log.d("Horario", "Diferencia dias: " + diferenciaDias);
       return diferenciaDias; //devolvemos el número de días
    }

    private void fijarHoraEnEdit(int viewId) { //fijamos la hora con TimePickerDialog en el EditText que se ha pasado por parámetro indicando su id
        EditText edit = (EditText) findViewById(viewId); //Creamos un objeto EditText y vinculamos la instancia al editText pasado por parámetro a través de su id
        final Calendar c = Calendar.getInstance(); //creamos un objeto calendar para obtener...
        mHour = c.get(Calendar.HOUR_OF_DAY); //la hora  en el momento actual
        mMinute = c.get(Calendar.MINUTE); //el minuto en el momento actual

        TimePickerDialog timePickerDialog = null; //definimos un TimePickerDialog para que el usuario pueda clicar una hora facilmente a traves de un dialogo emergente
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss"); //obtenemos un objeto DateTimeFormatter para dar formato a la hora y especificamos el patrón del formato
            timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> //creamos una nueva instancia de TimePickerDialog
                            edit.setText(LocalTime.of(hourOfDay,minute,00).format(formatoHora)), //ponemos la información en el editText al fijar una hora en el dialogo mostrado
                    mHour, mMinute, true); //establecemos la hora y minutos que se va a mostrar al abrir el dialogo, que será la actual y le indicamos con true que sea formato 24h
        }
        timePickerDialog.show(); //mostramos el dialogo
    }

    private void fijarFechaEnEdit(int viewId) { //fijamos la fecha con DatePickerDialog en el EditText que se ha pasado por parámetro indicando su id
        EditText edit = (EditText) findViewById(viewId); //Creamos un objeto EditText y vinculamos la instancia al editText pasado por parámetro a través de su i
        final Calendar c = Calendar.getInstance(); //creamos un objeto calendar para obtener...
        mYear = c.get(Calendar.YEAR); //el año en el momento actual
        mMonth = c.get(Calendar.MONTH); //el mes en el momento actual
        mDay = c.get(Calendar.DAY_OF_MONTH); //el día en el momento actual

        DatePickerDialog datePickerDialog = null; //definimos un DatePickerDialog para que el usuario pueda clicar una fecha facilmente a traves de un dialogo emergente
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> //creamos una nueva instancia de DatePickerDialog
                    edit.setText(LocalDate.of(year,monthOfYear+1, dayOfMonth).toString()), //ponemos la información en el editText al fijar una fecha en el dialogo mostrado
                    mYear, mMonth, mDay); //establecemos el año, el mes y el día que se va a mostrar al abrir el dialogo
        }
        datePickerDialog.show(); //mostramos el dialogo
    }
}