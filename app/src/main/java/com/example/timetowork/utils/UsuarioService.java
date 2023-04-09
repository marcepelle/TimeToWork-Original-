package com.example.timetowork.utils;

import com.example.timetowork.models.CorreoContrasena;
import com.example.timetowork.models.Empresa;
import com.example.timetowork.models.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UsuarioService {
    @POST("crearUsuario")
    Call<Void> crearUsuario(@Body Usuario usuario);
    @POST("crearEmpresa")
    Call<Void> crearEmpresa(@Body Empresa empresa);
    @POST("loginUsuario")
    Call<Usuario> loginUsuario(@Body CorreoContrasena correoContrasena);
}
