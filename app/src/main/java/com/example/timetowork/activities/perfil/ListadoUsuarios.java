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

        bindingGestionUser.listaUsuarios.setLayoutManager(new LinearLayoutManager(this)); //fijamos el layout que organizará las vistas para el RecyclerView listaUsuarios

        Bundle bundlePerAdmin = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        Usuario usuarioIntent = (Usuario) bundlePerAdmin.getSerializable("usuario"); //usuario de la sesión pasado a través del intent

        obtenerUsuarios(usuarioIntent); //obtenemos el listado de usuarios según el usuario de la sesión

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
    private void obtenerUsuarios(Usuario usuario) { //obtenemos la lista de usuarios según la empresa del usuario de la sesión
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<ArrayList<Usuario>> call = usuarioService.listarUsuarios(usuario);
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                usuarios.addAll(response.body()); //añadimos en el arraylist usuarios la respuesta de la llamada a la Api
                Log.d("ResUsuario", response.body().toString());
                Log.d("ResUsuario2", usuarios.toString());
                Toast.makeText(ListadoUsuarios.this, "Lista Obtenida", Toast.LENGTH_SHORT).show();
                UsuarioAdapter usuarioAdapter = new UsuarioAdapter(ListadoUsuarios.this, usuarios, usuario); //creamos el adaptador para el recyclerView pasandole por parámetro el Layout del activity, el listado de usuarios y el usuario de la sesión
                bindingGestionUser.listaUsuarios.setAdapter(usuarioAdapter); //fijamos el adaptador del recyclerview
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Toast.makeText(ListadoUsuarios.this, "Lista no Obtenida", Toast.LENGTH_SHORT).show();
            }
        });

    }
}