package com.example.timetowork.utils;

import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HorarioService {

    @POST("crearHorario")
    Call<Void> crearHorario(@Body Horario horario);
    @POST("getHorarios")
    Call<ArrayList<Horario>> getHorarios(@Body Usuario usuario);

    @POST("eliminarHorarios")
    Call<Void> eliminarHorarios(@Body Horario horario);

    @POST("ficharEntrada")
    Call<Void> ficharEntrada(@Body Horario horario);

    @POST("ficharSalida")
    Call<Void> ficharSalida(@Body Horario horario);

    @POST("obtenerFichar")
    Call<ArrayList<Horario>> obtenerFichar(@Body Horario horario);
}
