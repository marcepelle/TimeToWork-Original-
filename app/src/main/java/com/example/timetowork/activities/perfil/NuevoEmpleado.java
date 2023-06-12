package com.example.timetowork.activities.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timetowork.R;
import com.example.timetowork.databinding.ActivityGestionUsuarioBinding;
import com.example.timetowork.databinding.ActivityNuevoEmpleadoBinding;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevoEmpleado extends AppCompatActivity {

    ActivityNuevoEmpleadoBinding bindingNuevEmp;

    ArrayList<Usuario> usuarios = new ArrayList<>();

    Usuario usuarioIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindingNuevEmp = ActivityNuevoEmpleadoBinding.inflate(getLayoutInflater());
        View viewNuevEmp = bindingNuevEmp.getRoot();
        setContentView(viewNuevEmp);

        Bundle bundleNuevEmp = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
         usuarioIntent =(Usuario) bundleNuevEmp.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent

        bindingNuevEmp.btnCrearCuentaNuevEmp.setOnClickListener(v -> { //Botón crear cuenta nueva, si los EditText no están vacíos llamamos al metodo crearCuentaUsuario y hacemos un intent hacia el activity ListadoUsuarios, acción al ahcer click
            if(!emptyEdits()) {
                Toast.makeText(this, "Debes rellenar los espacios", Toast.LENGTH_SHORT).show();
            }
            if(!comprobarCorreo(bindingNuevEmp.editCorreoNuevEmp.getText().toString())){ //Si el correo no está bien estructurado
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!(bindingNuevEmp.editContrasenaNuevEmp.getText().toString()==bindingNuevEmp.editRepetirContraNuevEmp.getText().toString())){ //si la contraseña no se ha escrito bien las dos veces
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            crearCuentaUsuario(modeloEmpleado(usuarioIntent));
        });

        bindingNuevEmp.editFechaNacimientoNuevEmp.setFocusable(false);
        bindingNuevEmp.editFechaNacimientoNuevEmp.setOnClickListener(v -> {
            fijarFechaEnEdit(bindingNuevEmp.editFechaNacimientoNuevEmp.getId());
        });
        bindingNuevEmp.btnVolverNuevEmp.setOnClickListener(v -> { //Botón volver, volvemos al activity ListadoUsuarios, acción al hacer click
            obtenerUsuarios(usuarioIntent);
        });
    }

    private boolean emptyEdits() { //método que revisa si los editText estan vacíos
        if(bindingNuevEmp.editNombEmpleadoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editApellidosNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editTelefonoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editDireccionNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editCentroDeTrabajoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editFechaNacimientoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editCorreoNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editContrasenaNuevEmp.getText().toString().isEmpty()||bindingNuevEmp.editRepetirContraNuevEmp.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    private Usuario modeloEmpleado(Usuario usuarioAdmin){ //método que devuelve un objeto Usuario con los datos que contienen los EditText
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(String.valueOf(bindingNuevEmp.editNombEmpleadoNuevEmp.getText()));
        usuario.setApellidosUsuario(String.valueOf(bindingNuevEmp.editApellidosNuevEmp.getText()));
        usuario.setTelefono(Integer.parseInt(String.valueOf(bindingNuevEmp.editTelefonoNuevEmp.getText())));
        usuario.setDireccion(String.valueOf(bindingNuevEmp.editDireccionNuevEmp.getText()));
        usuario.setEmpresaUsuario(String.valueOf(usuarioAdmin.getEmpresaUsuario())); //la empresa será la misma del Admin
        usuario.setLugarTrabajo(String.valueOf(bindingNuevEmp.editCentroDeTrabajoNuevEmp.getText()));
        usuario.setFechaNacimiento(String.valueOf(bindingNuevEmp.editFechaNacimientoNuevEmp.getText()));
        usuario.setCorreoUsuario(String.valueOf(bindingNuevEmp.editCorreoNuevEmp.getText()));
        usuario.setContrasena(String.valueOf(bindingNuevEmp.editContrasenaNuevEmp.getText()));
        usuario.setEsAdmin(false);
        return usuario;
    }
    private void crearCuentaUsuario(Usuario usuario) { //creamos un Usuario en la base de datos, pasandole por parámetro un objeto Usuario
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Integer> call= usuarioService.crearUsuario(usuario);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body()==1){
                    Toast.makeText(NuevoEmpleado.this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                    Log.d("Exito", "Exito al crear cuenta Usuario");
                    obtenerUsuarios(usuarioIntent);
                }else{
                    Toast.makeText(NuevoEmpleado.this,"Cuenta no creada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(NuevoEmpleado.this,"Cuenta no creada", Toast.LENGTH_SHORT).show();
                Log.e("Fallo", t.getMessage());
            }
        });
    }

    private void obtenerUsuarios(Usuario usuario) { //obtenemos la lista de usuarios según la empresa del usuario de la sesión
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<ArrayList<Usuario>> call = usuarioService.listarUsuarios(usuario.getEmpresaUsuario()); //hacemos una llamada a la Api para que nos devuelva la lista de usuarios de la empresa del usuario pasado
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                usuarios.addAll(response.body()); //añadimos en el arraylist de usuarios de la respuesta de la llamada a la Api
                Intent ListadoUsuariosIntent  = new Intent(NuevoEmpleado.this, ListadoUsuarios.class);
                ListadoUsuariosIntent.putExtra("usuario", usuarioIntent);
                ListadoUsuariosIntent.putExtra("ListadoUsuarios", usuarios);
                startActivity(ListadoUsuariosIntent);
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
            }
        });

    }

    private void fijarFechaEnEdit(int viewId) { //fijamos la fecha con DatePickerDialog en el EditText que se ha pasado por parámetro indicando su id
        EditText edit = (EditText) findViewById(viewId); //Creamos un objeto EditText y vinculamos la instancia al editText pasado por parámetro a través de su i
        final Calendar c = Calendar.getInstance(); //creamos un objeto calendar para obtener...
        int mYear = c.get(Calendar.YEAR); //el año en el momento actual
        int mMonth = c.get(Calendar.MONTH); //el mes en el momento actual
        int mDay = c.get(Calendar.DAY_OF_MONTH); //el día en el momento actual

        DatePickerDialog datePickerDialog = null; //definimos un DatePickerDialog para que el usuario pueda clicar una fecha facilmente a traves de un dialogo emergente
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> //creamos una nueva instancia de DatePickerDialog
                    edit.setText(LocalDate.of(year,monthOfYear+1, dayOfMonth).toString()), //ponemos la información en el editText al fijar una fecha en el dialogo mostrado
                    mYear, mMonth, mDay); //establecemos el año, el mes y el día que se va a mostrar al abrir el dialogo
        }
        datePickerDialog.show(); //mostramos el dialogo
    }

    private boolean comprobarCorreo(String correo){ //Comprobamos mediante expresiones regulares que la cadena pasada sea una estructura valida para un correo
        Pattern patron = Pattern.compile("^[A-Za-z0-9-_]+(\\.[A-Za-z0-9-_]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"); //Definimos el patron a comprobar
        Matcher coincide = patron.matcher(correo); //Le pasamos la cadena al interpretador de patrones
        if(coincide.matches()){ //si coincide devolveremos true
            return true;
        }else{ // en caso contrario false
            return false;
        }
    }
}