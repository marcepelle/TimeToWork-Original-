package com.example.timetowork.utils;

import com.example.timetowork.models.CorreoContrasena;
import com.example.timetowork.models.Empresa;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {
    @POST("crearUsuario")
    Call<Void> crearUsuario(@Body Usuario usuario);
    @POST("crearEmpresa")
    Call<Void> crearEmpresa(@Body Empresa empresa);
    @POST("loginUsuario")
    Call<Usuario> loginUsuario(@Body CorreoContrasena correoContrasena);
    @POST("actualizarUsuario")
    Call<Usuario> actualizarUsuario(@Body Usuario usuario);
    @POST("obtenerUsuario")
    Call<Usuario> obtenerUsuario(@Body CorreoContrasena correoContrasena);
    @POST("listarUsuarios")
    Call<ArrayList<Usuario>> listarUsuarios(@Body Usuario usuario);
    @POST("borrarUsuario")
    Call<Void> borrarUsuario(@Body Usuario usuario);
}
