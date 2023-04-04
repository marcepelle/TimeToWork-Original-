package com.example.timetowork.utils;

import com.example.timetowork.models.Empresa;
import com.example.timetowork.models.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {

    @POST("crearUsuario")
    Call<Usuario> crearUsuario(@Body Usuario usuario);
    @POST("crearEmpresa")
    Call<Empresa> crearEmpresa(@Body Empresa empresa);


}
