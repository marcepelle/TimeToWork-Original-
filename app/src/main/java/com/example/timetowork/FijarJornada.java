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
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingFijar = ActivityFijarJornadaBinding.inflate(getLayoutInflater());
        View viewFijar = bindingFijar.getRoot();
        setContentView(viewFijar);
        Bundle bundle = getIntent().getExtras();
        Usuario usuarioIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundle.getSerializable("usuario", Usuario.class);
        }
        else {
            usuarioIntent = new Usuario();
        }
        Usuario usuarioSpinner;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioSpinner = bundle.getSerializable("usuarioSpinner", Usuario.class);
        }else{
            usuarioSpinner = new Usuario();
        }
        obtenerUsuariosParaSpinner(usuarioIntent); //obetner correos de los usuarios para el spinner
        Log.d("Bundle", "onCreate: item" + String.valueOf(bundle.get("posicionItem")));
        bindingFijar.txtSelecEmpleadoFijJor.setText("Empleado: " + usuarioSpinner.getNombreUsuario() + " " + usuarioSpinner.getApellidosUsuario());
        bindingFijar.btnSeleccionarFijJor.setOnClickListener(v -> {
            obtenerUsuario(bindingFijar.spinner.getSelectedItem().toString(), usuarioIntent, bindingFijar.spinner.getSelectedItemPosition());
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
            intentVolver.putExtra("mes", 1);
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

    private void obtenerUsuario(String correo, Usuario usuarioIntent, int position) {
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
                intentObtenerU.putExtra("posicionItem", position);
                startActivity(intentObtenerU);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private void obtenerUsuariosParaSpinner(Usuario usuario) {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<ArrayList<Usuario>> call = usuarioService.listarUsuarios(usuario);
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
               if(response.body().size()!=0) {
                   String[] correoUsuarios = new String[response.body().size()];
                   Log.d("ResBody", response.body().toString());
                   Toast.makeText(FijarJornada.this, "Lista Obtenida", Toast.LENGTH_SHORT).show();
                   for (int i = 0; i < response.body().size(); i++) {
                       correoUsuarios[i] = response.body().get(i).getCorreoUsuario();
                   }
                   bindingFijar.spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, correoUsuarios));
               }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Toast.makeText(FijarJornada.this, "Lista no Obtenida", Toast.LENGTH_SHORT).show();
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