package com.example.timetowork.utils;

import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MensajeService { //Interfaz que definirá los métodos abstractos que permitirán acceder a las rutas especificas de MensajeController

    @POST("crearMensaje") //Petición a la API para crear un mensaje en la base de datos
    Call<Void> crearMensaje(@Body Mensaje mensaje);

    @POST("obtenerRecibidos") //Petición a la API para obtener el listado de mensajes recibidos para el usuario pasado
    Call<ArrayList<Mensaje>> getRecibidos(@Body Usuario usuario);

    @POST("obtenerEnviados") //Petición a la API para obtener el listado de mensajes envíados para el usuario pasado
    Call<ArrayList<Mensaje>> getEnviados(@Body Usuario usuario);

    @POST("mensajeVistoDe") //Petición a la API para fijar como true que el usuario que envío el mensaje lo ha visto, en el atributo vistoDe
    Call<Void> mensajeVistoDe(@Body Mensaje mensaje);
    @POST("mensajeVistoPara") //Petición a la API para fijar como true que el usuario que recibió el mensaje lo ha visto, en el atributo vistoPara
    Call<Void> mensajeVistoPara(@Body Mensaje mensaje);
}
