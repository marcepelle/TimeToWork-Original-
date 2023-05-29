package com.example.timetowork.utils;

import com.example.timetowork.models.CorreoContrasena;
import com.example.timetowork.models.Empresa;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService { //Interfaz que definirá los métodos abstractos que permitirán acceder a las rutas especificas de UsuarioController
    @POST("crearUsuario") //Petición a la API para crear un usuario en la base de datos
    Call<Void> crearUsuario(@Body Usuario usuario);
    @POST("crearEmpresa") //Petición a la API para crear una empresa en la base de datos
    Call<Void> crearEmpresa(@Body Empresa empresa);
    @POST("loginUsuario") //Petición a la API para intentar sesión en la aplicación pasandole un objeto correoContrasena, si son correctos los datos obtendremos el usuario
    Call<Usuario> loginUsuario(@Body CorreoContrasena correoContrasena);
    @POST("actualizarUsuario") //Petición a la API para actualizar los datos del usuario pasado
    Call<Usuario> actualizarUsuario(@Body Usuario usuario);
    @POST("obtenerUsuario") //Petición a la API para obtener un usuario pasandole un objeto correoContrasena
    Call<Usuario> obtenerUsuario(@Body CorreoContrasena correoContrasena);
    @POST("listarUsuarios") //Petición a la API para obtener el listado de usuarios para el usuario pasado
    Call<ArrayList<Usuario>> listarUsuarios(@Body Usuario usuario);
    @POST("borrarUsuario") //Petición a la API para borrar el usuario pasado en la base de datos
    Call<Void> borrarUsuario(@Body Usuario usuario);
}
