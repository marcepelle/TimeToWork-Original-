package com.example.timetowork;

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
        bindingFijar = ActivityFijarJornadaBinding.inflate(getLayoutInflater());
        View viewFijar = bindingFijar.getRoot();
        setContentView(viewFijar);
        Bundle bundle = getIntent().getExtras();
        Usuario usuarioIntent;
        Usuario usuarioSpinner;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundle.getSerializable("usuario", Usuario.class); //usuario sesion
            usuarioSpinner = bundle.getSerializable("usuarioSpinner", Usuario.class); //usuario a tratar los datos
            correosSpinner = (ArrayList<ArrayList<String>>) bundle.getSerializable("CorreosSpinner", ArrayList.class); //recogemos lo valores del array que contiene los datos de los empleados
            anios =(ArrayList<Integer>) bundle.getSerializable("anios", ArrayList.class);
        }
        else {
            usuarioIntent = new Usuario();
            usuarioSpinner = new Usuario();
        }
        bindingFijar.txtSelecEmpleadoFijJor.setText("Empleado: " + usuarioSpinner.getNombreUsuario() + " " + usuarioSpinner.getApellidosUsuario());
        centrosSpinner = bundle.getStringArray("CentrosSpinner"); //recogemos lo valores del array que contiene los datos de los centros
        bindingFijar.spinnerCentroTrabajoFijJor.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, centrosSpinner)); //rellenamos el spinner de centros
        bindingFijar.spinnerCentroTrabajoFijJor.setSelection((Integer) bundle.get("posicionCentro")); //fijamos la posicion de centro de empresa
        bindingFijar.spinnerCorreosFijJor.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get((Integer) bundle.get("posicionCentro")))); //rellenamos el spiner de correos de empleados
        bindingFijar.spinnerCorreosFijJor.setSelection((Integer) bundle.get("posicionEmpleado"));
        bindingFijar.spinnerCentroTrabajoFijJor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //cada vez que se cambie el item seleccionado del centro cambiaran los datos del spinner de los empleados
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedCentro){
                    selectedCentro = true;
                    return;
                }
                bindingFijar.spinnerCorreosFijJor.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correosSpinner.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bindingFijar.spinnerCorreosFijJor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!selectedEmpleado){
                    selectedEmpleado = true;
                    return;
                }
                obtenerUsuario(bindingFijar.spinnerCorreosFijJor.getSelectedItem().toString(), usuarioIntent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bindingFijar.editDesdeFijJor.setOnClickListener(v -> {
            fijarFechaEnEdit(bindingFijar.editDesdeFijJor.getId());
        });
        bindingFijar.editHastaFijJor.setOnClickListener(v -> {
            fijarFechaEnEdit(bindingFijar.editHastaFijJor.getId());
        });
        bindingFijar.editHoraEntradaFijJor.setOnClickListener(v -> {
            fijarHoraEnEdit(bindingFijar.editHoraEntradaFijJor.getId());
        });
        bindingFijar.editHoraSalidaFijJor.setOnClickListener(v -> {
            fijarHoraEnEdit(bindingFijar.editHoraSalidaFijJor.getId());
        });
        bindingFijar.btnFijarJornadaFijJor.setOnClickListener(v -> {
            bindingFijar.btnFijarJornadaFijJor.setClickable(false);
            fijarJornada(usuarioSpinner, bindingFijar);
            bindingFijar.btnFijarJornadaFijJor.setClickable(true);
        });
        bindingFijar.btnEliminarJornadaFijJor.setOnClickListener(v -> {
            bindingFijar.btnFijarJornadaFijJor.setClickable(false);
            eliminarJornada(usuarioSpinner, bindingFijar);
            bindingFijar.btnFijarJornadaFijJor.setClickable(true);
        });
        bindingFijar.btnVolverFijJor.setOnClickListener(v -> {
            Intent intentVolver = new Intent(FijarJornada.this, HorarioSelect.class);
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
    private void eliminarJornada(Usuario usuarioSpinner, ActivityFijarJornadaBinding binding){
        LocalDate desde = null;
        LocalDate hasta = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            desde = LocalDate.parse(binding.editDesdeFijJor.getText());
            hasta = LocalDate.parse(binding.editHastaFijJor.getText());
        }
        for(int i=0; i<=diasEntreFechas(desde,hasta); i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                switch (desde.plusDays(i).getDayOfWeek().toString()){
                    case "MONDAY":
                        if (binding.CheckLunesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            eliminarHorario(horario);
                        }
                        break;
                    case "TUESDAY":
                        if (binding.CheckMartesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            eliminarHorario(horario);
                        }
                        break;
                    case "WEDNESDAY":
                        if (binding.CheckMiercolesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            eliminarHorario(horario);
                        }
                        break;
                    case "THURSDAY":
                        if (binding.CheckJuevesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            eliminarHorario(horario);
                        }
                        break;
                    case "FRIDAY":
                        if (binding.CheckViernesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            eliminarHorario(horario);
                        }
                        break;
                    case "SATURDAY":
                        if (binding.CheckSabadoFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            eliminarHorario(horario);
                        }
                        break;
                    case "SUNDAY":
                        if (binding.CheckDomingoFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            eliminarHorario(horario);
                        }
                }
            }
        }
        Toast.makeText(this, "Horarios Eliminados", Toast.LENGTH_SHORT).show();
    }
    private void eliminarHorario(Horario horario) {
        HorarioService horarioService = Apis.getHorarioService();
        Call<Void> call = horarioService.eliminarHorarios(horario);
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
    private void fijarJornada(Usuario usuarioSpinner, ActivityFijarJornadaBinding binding){
        LocalDate desde = null;
        LocalDate hasta = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            desde = LocalDate.parse(binding.editDesdeFijJor.getText());
            hasta = LocalDate.parse(binding.editHastaFijJor.getText());
        }
        for(int i=0; i<=diasEntreFechas(desde,hasta); i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                switch (desde.plusDays(i).getDayOfWeek().toString()){
                    case "MONDAY":
                        if (binding.CheckLunesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            crearHorario(horario);
                        }
                        break;
                    case "TUESDAY":
                        if (binding.CheckMartesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            crearHorario(horario);
                        }
                        break;
                    case "WEDNESDAY":
                        if (binding.CheckMiercolesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            crearHorario(horario);
                        }
                        break;
                    case "THURSDAY":
                        if (binding.CheckJuevesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            crearHorario(horario);
                        }
                        break;
                    case "FRIDAY":
                        if (binding.CheckViernesFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            crearHorario(horario);
                        }
                        break;
                    case "SATURDAY":
                        if (binding.CheckSabadoFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            crearHorario(horario);
                        }
                        break;
                    case "SUNDAY":
                        if (binding.CheckDomingoFijJor.isChecked()){
                            Log.d("usuario spinner", usuarioSpinner.toString());
                            Horario horario = modeloHorario(usuarioSpinner, desde.plusDays(i), binding);
                            crearHorario(horario);
                        }
                }
            }
        }
        Toast.makeText(this, "Horarios Fijados", Toast.LENGTH_SHORT).show();
    }
    private void crearHorario(Horario horario) {
        HorarioService horarioService = Apis.getHorarioService();
        Call<Void> call = horarioService.crearHorario(horario);
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

    private void obtenerUsuario(String correo, Usuario usuarioIntent) { //quitar postion
        CorreoContrasena correoContrasena = new CorreoContrasena();
        correoContrasena.setCorreo(correo);
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.obtenerUsuario(correoContrasena);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Intent intentObtenerU = new Intent(FijarJornada.this, FijarJornada.class);
                intentObtenerU.putExtra("usuario", usuarioIntent);
                intentObtenerU.putExtra("usuarioSpinner", response.body());
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

    private Horario modeloHorario(Usuario usuario, LocalDate fecha, ActivityFijarJornadaBinding binding) {
        Horario horario = new Horario();
        horario.setEmpleado(usuario.getNombreUsuario() + " " + usuario.getApellidosUsuario());
        horario.setCorreoEmpleado(usuario.getCorreoUsuario());
        horario.setCentroTrabajo(usuario.getLugarTrabajo());
        horario.setUsuario_fk(usuario);
        horario.setFecha((fecha.toString()));
        horario.setHoraEntrada((String.valueOf(binding.editHoraEntradaFijJor.getText())));
        horario.setHoraSalida((String.valueOf(binding.editHoraSalidaFijJor.getText())));
        return horario;
    }

    private int diasEntreFechas(LocalDate desde, LocalDate hasta){
        int diferenciaDias = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Period periodoEntreFechas = Period.between(desde, hasta);
            diferenciaDias = periodoEntreFechas.getDays();
        }
        Log.d("Horario", "Diferencia dias: " + diferenciaDias);
       return diferenciaDias;
    }

    private void fijarHoraEnEdit(int viewId) {
        EditText edit = (EditText) findViewById(viewId);
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        DateTimeFormatter formatoHora = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        }

        TimePickerDialog timePickerDialog = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter finalFormatoHora = formatoHora;
            timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) ->
                            edit.setText(LocalTime.of(hourOfDay,minute,00).format(finalFormatoHora)), mHour, mMinute, true);
        }
        timePickerDialog.show();
    }

    private void fijarFechaEnEdit(int viewId) {
        EditText edit = (EditText) findViewById(viewId);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> edit.setText(LocalDate.of(year,monthOfYear+1, dayOfMonth).toString()), mYear, mMonth, mDay);
        }
        datePickerDialog.show();
    }
}