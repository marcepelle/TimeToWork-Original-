package com.example.timetowork.utils;

import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HorarioService { //Interfaz que definirá los métodos abstractos que permitirán acceder a las rutas especificas de HorarioController

    @POST("crearHorario")  //Petición a la API para crear un horario en la base de datos
    Call<Void> crearHorario(@Body Horario horario);
    @POST("getHorarios") //Petición a la API para obtener el listado de horarios para el Usuario pasado
    Call<ArrayList<Horario>> getHorarios(@Body Usuario usuario);

    @POST("eliminarHorarios") //Petición a la API para eliminar el horario pasado
    Call<Void> eliminarHorarios(@Body Horario horario);

    @POST("ficharEntrada") //Petición a la API para fichar la hora de entrada en el horario pasado
    Call<Void> ficharEntrada(@Body Horario horario);

    @POST("ficharSalida") //Petición a la API para fichar la hora de salida en el horario pasado
    Call<Void> ficharSalida(@Body Horario horario);

    @POST("obtenerFichar") //Petición a la API para obtener un horario concreto según el objeto horario pasado para luego comprobar si se ha fichado o no
    Call<ArrayList<Horario>> obtenerFichar(@Body Horario horario);
}
