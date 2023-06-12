package com.example.timetowork.activities.perfil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.adapters.UsuarioAdapter;
import com.example.timetowork.databinding.ActivityListadoUsuariosBinding;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListadoUsuarios extends AppCompatActivity {
    ActivityListadoUsuariosBinding bindingGestionUser;
    ArrayList<Usuario> usuarios = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingGestionUser = ActivityListadoUsuariosBinding.inflate(getLayoutInflater());
        View viewLayout = bindingGestionUser.getRoot();
        setContentView(viewLayout);

        Bundle bundlePerAdmin = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        Usuario usuarioIntent = (Usuario) bundlePerAdmin.getSerializable("usuario"); //usuario de la sesión pasado a través del intent
        usuarios = (ArrayList<Usuario>) bundlePerAdmin.getSerializable("ListadoUsuarios");

        bindingGestionUser.listaUsuarios.setLayoutManager(new LinearLayoutManager(this)); //fijamos el layout que organizará las vistas para el RecyclerView listaUsuarios
        UsuarioAdapter usuarioAdapter = new UsuarioAdapter(ListadoUsuarios.this, usuarios, usuarioIntent); //creamos el adaptador para el recyclerView pasandole por parámetro el Layout del activity, el listado de usuarios y el usuario de la sesión
        bindingGestionUser.listaUsuarios.setAdapter(usuarioAdapter); //fijamos el adaptador del recyclerview

        bindingGestionUser.btnNuevEmpLisUs.setOnClickListener(v -> { //Botón nuevo empleado, pasamos al activity NuevoEmpleado, acción al hacer click
            Intent intentNuevEmp = new Intent(ListadoUsuarios.this, NuevoEmpleado.class);
            intentNuevEmp.putExtra("usuario", usuarioIntent);
            startActivity(intentNuevEmp);
        });
        bindingGestionUser.btnVolverLisEmp.setOnClickListener(v -> { //Botón volver, volvemos al activity PerfilAdmin, acción al hacer click
            Intent intentVolver = new Intent(ListadoUsuarios.this, PerfilAdmin.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }
}