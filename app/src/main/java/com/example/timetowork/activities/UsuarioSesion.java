package com.example.timetowork.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timetowork.activities.horarios.HorarioSelect;
import com.example.timetowork.activities.mensajes.MensajesPerfil;
import com.example.timetowork.activities.perfil.PerfilAdmin;
import com.example.timetowork.activities.perfil.PerfilEmpleado;
import com.example.timetowork.databinding.ActivityUsuarioSesionBinding;
import com.example.timetowork.models.Horario;
import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.HorarioService;
import com.example.timetowork.utils.MensajeService;
import com.example.timetowork.utils.UsuarioService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.O)
public class UsuarioSesion extends AppCompatActivity {
    LocalDate fecha;
    LocalTime hora;
    private String horaEntrada, horaSalida, currentDate, currentTime;
    ActivityUsuarioSesionBinding bindingSesion;
    private boolean fichadoEntrada, fichadoSalida, existeHorario;
    ArrayList<ArrayList<String>> correoUsuarios;
    String[] centrosUsuarios;
    ArrayList<Mensaje> mensajesRecibidos;
    ArrayList<Mensaje> mensajesEnviados;

    int posicionCentro;
    int posicionCorreo;

    ArrayList<Integer> anios;
    ArrayList<Horario> horarios = new ArrayList<Horario>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindingSesion = ActivityUsuarioSesionBinding.inflate(getLayoutInflater()); // crea una instancia de la clase de vinculación para la actividad que se usará
        View view = bindingSesion.getRoot(); //referencia a la vista raíz
        setContentView(view); // para que sea la vista activa en la pantalla

        Bundle intentObtenido = getIntent().getExtras(); //obtenemos los datos pasados en el intent del anterior activity
        Usuario usuarioIntent = (Usuario) intentObtenido.getSerializable("usuario"); //obtenemos el usuario de la sesión pasado por intent

        bindingSesion.textTitleUsrProfile.setText("Usuario: " + usuarioIntent.getNombreUsuario()); //añadimos el nombre en el título
        fecha = LocalDate.now();
        currentDate =" " + fecha; //obteniendo la fecha actual
        bindingSesion.textFechaUsrProfile.append(currentDate); //añadimos la fecha actual al textview

        obtenerFicha(usuarioIntent); //obtenemos si ya se ha fichado en el dia e insertamos la información en los TextView
        CorreoCentroUsuariosParaSpinner(usuarioIntent); //obtenemos los datos de los centros y los correos para cada centro, para que puedan usarse en los activities EnviarMensaje y HorarioSelect
        Obtenerhorarios(usuarioIntent); //obtención de los horarios para el usuario pasado
        ObtenerRecibidos(usuarioIntent); //obtenemos el listado de mensajes recibidos para el usuario pasado
        ObtenerEnviados(usuarioIntent); //obtenemos el listado de mensajes envíados para el usuario pasado

