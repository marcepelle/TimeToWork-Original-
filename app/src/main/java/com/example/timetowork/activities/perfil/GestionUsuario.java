package com.example.timetowork.activities.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timetowork.R;

import com.example.timetowork.databinding.ActivityGestionUsuarioBinding;
import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.HorarioService;
import com.example.timetowork.utils.UsuarioService;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionUsuario extends AppCompatActivity {
    ActivityGestionUsuarioBinding bindinGesUs;
    Usuario usuarioIntent;
    ArrayList<Usuario> usuarios = new ArrayList<>();
    Usuario usuarioGestionado;
    ArrayList<Integer> anios;
    ArrayList<Horario> horarios = new ArrayList<Horario>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindinGesUs = ActivityGestionUsuarioBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View viewGesUs = bindinGesUs.getRoot(); //referencia a la vista raíz
        setContentView(viewGesUs); // para que sea la vista activa en la pantalla

        Bundle bundleLisUs = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        usuarioIntent = (Usuario) bundleLisUs.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent
        usuarioGestionado = (Usuario) bundleLisUs.getSerializable("usuarioGestionado"); //obtenemos el usuario a gestionar que se ha clicado en la lista del anterior activity

        fijarEdits(usuarioGestionado); //rellenamos los editext del activity
        Obtenerhorarios(usuarioGestionado); //obtención de los horarios del usuario gestionado

        bindinGesUs.btnCambiarContrasenaGesUs.setOnClickListener(v -> {
            AlertDialog dialog = dialogCambiarContrasena(usuarioGestionado); //Definimos un objeto de tipo AlertDialog y le asignamos una instancia a través del método dialogCambiarContrasena
            dialog.show(); //Mostramos el dialogo en el activity
        });

        bindinGesUs.btnEstablecerDatosGesUs.setOnClickListener(v -> { //botón establecer datos, actualiza los datos del usuario gestionado, acción al hacer clic
            if(emptyEdits()) { //Si los edits están vacíos
                Toast.makeText(this, "Rellenar los espacios", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!comprobarCorreo(bindinGesUs.editCorreoGesUs.getText().toString())){ //Si el correo no está bien estructurado
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
                return;
            }
            modificarDatos();
            actualizarUsuario(usuarioGestionado, usuarioIntent);
        });
        bindinGesUs.btnEliminarUsGesUs.setOnClickListener(v -> { //botón eliminar usuario, elimina el usuario y vuelve al activty ListadoUsuarios, acción al hacer clic
                dialogoEliminarUsuario();
        });
        bindinGesUs.btnInformeUsGesUs3.setOnClickListener(v -> { //botón informe, intent hacia el activity InformeEmpleado, acción al hacer clic
            Intent intentInforme = new Intent(GestionUsuario.this, InformeEmpleado.class);
            intentInforme.putExtra("usuario", usuarioIntent);
            intentInforme.putExtra("usuarioGestionado", usuarioGestionado);
            intentInforme.putExtra("horarios", horarios);
            intentInforme.putExtra("anios", anios);
            startActivity(intentInforme);
        });

        bindinGesUs.editFechaNacimientoGesUs.setFocusable(false);
        bindinGesUs.editFechaNacimientoGesUs.setOnClickListener(v -> {
            fijarFechaEnEdit(bindinGesUs.editFechaNacimientoGesUs.getId());
        });

        bindinGesUs.btnVolverGesUs.setOnClickListener(v -> { //botón volver, vuelve al activity anterior, acción al hacer clic
            obtenerUsuarios(usuarioIntent);
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
                    Toast.makeText(GestionUsuario.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                    Intent actualizarIntent = new Intent(GestionUsuario.this, GestionUsuario.class);
                    actualizarIntent.putExtra("usuario", usuarioIntent);
                    actualizarIntent.putExtra("usuarioGestionado", response.body());
                    startActivity(actualizarIntent);
                    return;
                }
                Toast.makeText(GestionUsuario.this, "La contraseña no se ha podido actualizar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(GestionUsuario.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void modificarDatos() {
        usuarioGestionado.setNombreUsuario(String.valueOf(bindinGesUs.editNombEmpleadoGesUs.getText()));
        usuarioGestionado.setApellidosUsuario(String.valueOf(bindinGesUs.editApellidosGesUs.getText()));
        usuarioGestionado.setTelefono(Integer.valueOf(String.valueOf(bindinGesUs.editTelefonoGesUs.getText())));
        usuarioGestionado.setDireccion(String.valueOf(bindinGesUs.editDireccionGesUs.getText()));
        usuarioGestionado.setLugarTrabajo(String.valueOf(bindinGesUs.editCentroDeTrabajoGesUs.getText()));
        usuarioGestionado.setFechaNacimiento(String.valueOf(bindinGesUs.editFechaNacimientoGesUs.getText()));
        usuarioGestionado.setCorreoUsuario(String.valueOf(bindinGesUs.editCorreoGesUs.getText()));
    }

    private boolean emptyEdits() { //comprobamos que ningún editext este vacío, excepto la contraseña que si esta vacío no se cambia
        if(bindinGesUs.editNombEmpleadoGesUs.getText().toString().isEmpty()||bindinGesUs.editApellidosGesUs.getText().toString().isEmpty()||bindinGesUs.editTelefonoGesUs.getText().toString().isEmpty()||bindinGesUs.editDireccionGesUs.getText().toString().isEmpty()||bindinGesUs.editCentroDeTrabajoGesUs.getText().toString().isEmpty()||bindinGesUs.editFechaNacimientoGesUs.getText().toString().isEmpty()||bindinGesUs.editCorreoGesUs.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    private void dialogoEliminarUsuario() { //crea un dialogo emergente al intentar eliminar usuario
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estas seguro que quieres eliminar el usuario?") //establecemos el mensaje del dialogo
                .setPositiveButton("Si", (dialog, which) -> { //si la persona hace clic en Si
                    eliminarUsuario(usuarioGestionado);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()); //si la persona hace clic en Cancelar no pasa nada
        builder.show(); //mostramos el dialogo
    }

    private void eliminarUsuario(Usuario usuarioBorrar) { //eliminar usuario de la base de datos
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Integer> call = usuarioService.borrarUsuario(usuarioBorrar.getIdUsuario());
        Log.d("Borrando", "eliminarUsuario: " + usuarioBorrar.getNombreUsuario());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body()==1){
                    obtenerUsuarios(usuarioIntent);
                    Toast.makeText(GestionUsuario.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(GestionUsuario.this, "Usuario no eliminado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(GestionUsuario.this, "Usuario No borrado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fijarEdits(Usuario usuarioGestionado) { //rellenamos los editext del activity con la información del usuario
        bindinGesUs.editNombEmpleadoGesUs.setText(usuarioGestionado.getNombreUsuario());
        bindinGesUs.editApellidosGesUs.setText(usuarioGestionado.getApellidosUsuario());
        bindinGesUs.editTelefonoGesUs.setText(String.valueOf(usuarioGestionado.getTelefono()));
        bindinGesUs.editDireccionGesUs.setText(usuarioGestionado.getDireccion());
        bindinGesUs.editCentroDeTrabajoGesUs.setText(usuarioGestionado.getLugarTrabajo());
        bindinGesUs.editFechaNacimientoGesUs.setText(usuarioGestionado.getFechaNacimiento());
        bindinGesUs.editCorreoGesUs.setText(usuarioGestionado.getCorreoUsuario());
    }

    private void actualizarUsuario(Usuario usuarioGestionado, Usuario usuarioIntent) { //actualizamos el usuario en la base de datos y recargamos el activity para obtener los datos nuevos
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<Usuario> call = usuarioService.actualizarUsuario(usuarioGestionado);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
               if(response.body()!=null) {
                   Intent actualizarIntent = new Intent(GestionUsuario.this, GestionUsuario.class);
                   actualizarIntent.putExtra("usuario", usuarioIntent);
                   actualizarIntent.putExtra("usuarioGestionado", response.body());
                   Log.d("ResUsuario", "Usuario id:" + response.body().getIdUsuario() + response.body().getNombreUsuario());
                   startActivity(actualizarIntent);
                   return;
               }
                Toast.makeText(GestionUsuario.this, "El usuario no se ha podido actualizar, puede que el correo ya exista", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(GestionUsuario.this, "Usuario no actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Obtenerhorarios(Usuario usuario) { //obtenemos el listado de horarios para el usuario que le pasemos y rellenamos el array anios
        HorarioService horarioService = Apis.getHorarioService();
        Call<ArrayList<Horario>> call = horarioService.getHorarios(usuario.getCorreoUsuario());
        call.enqueue(new Callback<ArrayList<Horario>>() {
            @Override
            public void onResponse(Call<ArrayList<Horario>> call, Response<ArrayList<Horario>> response) {
                horarios.addAll(response.body()); //añade todos los objetos horarios de la respuesta del array de la llamada a la Api
                listarAnios(horarios); //rellenamos el array anios
            }

            @Override
            public void onFailure(Call<ArrayList<Horario>> call, Throwable t) {

            }
        });
    }

    private void listarAnios(ArrayList<Horario> horarios) { //Listando los años de los que dispone historial de horarios el trabajador para poder rellenarlos en el spinner del activity de informe
        ArrayList<Integer> arrayAuxAnios = new ArrayList<Integer>();
        for (int i = 0; i < horarios.size(); i++) {  //recorremos la lista de horarios del usuario gestionado y recogemos del campo fecha el año y lo añadimos en el array auxiliar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                arrayAuxAnios.add(LocalDate.parse(horarios.get(i).getFecha()).getYear());
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            anios = (ArrayList<Integer>) arrayAuxAnios.stream().distinct().collect(Collectors.toList());//gracias al metodo stream que permite trabajar con el modelo de datos de una colección, conseguimos los años que sean distintos del array
            Log.d("GesionUsuario", anios.toString());
            if(anios.isEmpty()){ //si el array está vacío devolvemos al menos el año actual
                    anios.add(LocalDate.now().getYear());
            }
        }
    }

    private void obtenerUsuarios(Usuario usuario) { //obtenemos la lista de usuarios según la empresa del usuario de la sesión
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<ArrayList<Usuario>> call = usuarioService.listarUsuarios(usuario.getEmpresaUsuario()); //hacemos una llamada a la Api para que nos devuelva la lista de usuarios de la empresa del usuario pasado
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                usuarios.addAll(response.body()); //añadimos en el arraylist de usuarios de la respuesta de la llamada a la Api
                Intent ListadoUsuariosIntent  = new Intent(GestionUsuario.this, ListadoUsuarios.class);
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