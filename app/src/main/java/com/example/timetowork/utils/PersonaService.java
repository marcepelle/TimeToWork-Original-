package com.example.timetowork.utils;

import com.example.timetowork.models.Persona;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

//Cada método define una ruta, y especifica qué clase se encargará de procesar la respuesta obtenida.
public interface PersonaService {

    @GET("listar")
    Call<List<Persona>> getPersonas(); //

}
