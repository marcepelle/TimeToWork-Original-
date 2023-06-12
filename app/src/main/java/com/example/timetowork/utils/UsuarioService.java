package com.example.timetowork.utils;

import com.example.timetowork.models.CorreoContrasena;
import com.example.timetowork.models.Empresa;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuarioService { //Interfaz que definirá los métodos abstractos que permitirán acceder a las rutas especificas de UsuarioController
    @POST("crearUsuario") //Petición a la API para crear un usuario en la base de datos
    Call<Integer> crearUsuario(@Body Usuario usuario);
    @POST("crearEmpresa") //Petición a la API para crear una empresa en la base de datos
    Call<Integer> crearEmpresa(@Body Empresa empresa);
    @GET("loginUsuario") //Petición a la API para intentar sesión en la aplicación pasandole un objeto correoContrasena, si son correctos los datos obtendremos el usuario
    Call<Usuario> loginUsuario(@Query("correo") String correo,@Query("contrasena") String contrasena);
    @PUT("actualizarUsuario") //Petición a la API para actualizar los datos del usuario pasado
    Call<Usuario> actualizarUsuario(@Body Usuario usuario);
    @PUT("actualizarContrasena") //Petición a la API para actualizar la contrasena del usuario pasado
    Call<Usuario> actualizarContrasena(@Body Usuario usuario);
    @GET("obtenerUsuario") //Petición a la API para obtener un usuario pasandole un objeto correoContrasena
    Call<Usuario> obtenerUsuario(@Query("correo") String correo);
    @GET("listarUsuarios") //Petición a la API para obtener el listado de usuarios para el usuario pasado
    Call<ArrayList<Usuario>> listarUsuarios(@Query("empresa") String empresa);
    @DELETE("borrarUsuario/{id}") //Petición a la API para borrar el usuario pasado en la base de datos
    Call<Integer> borrarUsuario(@Path("id") int id);
}
