package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.timetowork.databinding.ActivityHorariosBinding;
import com.example.timetowork.models.Persona;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.PersonaService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Horarios extends AppCompatActivity {

    ActivityHorariosBinding bindingHorarios;
    PersonaService personaService;
    List<Persona> listaPersonas= new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_horarios);
        bindingHorarios = ActivityHorariosBinding.inflate(getLayoutInflater());
        View view = bindingHorarios.getRoot();
        setContentView(view);
        listView = bindingHorarios.listView;
        listarHorarios();
    }

    public void listarHorarios(){
        personaService= Apis.getPersonaService(); //obtenemos la interfaz PersonaService para poder hacer las peticiones al endpoint de la API correspondiente
        Call<List<Persona>> call=personaService.getPersonas(); //invocación de un metodo retrofit para que haga una solicitud a un servidor web
        call.enqueue(new Callback<List<Persona>>() {
            @Override
            public void onResponse(Call<List<Persona>> call, Response<List<Persona>> response) { //operación realizada con exito
                listaPersonas=response.body(); //cargamos la lista con la respuesta obtenida
                listView.setAdapter(new PersonaAdapter(Horarios.this,R.layout.content_main, listaPersonas));//cargamos en el layout la lista
            }

            @Override
            public void onFailure(Call<List<Persona>> call, Throwable t) { //fallo al intentar obtener los objetos
                Log.e("messageError:", t.getMessage());
            }
        });
    }
}