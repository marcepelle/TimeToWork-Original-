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

}
