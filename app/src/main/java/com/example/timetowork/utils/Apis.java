
package com.example.timetowork.utils;

import android.util.Log;

import java.net.InetAddress;

//clase que se encargará de instanciar un objeto Retrofit y definirá la ruta base(controller) de la API que queremos consultar
public class Apis {
    //ruta del controller en la ip y puerto definidos

    private static String localIp = "192.168.56.1";
    public static final String URL_01="http://" + localIp + ":8080/usuarios/";
    public static final String URL_02="http://" + localIp + ":8080/horarios/";
    public static final String URL_03="http://" + localIp + ":8080/mensajes/";

    public static UsuarioService getUsuarioService(){
        return Cliente.getCliente(URL_01).create(UsuarioService.class); //Devolvemos un objeto retrofit definiendo la ruta base de la API (URL_01) e implementamos la interfaz (UsuarioService) con sus métodos abstractos para acceder a las rutas especificas
    }

    public static HorarioService getHorarioService(){
        return Cliente.getCliente(URL_02).create(HorarioService.class); //Devolvemos un objeto retrofit definiendo la ruta base de la API (URL_02) e implementamos la interfaz (HorarioService) con sus métodos abstractos para acceder a las rutas especificas
    }

    public static MensajeService getMensajeService(){
        return Cliente.getCliente(URL_03).create(MensajeService.class); //Devolvemos un objeto retrofit definiendo la ruta base de la API (URL_03) e implementamos la interfaz (MensajeService) con sus métodos abstractos para acceder a las rutas especificas
    }

}