        bindingSesion.btnPerfil.setOnClickListener(v -> { // Botón Perfil, hacemos un intent a...
            Intent intentPerfil;
            if(usuarioIntent.isEsAdmin()) { // PerfilAdmin si el usuario de la sesión es Administrador
                Log.d("EsAdmin", "EsAdmin: " + usuarioIntent.isEsAdmin());
                intentPerfil = new Intent(UsuarioSesion.this, PerfilAdmin.class);
            }
            else{ //o a PerfilEmpleado si no es administrador
                Log.d("EsAdmin", "EsAdmin: " + usuarioIntent.isEsAdmin());
                intentPerfil = new Intent(UsuarioSesion.this, PerfilEmpleado.class);
            }
            intentPerfil.putExtra("usuario", usuarioIntent);
            startActivity(intentPerfil);
        });
        bindingSesion.btnEntradaUsrProfile.setOnClickListener(v -> { //evento al clicar el boton de fichar entrada
            if (!fichadoEntrada && existeHorario) { //si ya se ha fichado la entrada no se puede volver a fichar
                ficharEntrada(usuarioIntent);
            }
        });
        bindingSesion.btnSalidaUsrProfile.setOnClickListener(v -> { //evento al clicar el boton de fichar salida
            if(!fichadoSalida && existeHorario) { //si ya se ha fichado la salida no se puede volver a fichar
                ficharSalida(usuarioIntent);
            }
        });
        bindingSesion.btnHorario.setOnClickListener(v -> { //Botón Horario, hacemos un intent a HorarioSelect, acción al hacer clic
            Intent intentHorarios = new Intent(UsuarioSesion.this, HorarioSelect.class);
            Log.d("ResIntent", correoUsuarios.toString());
            intentHorarios.putExtra("usuario", usuarioIntent);
            intentHorarios.putExtra("usuarioSpinner", usuarioIntent);
            intentHorarios.putExtra("mes", 0);
            intentHorarios.putExtra("posicionCentro", posicionCentro);
            intentHorarios.putExtra("posicionEmpleado", posicionCorreo);
            intentHorarios.putExtra("CorreosSpinner", correoUsuarios);
            intentHorarios.putExtra("CentrosSpinner", centrosUsuarios);
            intentHorarios.putExtra("posicionAnios", 0);
            intentHorarios.putExtra("anios", anios);
            startActivity(intentHorarios);
        });
        bindingSesion.btnMensajes.setOnClickListener(v -> { //Botón Mensajes, hacemos un intent a MensajesPerfil, acción al hacer clic
            Intent intentMensajes = new Intent(UsuarioSesion.this, MensajesPerfil.class);
            intentMensajes.putExtra("usuario", usuarioIntent);
            intentMensajes.putExtra("posicionCentro", posicionCentro);
            intentMensajes.putExtra("posicionCorreo", posicionCorreo);
            intentMensajes.putExtra("CorreosSpinner", correoUsuarios);
            intentMensajes.putExtra("CentrosSpinner", centrosUsuarios);
            intentMensajes.putExtra("mensajesRecibidos", mensajesRecibidos);
            intentMensajes.putExtra("mensajesEnviados", mensajesEnviados);
            startActivity(intentMensajes);

        });
        bindingSesion.btnCerrarSesionUsrProfile.setOnClickListener(v -> {  //Botón cerrar sesión, cerramos la sesión del usuario, acción al hacer clic
            dialogoCierreSesion();
        });
    }

    private void dialogoCierreSesion() { //crea un dialogo emergente al intentar cerrar sesión
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cerrar sesión") //establecemos el mensaje del dialogo
                .setPositiveButton("Si", (dialog, which) -> { //si la persona hace clic en Si
                    Intent intentCerrarSesion = new Intent(UsuarioSesion.this, MainActivity.class); //hacemos un intent al MainActivity
                    intentCerrarSesion.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //borramos las tareas de los activities anteriores, del actual y comenzamos el activity con una nueva tarea
                    startActivity(intentCerrarSesion);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()); //si la persona hace clic en Cancelar no pasa nada
        builder.show(); //mostramos el dialogo
    }

    private void ObtenerEnviados(Usuario usuarioIntent) { //obtenemos el listado de mensajes enviados para el usuario pasado
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getEnviados(usuarioIntent.getCorreoUsuario()); //hacemos la llamada a la Api para que nos devuelva los mensajes envíados para el usuario que pasemos
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){ //si la respuesta de la solicitud a la Api no está vacío
                    mensajesEnviados = response.body(); //rellenamos mensajesEnviados con la respuesta
                    return;
                }
                mensajesEnviados = new ArrayList<Mensaje>(); //si la respuesta estaba vacía entonces inicializamos mensajesEnviados para que no de un error de NullPointerException
            }
            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Fallo, Mensajes no obtenidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ObtenerRecibidos(Usuario usuarioIntent) { //obtenemos el listado de mensajes recibidos para el usuario pasado
        MensajeService mensajeService = Apis.getMensajeService();
        Call<ArrayList<Mensaje>> call = mensajeService.getRecibidos(usuarioIntent.getCorreoUsuario()); //hacemos la llamada a la Api para que nos devuelva los mensajes recibidos para el usuario que pasemos
        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                if(response.body().size()!=0){ //si la respuesta de la solicitud a la Api no está vacío
                    mensajesRecibidos = response.body(); //rellenamos mensajesRecibidos con la respuesta
                    return;
                }
                mensajesRecibidos= new ArrayList<Mensaje>(); //si la respuesta estaba vacía entonces inicializamos mensajesRecibidos para que no de un error de NullPointerException
            }
            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Fallo, Mensajes no obtenidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerFicha(Usuario usuario){ //obtenemos si ya se ha fichado en el dia e insertamos la información en los TextView
        Horario horario = modeloHorario(usuario); // creamos un objeto horario con la fecha actual para el usuario de la sesión
        HorarioService horarioService = Apis.getHorarioService();
        Call<ArrayList<Horario>> call = horarioService.obtenerFichar(horario.getCorreoEmpleado(), LocalDate.parse(horario.getFecha())); //hacemos la llamada a la Api para que nos devuelva, si existe, el horario de la fecha actual para el usuario de la sesión
        call.enqueue(new Callback<ArrayList<Horario>>() {
            @Override
            public void onResponse(Call<ArrayList<Horario>> call, Response<ArrayList<Horario>> response) {
                if(response.body().size()!=0) { //si la respuesta de la llamada a la Api nos devuelve un arraylist superior a 0 es que existe un horario para la fecha indicada
                    existeHorario = true; //fijamos en nuestro booleano que existe el horario
                    Log.d("Ficha", "Hora Entrada: " + response.body().get(0).getFichaEntrada() + " Hora Salida: " + response.body().get(0).getFichaSalida());
                    bindingSesion.textHoraInUsrProfile.setText("Hora Entrada: " + response.body().get(0).getFichaEntrada()); //establecemos en el textview la hora fichada de entrada con la respuesta de la Api
                    if (response.body().get(0).getFichaEntrada() == null) { //si no se ha fichado la entrada
                        fichadoEntrada = false;
                    } else { //si se ha fichado la entrada
                        fichadoEntrada = true;
                    }
                    bindingSesion.textHoraOutUsrProfile.setText("Hora Salida: " + response.body().get(0).getFichaSalida()); //establecemos en el textview la hora fichada de salida con la respuesta de la Api
                    if (response.body().get(0).getFichaSalida() == null) { //si no se ha fichado la salida
                        fichadoSalida = false;
                    } else { //si se ha fichado la salida
                        fichadoSalida = true;
                    }
                }else { //si no existe el horario para la fecha y usuario indicado
                    Log.d("Ficha", "Horario no conseguido");
                    existeHorario = false;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Horario>> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this,"Horario no conseguido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Horario modeloHorario(Usuario usuario) { //devuelve un objeto horario para el usuario pasado con la fecha y la hora actual
        hora = LocalTime.now();
        fecha = LocalDate.now();
        Horario horario = new Horario();
        horario.setEmpleado((usuario.isEsAdmin())?usuario.getNombreUsuario() : usuario.getNombreUsuario() + " " +usuario.getApellidosUsuario()); //Fijamos el nombre y los apellidos del usuario para el horario
        horario.setCorreoEmpleado(usuario.getCorreoUsuario()); //correo del usuario para el horario
        horario.setCentroTrabajo(usuario.getLugarTrabajo()); //centro de trabajo del usuario para el horario
        horario.setUsuario_fk(usuario); // Fijamos el usuario para el horario
        horario.setFecha(fecha.toString()); //Fijamos la fecha del horario
        horario.setFichaEntrada(String.valueOf(hora)); //Fijamos la hora que se usará en caso que se vaya a fichar la entrada
        horario.setFichaSalida(String.valueOf(hora)); //Fijamos la hora que se usará en caso que se vaya a fichar la salida
        return horario; //Devolvemos el horario
    }

    private void ficharEntrada(Usuario usuario) { //fichamos la entrada en la base de datos y lo fijamos en el TextView
        hora = LocalTime.now(); //obtenemos la hora actual
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm:ss"); //creamos un objeto DateTimeFormatter para poder darle un formato al objeto LocalTime
        fecha = LocalDate.now(); //obtenemos la fecha actual
        horaEntrada =" " + hora.format(formatoHora); //le damos formato a la hora
        bindingSesion.textHoraInUsrProfile.setText("Hora Entrada: " + horaEntrada); //añadimos la hora de entrada al textview
        fichadoEntrada= true; //fijamos el booleano como true para que no se pueda volver a fichar la entrada
        Horario horario = modeloHorario(usuario); //creamos un objeto horario para el usuario pasado para poder fichar la entrada
        HorarioService horarioService = Apis.getHorarioService();
        Call<Void> call = horarioService.ficharEntrada(horario); //hacemos una llamada a la Api para insertar la ficha de entrada en el horario para la fecha y usuario pasado
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(UsuarioSesion.this, "Fichado entrada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Fallo al fichar", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ficharSalida(Usuario usuario) { //fichamos la salida en la base de datos y lo fijamos en el TextView
        hora = LocalTime.now(); //obtenemos la hora actual
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm:ss"); //creamos un objeto DateTimeFormatter para poder darle un formato al objeto LocalTime
        fecha = LocalDate.now(); //obtenemos la fecha actual
        horaSalida =" " + hora.format(formatoHora); //le damos formato a la hora
        bindingSesion.textHoraOutUsrProfile.setText("Hora Salida: " + horaSalida); //añadimos la hora de salida al textview
        fichadoSalida = true; //fijamos el booleano como true para que no se pueda volver a fichar la salida
        Horario horario = modeloHorario(usuario); //creamos un objeto horario para el usuario pasado para poder fichar la salida
        HorarioService horarioService = Apis.getHorarioService();
        Call<Void> call = horarioService.ficharSalida(horario); //hacemos una llamada a la Api para insertar la ficha de salida en el horario para la fecha y usuario pasado
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(UsuarioSesion.this, "Fichado salida", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Fallo al fichar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CorreoCentroUsuariosParaSpinner(Usuario usuario) { //obtenemos los datos de los centros y los correos para cada centro, para que puedan usarse en los activities EnviarMensaje y HorarioSelect
        UsuarioService usuarioService = Apis.getUsuarioService();
        Call<ArrayList<Usuario>> call = usuarioService.listarUsuarios(usuario.getEmpresaUsuario()); //hacemos una llamada a la Api para que nos devuelva un listado de usuarios de la empresa del usuario que pasamos
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                if(response.body().size()!=0) { //si el listado de usuarios no esta vacío
                    listarCentroYCorreos(response.body(), usuario); //llamamos al método listarCentrosYCorreos para llenar centroUsuarios y correoUsuarios
                    return;
                }
                //si el listado de usuarios está vacío inicializamos centroUsuarios y correoUsuarios
                centrosUsuarios = new String[]{"vacio"};
                correoUsuarios = new ArrayList<ArrayList<String>>();
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Lista Correos no Obtenida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listarCentroYCorreos(ArrayList<Usuario> response, Usuario usuario) { //rellenamos las variables centroUsuarios y correoUsuarios
        String [] arrayAuxCentros = new String[response.size()]; //creamos un array de string auxiliar que tendrá el tamaño del ArrayList de la respuesta de la Api
        Log.d("ResBody", response.toString());
        for (int i = 0; i < response.size(); i++) { //rellenamos arrayAuxCentros con los centros de los trabajadores
            arrayAuxCentros[i] = response.get(i).getLugarTrabajo();
        }
        centrosUsuarios = Arrays.stream(arrayAuxCentros).distinct().toArray(String[]::new); //desde el array de string auxiliar con stream obtenemos solo los valores distintos de los centros
        correoUsuarios = new ArrayList<ArrayList<String>>(); //inicializamos correoUsuarios que será un ArrayList de ArrayList de los correos de cada centro

        for(int i=0;i<centrosUsuarios.length;i++){
            ArrayList<String> arrayAuxCorreos = new ArrayList<>(); //creamos un ArrayList auxiliar para rellenarlo con los correos de los usuarios por el centro iterado
            int contador = 0;
            if(centrosUsuarios[i].equals(usuario.getLugarTrabajo())){ //si el centro en la iteración coincide con el centro de trabajo del usuario pasado nos quedamos con la posición para que al pasar a otro activity le pasemos la posición del centro del usuario
                posicionCentro = i;
            }
            for (int j = 0; j < response.size(); j++) { //iteramos por el tamaño de la lista de usuarios que nos dió la respuesta
                if(centrosUsuarios[i].equals(response.get(j).getLugarTrabajo())) { //si el centro iterado(primer for) coincide con el centro del usuario de la lista de usuarios(segundo for) entonces...
                    Log.d("ResCorreoCentro", "cen: " + centrosUsuarios[i] + "res: " + response.get(j).getLugarTrabajo());
                    arrayAuxCorreos.add(response.get(j).getCorreoUsuario()); //añadimos el correo del usuario en el array auxiliar
                    if(response.get(j).getCorreoUsuario().equals(usuario.getCorreoUsuario())){ //si el correo en el usuario de la lista de usuarios iterada coincide con el correo del usuario pasado nos quedamos con la posición para que al pasar a otro activity le pasemos la posición del correo del usuario
                        posicionCorreo = contador; //posición dentro del array auxiliar
                    }
                    contador++; //añadimos una unidad al contador solo si coincide el centro del usuario que es cuando se añade un correo al array auxiliar
                }
            }
            Log.d("ResCorreoCentro", arrayAuxCorreos.toString());
            correoUsuarios.add(arrayAuxCorreos); //una vez se ha iterado el centro se añade el array auxiliar con los correos de ese centro
        }
        Log.d("ResCorreoCentro", correoUsuarios.toString());
    }

    private void Obtenerhorarios(Usuario usuarioIntent) { //obtención de los horarios para el usuario pasado
        HorarioService horarioService = Apis.getHorarioService();
        Call<ArrayList<Horario>> call = horarioService.getHorarios(usuarioIntent.getCorreoUsuario()); //hacemos una llamada a la Api para que nos devuelva el listado de horarios para el usuario pasado
        call.enqueue(new Callback<ArrayList<Horario>>() {
            @Override
            public void onResponse(Call<ArrayList<Horario>> call, Response<ArrayList<Horario>> response) {
                horarios.addAll(response.body()); //rellenamos la variable horarios con la respuesta de la solicitud a la Api
                listarAnios(horarios); //listamos los años que existen en los horarios obtenidos
            }

            @Override
            public void onFailure(Call<ArrayList<Horario>> call, Throwable t) {
                Toast.makeText(UsuarioSesion.this, "Fallo, Horarios no obtenidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listarAnios(ArrayList<Horario> horarios) {
        ArrayList<Integer> arrayAuxAnios = new ArrayList<Integer>(); //creamos un array auxiliar que almacenará los años de la lista de horarios
        for (int i = 0; i < horarios.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                arrayAuxAnios.add(LocalDate.parse(horarios.get(i).getFecha()).getYear());//obtenemos el año del horario iterado
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            anios = (ArrayList<Integer>) arrayAuxAnios.stream().distinct().collect(Collectors.toList()); //almacenamos en anios los valores distintos que hemos obtenido en el array auxiliar gracias a stream
            Log.d("UsSes", anios.toString());
        }
        if(anios.isEmpty()){ //si anios esta vacío añadimos al menos el año actual
            anios.add(LocalDate.now().getYear());
        }
    }
}