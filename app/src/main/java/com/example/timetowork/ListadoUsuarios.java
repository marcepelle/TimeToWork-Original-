package com.example.timetowork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
        bindingGestionUser.listaUsuarios.setLayoutManager(new LinearLayoutManager(this));
        Bundle bundlePerAdmin = getIntent().getExtras();
        Usuario usuarioIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            usuarioIntent = bundlePerAdmin.getSerializable("usuario", Usuario.class);
        }
        else{
            usuarioIntent = new Usuario();
        }
        obtenerUsuarios(usuarioIntent);
        bindingGestionUser.btnNuevEmpLisUs.setOnClickListener(v -> {
            Intent intentNuevEmp = new Intent(ListadoUsuarios.this, NuevoEmpleado.class);
            intentNuevEmp.putExtra("usuario", usuarioIntent);
            startActivity(intentNuevEmp);
        });
        bindingGestionUser.btnVolverLisEmp.setOnClickListener(v -> {
            Intent intentVolver = new Intent(ListadoUsuarios.this, PerfilAdmin.class);
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }
    private void obtenerUsuarios(Usuario usuario) {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<ArrayList<Usuario>> call = usuarioService.listarUsuarios(usuario);
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                usuarios.addAll(response.body());
                Log.d("ResUsuario", response.body().toString());
                Log.d("ResUsuario2", usuarios.toString());
                Toast.makeText(ListadoUsuarios.this, "Lista Obtenida", Toast.LENGTH_SHORT).show();
                UsuarioAdapter usuarioAdapter = new UsuarioAdapter(ListadoUsuarios.this, usuarios, usuario);
                bindingGestionUser.listaUsuarios.setAdapter(usuarioAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Toast.makeText(ListadoUsuarios.this, "Lista no Obtenida", Toast.LENGTH_SHORT).show();
            }
        });

    }
}