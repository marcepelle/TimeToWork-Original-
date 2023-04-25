package com.example.timetowork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import com.example.timetowork.databinding.ActivityInformeEmpleadoBinding;
import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class InformeEmpleado extends AppCompatActivity {
    ActivityInformeEmpleadoBinding bindingInforme;
    Usuario usuarioIntent;
    Usuario usuarioGestionado;
    ArrayList<Integer> anios;
    ArrayList<Horario> horarios = new ArrayList<Horario>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingInforme = ActivityInformeEmpleadoBinding.inflate(getLayoutInflater());
        View viewInforme = bindingInforme.getRoot();
        setContentView(viewInforme);
        Bundle bundleInforme = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundleInforme.getSerializable("usuario", Usuario.class);
            usuarioGestionado = bundleInforme.getSerializable("usuarioGestionado", Usuario.class);
            horarios = (ArrayList<Horario>) bundleInforme.getSerializable("horarios", ArrayList.class);
            anios = (ArrayList<Integer>) bundleInforme.getSerializable("anios", ArrayList.class);
        }
        bindingInforme.txtnomEmpleadoInfEmp.setText(usuarioGestionado.getNombreUsuario() + " " + usuarioGestionado.getApellidosUsuario());
        int minAnio = anios.size() - 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime date = LocalDateTime.of((LocalDate.of(anios.get(minAnio), 1, 1)), LocalTime.now());
            Instant instant = date.atZone(ZoneId.systemDefault()).toInstant();
            bindingInforme.calendarView.setMinDate(instant.toEpochMilli()); //fijo el minimo año que tendra el calendario segun si existen horarios para el empleado en ese año
            Log.d("Mindate1", "Mindate: " + bindingInforme.calendarView.getMinDate());
        }
        bindingInforme.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    fijartInfoTextview(view, year, month, dayOfMonth);
            }
        });
        bindingInforme.btnVolverInfEmp.setOnClickListener(v -> {
            Intent intentVolver;
            if(usuarioIntent.isEsAdmin()){
                intentVolver = new Intent(InformeEmpleado.this, GestionUsuario.class);
            }else{
                intentVolver = new Intent(InformeEmpleado.this, PerfilEmpleado.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            intentVolver.putExtra("usuarioGestionado", usuarioGestionado);
            startActivity(intentVolver);
        });
    }
    private int diferenciaHoras(String desde, String hasta) {
        int difHoras = 0;
        long resultado = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("Horadesde", desde);
            Log.d("HoraHasta", hasta);
            LocalTime desdeTime =  LocalTime.parse(desde);
            LocalTime hastaTime = LocalTime.parse(hasta);
            resultado = desdeTime.until(hastaTime, ChronoUnit.HOURS);
        }
        difHoras = (int) resultado;
        return difHoras;
    }

    private void fijartInfoTextview(CalendarView view, int year, int month, int dayOfMonth){
        int HorasMes = 0;
        int HorasDia = 0;
        int HorasSemana = 0;
        bindingInforme.txtentradaInfEmp.setText("Entrada: ");
        bindingInforme.txtSalidaInfEmp.setText("Salida: ");
        LocalDate inicioSemana = null;
        LocalDate finSemana = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bindingInforme.txtFechaInfEmp.setText("Fecha: " + LocalDate.of(year, month+1, dayOfMonth));
            for(int i=0; i<horarios.size(); i++){
                if(LocalDate.parse(horarios.get(i).getFecha()).getMonthValue()==(month + 1) && LocalDate.parse(horarios.get(i).getFecha()).getYear()==year){ //si el año y mes del array es la misma que la fecha seleccionada sumo las horas del mes, le sumo 1 porque en el calendarview empieza en 0
                    if(horarios.get(i).getFichaEntrada()!=null && horarios.get(i).getFichaSalida()!=null) { //si no se ha fichado no entra
                        HorasMes += diferenciaHoras(horarios.get(i).getFichaEntrada(), horarios.get(i).getFichaSalida());
                        Log.d("HoraMes", String.valueOf(HorasMes));
                        if (LocalDate.parse(horarios.get(i).getFecha()).getDayOfMonth() == (dayOfMonth)) { //si coincide el dia del mes del horario del array con el dia seleccionado se suma las horas del día
                            HorasDia += diferenciaHoras(horarios.get(i).getFichaEntrada(), horarios.get(i).getFichaSalida());
                            bindingInforme.txtentradaInfEmp.setText("Entrada: " + horarios.get(i).getFichaEntrada());
                            bindingInforme.txtSalidaInfEmp.setText("Salida: " + horarios.get(i).getFichaSalida());
                            if(!(LocalDate.parse(horarios.get(i).getFecha()).getDayOfWeek() ==DayOfWeek.MONDAY)) {
                                inicioSemana = LocalDate.parse(horarios.get(i).getFecha()).with(TemporalAdjusters.previous(DayOfWeek.MONDAY)); //me indica la fecha del anterior lunes antes de esta fecha
                            }else{
                                inicioSemana = LocalDate.parse(horarios.get(i).getFecha()); // si es lunes entonces sera esta misma fecha
                            }
                            if (!(LocalDate.parse(horarios.get(i).getFecha()).getDayOfWeek() ==DayOfWeek.SUNDAY)) {
                                finSemana = LocalDate.parse(horarios.get(i).getFecha()).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)); //me indica la fecha del proximo domingo despues de esta fecha
                            }else{
                                finSemana = LocalDate.parse(horarios.get(i).getFecha()); //si es domingo entonces sera esta misma fecha
                            }
                            Log.d("Semana", "Inicio " + inicioSemana + " Fin " + finSemana);
                        }else{ //si la fecha seleccionada no esta en las fechas de la array de los horarios
                            if(!(LocalDate.of(year, month+1, dayOfMonth).getDayOfWeek() ==DayOfWeek.MONDAY)) {
                                inicioSemana = LocalDate.of(year, month+1, dayOfMonth).with(TemporalAdjusters.previous(DayOfWeek.MONDAY)); //me indica la fecha del anterior lunes antes de esta fecha
                            }else{
                                inicioSemana = LocalDate.of(year, month+1, dayOfMonth); // si es lunes entonces sera esta misma fecha
                            }
                            if (!(LocalDate.of(year, month+1, dayOfMonth).getDayOfWeek() ==DayOfWeek.SUNDAY)) {
                                finSemana = LocalDate.of(year, month+1, dayOfMonth).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)); //me indica la fecha del proximo domingo despues de esta fecha
                            }else{
                                finSemana = LocalDate.of(year, month+1, dayOfMonth); //si es domingo entonces sera esta misma fecha
                            }
                            Log.d("Semana", "Inicio " + inicioSemana + " Fin " + finSemana);
                        }
                    }
                }
            }
            for(int i=0; i<horarios.size(); i++){
                if((inicioSemana!=null)&&(finSemana!=null)){
                    if ((LocalDate.parse(horarios.get(i).getFecha()).isAfter(inicioSemana) || LocalDate.parse(horarios.get(i).getFecha()).isEqual(inicioSemana)) && (LocalDate.parse(horarios.get(i).getFecha()).isBefore(finSemana) || LocalDate.parse(horarios.get(i).getFecha()).isEqual(inicioSemana))) {
                        if (horarios.get(i).getFichaEntrada() != null && horarios.get(i).getFichaSalida() != null) {
                            HorasSemana += diferenciaHoras(horarios.get(i).getFichaEntrada(), horarios.get(i).getFichaSalida());
                        }
                    }
                }
            }
        }
        bindingInforme.txtHorasDiasTrabajadosInfEmp.setText("Horas trabajadas en el día: " + HorasDia);
        bindingInforme.txtHorasMesTrabajadosInfEmp.setText("Horas trabajadas en el mes: " + HorasMes);
        bindingInforme.txtHorasSemanaTrabajadosInfEmp.setText("Horas trabajadas en la semana: " + HorasSemana);
    }

}