package com.example.timetowork.utils;

import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MensajeService {

    @POST("crearMensaje")
    Call<Void> crearMensaje(@Body Mensaje mensaje);

    @POST("obtenerRecibidos")
    Call<ArrayList<Mensaje>> getRecibidos(@Body Usuario usuario);

    @POST("obtenerEnviados")
    Call<ArrayList<Mensaje>> getEnviados(@Body Usuario usuario);

    @POST("mensajeVistoDe")
    Call<Void> mensajeVistoDe(@Body Mensaje mensaje);
    @POST("mensajeVistoPara")
    Call<Void> mensajeVistoPara(@Body Mensaje mensaje);
}
