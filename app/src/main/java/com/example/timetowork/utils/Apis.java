package com.example.timetowork.utils;

//clase que se encargará de instanciar un objeto Retrofit y  definirá la ruta base(controller) de la API que queremos consultar
public class Apis {
    //controller en la ip y puerto
    public static final String URL_001="http://192.168.0.26:8080/personas/";

    public static PersonaService getPersonaService(){
        return Cliente.getCliente(URL_001).create(PersonaService.class);

    }

}
