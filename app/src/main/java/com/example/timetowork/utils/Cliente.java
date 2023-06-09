package com.example.timetowork.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cliente {
    public static Retrofit getCliente(String url){ //Obtención de un objeto retrofit pasandole por parametro la ruta del Controller de la Api, que nos permitirá hacer peticiones
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
