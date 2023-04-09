package com.example.timetowork.utils;

//clase que se encargará de instanciar un objeto Retrofit y  definirá la ruta base(controller) de la API que queremos consultar
public class Apis {
    //controller en la ip y puerto
    public static final String URL_002="http://192.168.0.26:8080/usuarios/";

    public static UsuarioService getUsuarioService(){
        return Cliente.getCliente(URL_002).create(UsuarioService.class); //crea una implementacion de la interfaz  endpoints de la API definida
    }

}
