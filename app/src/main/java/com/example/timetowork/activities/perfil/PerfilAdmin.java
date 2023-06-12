package com.example.timetowork.activities.perfil;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.timetowork.R;
import com.example.timetowork.activities.UsuarioSesion;
import com.example.timetowork.databinding.ActivityPerfilAdminBinding;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.UsuarioService;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilAdmin extends AppCompatActivity {

    ActivityPerfilAdminBinding bindingPerfilAdmin;
    Usuario usuarioIntent;

    ArrayList<Usuario> usuarios = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindingPerfilAdmin = ActivityPerfilAdminBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View view = bindingPerfilAdmin.getRoot(); //referencia a la vista raíz
        setContentView(view); // para que sea la vista activa en la pantalla

        Bundle bundlePerAdmin = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        usuarioIntent = (Usuario) bundlePerAdmin.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent

        fijarEditTexts(usuarioIntent); //rellenamos la información en los EditText con los datos del Usuario de la sesión
        obtenerUsuarios(usuarioIntent);

        bindingPerfilAdmin.btnCambiarContrasenaPerAdm.setOnClickListener(v -> {
            AlertDialog dialog = dialogCambiarContrasena(usuarioIntent); //Definimos un objeto de tipo AlertDialog y le asignamos una instancia a través del método dialogCambiarContrasena
            dialog.show(); //Mostramos el dialogo en el activity
        });

        bindingPerfilAdmin.btnEstabDatosPerAdm.setOnClickListener(v -> { //Botón establecer datos, llamamos al método modeloUsuario y al método actualizarUsuario, acción al hacer clic
            if(emptyEdits()) { //Si los edits están vacíos
                Toast.makeText(this, "Rellenar los espacios", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!comprobarCorreo(bindingPerfilAdmin.editCorreoPerAdm.getText().toString())){ //Si el correo no está bien estructurado
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
                return;
            }
            modificarDatos();
            actualizarUsuario(usuarioIntent);
        });
        bindingPerfilAdmin.btnGestionUsuariosPerAdm.setOnClickListener(v -> { //Botón gestión usuarios, hacemos un  intent al activity ListadoUsuarios, acción al hacer clic
            Intent intentGestionUusario = new Intent(PerfilAdmin.this, ListadoUsuarios.class);
            intentGestionUusario.putExtra("usuario", usuarioIntent);
            intentGestionUusario.putExtra("ListadoUsuarios", usuarios);
            startActivity(intentGestionUusario);
        });

        bindingPerfilAdmin.btnVolverPerAdm.setOnClickListener(v -> { //Botón volver, volvemos al activity UsuarioSesion, acción al hacer clic
            Intent intentVolver = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intentVolver = new Intent(PerfilAdmin.this, UsuarioSesion.class);
            }
            intentVolver.putExtra("usuario", usuarioIntent);
            startActivity(intentVolver);
        });
    }

    public AlertDialog dialogCambiarContrasena(Usuario usuario) { //Devuelve un objeto AlertDialog para poder responder el mensaje en cuestión
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //Con un elemento Builder podremos definir las partes de la creación de un objeto de clase AlertDialog
        LayoutInflater inflater = this.getLayoutInflater(); //Obtenemos el layout donde se mostrará el dialogo
        View viewAlert = inflater.inflate(R.layout.dialog_cambiar_password, null); //Creamos la vista en el Layout pasandole por parámetro el Layout que se va a mostrar en la vista
        EditText editContrasena = (EditText) viewAlert.findViewById(R.id.editContrasenaDialog); //Obtenemos el EdiText de la vista del dialogo donde irá el contenido de la contraseña
        EditText editRepeatContrasena = (EditText) viewAlert.findViewById(R.id.editRepeatContrasenaDialog); //Obtenemos el EdiText de la vista del dialogo donde irá el contenido de la contraseña
        Button btnActualizarContra = (Button)  viewAlert.findViewById(R.id.btnActualizarContraDialog);
        btnActualizarContra.setOnClickListener(v -> {
            if((editContrasena.getText().toString().equals(editRepeatContrasena.getText().toString()))&&(!editContrasena.getText().toString().isEmpty() && !editRepeatContrasena.getText().toString().isEmpty())) {
                usuario.setContrasena(editContrasena.getText().toString());
                actualizarContrasena(usuario);
            }else{
                Toast.makeText(this, "No coinciden las contraseñas", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(viewAlert) //En la vista del dialogo...
                .setNegativeButton("Cancelar", (dialog, which) -> { //En caso de que el usuario pulse cancelar no se hará nada
                    dialog.dismiss();
                });
        return builder.create(); //Devolvemos el objeto AlertDialog creandolo con el builder
    }

    private void actualizarContrasena(Usuario usuario) {
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.actualizarContrasena(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.body()!=null) {
                    Toast.makeText(PerfilAdmin.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                    Intent actualizarIntent = new Intent(PerfilAdmin.this, PerfilAdmin.class);
                    actualizarIntent.putExtra("usuario", response.body());
                    startActivity(actualizarIntent);
                    return;
                }
                Toast.makeText(PerfilAdmin.this, "La contraseña no se ha podido actualizar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilAdmin.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean emptyEdits() { //comprobamos que ningún editext este vacío, excepto la contraseña que si esta vacío no se cambia
        if(bindingPerfilAdmin.editNombAdminPerAdm.getText().toString().isEmpty()||bindingPerfilAdmin.editCorreoPerAdm.getText().toString().isEmpty()||bindingPerfilAdmin.editTelefonoPerAdm.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }
    private void fijarEditTexts(Usuario usuarioIntent) { //fijamos la información del usuario en los EditText
        bindingPerfilAdmin.editNombAdminPerAdm.setText(usuarioIntent.getNombreUsuario());
        bindingPerfilAdmin.editCorreoPerAdm.setText(usuarioIntent.getCorreoUsuario());
        bindingPerfilAdmin.editTelefonoPerAdm.setText(String.valueOf(usuarioIntent.getTelefono()));
    }

    private void modificarDatos() { //cambiamos los datos del usuario con la información de los EditText
        usuarioIntent.setNombreUsuario(String.valueOf(bindingPerfilAdmin.editNombAdminPerAdm.getText()));
        usuarioIntent.setCorreoUsuario(String.valueOf(bindingPerfilAdmin.editCorreoPerAdm.getText()));
        usuarioIntent.setTelefono(Integer.valueOf(String.valueOf(bindingPerfilAdmin.editTelefonoPerAdm.getText())));
    }

    private void actualizarUsuario(Usuario usuario) { //actualizamos el usuario en la base de datos y actualizamos el activity haciendo un intent
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.actualizarUsuario(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
               if(response.body()!=null) {
                   Intent actualizarIntent = new Intent(PerfilAdmin.this, PerfilAdmin.class);
                   actualizarIntent.putExtra("usuario", response.body()); //habiendo implementado la interfaz serializable puedo pasar un objeto a otra activity
                   Log.d("ResUsuario", "Usuario id:" + response.body().getIdUsuario() + response.body().getNombreUsuario());
                   startActivity(actualizarIntent);
                   return;
               }
                Toast.makeText(PerfilAdmin.this, "El usuario no se ha podido actualizar, puede que el correo ya exista", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilAdmin.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
            }
        });

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