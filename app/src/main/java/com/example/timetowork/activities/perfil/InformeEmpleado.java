package com.example.timetowork.activities.perfil;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

        Bundle bundleInforme = getIntent().getExtras(); //obtenemos los datos pasado a través del intent
        usuarioIntent =(Usuario) bundleInforme.getSerializable("usuario"); //usuario de la sesión
        usuarioGestionado = (Usuario) bundleInforme.getSerializable("usuarioGestionado"); //usuario a gestionar
        horarios = (ArrayList<Horario>) bundleInforme.getSerializable("horarios"); //horarios del usuario a gestionar
        anios = (ArrayList<Integer>) bundleInforme.getSerializable("anios"); //años en los que hay horarios para el usuario a gestionar

        bindingInforme.txtnomEmpleadoInfEmp.setText(usuarioGestionado.getNombreUsuario() + " " + usuarioGestionado.getApellidosUsuario());
        int minAnio = anios.size() - 1; //al estar ordenados de mayor a menor obtenemos el menor año del array en la ultima posición
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime date = LocalDateTime.of((LocalDate.of(anios.get(minAnio), 1, 1)), LocalTime.now()); //creamos un objeto de tipo LocalDateTime para poder pasarselo a la clase Instant
            Instant instant = date.atZone(ZoneId.systemDefault()).toInstant(); //con un objeto de la clase Instant conseguimos un instante en el tiempo para poder convertirla hasta en nanosegundos
            bindingInforme.calendarView.setMinDate(instant.toEpochMilli()); //fijo el minimo año que tendra el calendarView pasando el objeto instant con los milisegundos transcurridos desde el momento EPOCH(1970-01-01T00:00:00Z)
            Log.d("Mindate1", "Mindate: " + bindingInforme.calendarView.getMinDate());
        }

        bindingInforme.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> fijartInfoTextview(view, year, month, dayOfMonth)); //acción que se ejecutará cada vez que se cambie la fecha en el calendarView

        bindingInforme.btnVolverInfEmp.setOnClickListener(v -> { //Botón volver, volverá al activity de GestionUsuario si el usuario de la sesión es Admin y si no al activity PerfilEmpleado, acción al hacer click
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
    private int diferenciaHoras(String desde, String hasta) { //método para calcular las horas transcuridas desde una hora a otra en formato LocalTime
        int difHoras = 0;
        long resultado = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("Horadesde", desde);
            Log.d("HoraHasta", hasta);
            LocalTime desdeTime =  LocalTime.parse(desde); //parseamos el String pasado a través del método para obtener un objeto LocalTime de la hora inicio
            LocalTime hastaTime = LocalTime.parse(hasta); //parseamos el String pasado a través del método para obtener un objeto LocalTime de la hora fin
            resultado = desdeTime.until(hastaTime, ChronoUnit.HOURS); //con el método until de la clase Localtime obtenemos el número de horas (tipo Long)
        }
        difHoras = (int) resultado; //parseamos el long para convertir la variable en tipo entero(integer)
        return difHoras; //devolvemos el resultado
    }

    private void fijartInfoTextview(CalendarView view, int year, int month, int dayOfMonth){ // método que fijara la información en los Textview sobre la información de horas trabajadas del usuario del informe
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
                        HorasMes += diferenciaHoras(horarios.get(i).getFichaEntrada(), horarios.get(i).getFichaSalida()); //sumamos la horas trabajadas del mes
                        Log.d("HoraMes", String.valueOf(HorasMes));
                        if (LocalDate.parse(horarios.get(i).getFecha()).getDayOfMonth() == (dayOfMonth)) { //si coincide el dia del mes del horario del array con el dia seleccionado se suma las horas del día
                            HorasDia += diferenciaHoras(horarios.get(i).getFichaEntrada(), horarios.get(i).getFichaSalida());//sumamos la horas trabajadas del día
                            //fijamos en los TextView la hora de entrada y de salida que se ha fichado
                            bindingInforme.txtentradaInfEmp.setText("Entrada: " + horarios.get(i).getFichaEntrada());
                            bindingInforme.txtSalidaInfEmp.setText("Salida: " + horarios.get(i).getFichaSalida());
                            if(!(LocalDate.parse(horarios.get(i).getFecha()).getDayOfWeek() ==DayOfWeek.MONDAY)) {//buscando el inicio de semana del día seleccionado, si no es lunes la fecha seleccionada...
                                inicioSemana = LocalDate.parse(horarios.get(i).getFecha()).with(TemporalAdjusters.previous(DayOfWeek.MONDAY)); //buscamos el valor de la fecha del lunes anterior a esta fecha gracias al método with pasandole por páremetro la clase TemporalAdjusters
                            }else{
                                inicioSemana = LocalDate.parse(horarios.get(i).getFecha()); // si es lunes entonces será esta misma fecha
                            }
                            if (!(LocalDate.parse(horarios.get(i).getFecha()).getDayOfWeek() ==DayOfWeek.SUNDAY)) {
                                finSemana = LocalDate.parse(horarios.get(i).getFecha()).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)); //me indica la fecha del próximo domingo después de esta fecha
                            }else{
                                finSemana = LocalDate.parse(horarios.get(i).getFecha()); //si es domingo entonces será esta misma fecha
                            }
                            Log.d("Semana", "Inicio " + inicioSemana + " Fin " + finSemana);
                        }else{ //si la fecha seleccionada no esta en las fechas de la array de los horarios
                            if(!(LocalDate.of(year, month+1, dayOfMonth).getDayOfWeek() ==DayOfWeek.MONDAY)) {
                                inicioSemana = LocalDate.of(year, month+1, dayOfMonth).with(TemporalAdjusters.previous(DayOfWeek.MONDAY)); //me indica la fecha del anterior lunes antes de esta fecha
                            }else{
                                inicioSemana = LocalDate.of(year, month+1, dayOfMonth); // si es lunes entonces será esta misma fecha
                            }
                            if (!(LocalDate.of(year, month+1, dayOfMonth).getDayOfWeek() ==DayOfWeek.SUNDAY)) {
                                finSemana = LocalDate.of(year, month+1, dayOfMonth).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)); //me indica la fecha del próximo domingo después de esta fecha
                            }else{
                                finSemana = LocalDate.of(year, month+1, dayOfMonth); //si es domingo entonces será esta misma fecha
                            }
                            Log.d("Semana", "Inicio " + inicioSemana + " Fin " + finSemana);
                        }
                    }
                }
            }
            for(int i=0; i<horarios.size(); i++){ //calculamos las horas de la semana trabajadas
                if((inicioSemana!=null)&&(finSemana!=null)){ //verificamos que las variables de inicio y fin de semana no sean nulos
                    if ((LocalDate.parse(horarios.get(i).getFecha()).isAfter(inicioSemana) || LocalDate.parse(horarios.get(i).getFecha()).isEqual(inicioSemana)) && (LocalDate.parse(horarios.get(i).getFecha()).isBefore(finSemana) || LocalDate.parse(horarios.get(i).getFecha()).isEqual(inicioSemana))) { //verificamos gracias a los metodos isAfter, isBefore e isEqual si la fecha iterada por el for en la array del horario esta dentro de la semana del día seleccionado
                        if (horarios.get(i).getFichaEntrada() != null && horarios.get(i).getFichaSalida() != null) { //comprobamos que se haya fichado ese día
                            HorasSemana += diferenciaHoras(horarios.get(i).getFichaEntrada(), horarios.get(i).getFichaSalida()); //si se cumplen las condiciones se suman las horas de ese día en la variable HorasSemana
                        }
                    }
                }
            }
        }
        //fijando los resultados en los TextView
        bindingInforme.txtHorasDiasTrabajadosInfEmp.setText("Horas trabajadas en el día: " + HorasDia);
        bindingInforme.txtHorasMesTrabajadosInfEmp.setText("Horas trabajadas en el mes: " + HorasMes);
        bindingInforme.txtHorasSemanaTrabajadosInfEmp.setText("Horas trabajadas en la semana: " + HorasSemana);
    }

}