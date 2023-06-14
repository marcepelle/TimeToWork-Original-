package com.example.timetowork.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timetowork.R;
import com.example.timetowork.activities.mensajes.ResponderMensaje;
import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;
import com.example.timetowork.utils.Apis;
import com.example.timetowork.utils.MensajeService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MensajesAdapter extends RecyclerView.Adapter <MensajesAdapter.MensajesHolder> { //Está clase que hereda de RecyclerView.Adapter creará el modelo de datos para el listado de mensajes

    Context context; //Contexto del layout donde se insertará el adaptador
    ArrayList<Mensaje> mensajes; //Listado de mensajes
    Usuario usuarioIntent; //usuario de la sesión
    ArrayList<ArrayList<String>> correoUsuarios; //Listados de correos de los usuarios por centro
    String[] centrosUsuarios; //listado de centros de la empresa del usuario

    public MensajesAdapter(Context context, ArrayList<Mensaje> mensajes, Usuario usuarioIntent, ArrayList<ArrayList<String>> correoUsuarios, String[] centrosUsuarios) { //Constructor de MensajesAdapter
        this.context = context;
        this.mensajes = mensajes;
        this.usuarioIntent = usuarioIntent;
        this.correoUsuarios = correoUsuarios;
        this.centrosUsuarios = centrosUsuarios;
    }

    @NonNull
    @Override
    public MensajesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Este metódo será llamado cada vez que se requiera crear un item o elemento del listado, creará la vista del item
        View view = LayoutInflater.from(context).inflate(R.layout.content_listado_mensajes, parent, false); //inflamos el layout del item, pasandole por parámetro el layout base de creación para la vista, parent que es el ViewGroup en el que se agregará la nueva Vista e indicamos como false que adjunte instantaneamente la vista en el parent para evitar un IllegalStateException
        return new MensajesHolder(view); //devolvemos el MensajesHolder
    }

    @Override
    public void onBindViewHolder(@NonNull MensajesAdapter.MensajesHolder holder, int position) { //Este método va a vincular los datos al ViewHolder para una posición dada en el listado del RecyclerView
        holder.fecha.setText(mensajes.get(position).getFecha()); //fijamos la fecha del mensaje en el TextView
        holder.hora.setText(mensajes.get(position).getHora()); //fijamos la hora del mensaje en el TextView
        holder.de.setText(mensajes.get(position).getDe()); //fijamos el correo de la persona que envía el mensaje en el TextView
        holder.para.setText(mensajes.get(position).getPara()); //fijamos el correo de la persona que recibe el mensaje en el TextView
        holder.asunto.setText(mensajes.get(position).getAsunto()); //fijamos el asunto del mensaje en el TextView
        if(usuarioIntent.getCorreoUsuario().equals(mensajes.get(position).getDe())){ //si el usuario de la sesión coincide con el usuario que envía el mensaje quiere decir que es un mensaje envíado
            holder.accion.setText("ver"); //fijamos la acción en el TextView
            holder.accion.setOnClickListener(v -> { //en el TextView acción, al clicar, mostraremos a través de un dialogo emergente(AlertDialog) el mensaje envíado en cuestión y lo fijaremos como visto
                if(!mensajes.get(position).isVistoDe()){ //Si el mensaje no se había fijado como visto por la persona que lo envía...
                    mensajeVistoTrue(mensajes.get(position), "De"); //insertamos como true en la base de datos que la persona que envío el mensaje lo ha visto
                    mensajes.get(position).setVistoDe(true); //fijamos como true en el objeto mensaje que la persona que envío el mensaje lo ha visto
                }
                if(mensajes.get(position).isVistoDe()){
                    holder.visto.setText("Sí"); //fijamos en el TextView visto el valor del boolean del atributo vistoDe del objeto mensaje, en este caso si es true
                }else{
                    holder.visto.setText("No"); //fijamos en el TextView visto el valor del boolean del atributo vistoDe del objeto mensaje, en este caso si es false
                }
                AlertDialog alertDialog = dialogVerEnviado(mensajes.get(position)); //creamos el dialogo emergente para ver el mensaje envíado por el usuario de la sesión
                alertDialog.show(); //mostramos el dialogo
            });
            if(mensajes.get(position).isVistoDe()){
                holder.visto.setText("Sí"); //fijamos en el TextView visto el valor del boolean del atributo vistoDe del objeto mensaje, en este caso si es true
            }else{
                holder.visto.setText("No"); //fijamos en el TextView visto el valor del boolean del atributo vistoDe del objeto mensaje, en este caso si es true
            }
        }else{ //si el usuario de la sesión no coincide con el usuario que envía el mensaje quiere decir que es un mensaje recibido
            holder.accion.setText("ver"); //fijamos la acción en el TextView
            holder.accion.setOnClickListener(v -> { //en el TextView acción, al clicar, mostraremos a través de un dialogo emergente(AlertDialog) el mensaje recibido en cuestión y lo fijaremos como visto
                if(!mensajes.get(position).isVistoPara()){  //Si el mensaje no se había fijado como visto por la persona que lo recibe...
                    mensajeVistoTrue(mensajes.get(position), "Para"); //insertamos como true en la base de datos que la persona que recibió el mensaje lo ha visto
                    mensajes.get(position).setVistoPara(true); //fijamos como true en el objeto mensaje que la persona que recibió el mensaje lo ha visto
                }
                if(mensajes.get(position).isVistoPara()){
                    holder.visto.setText("Sí"); //fijamos en el TextView visto el valor del boolean del atributo vistoPara del objeto mensaje, en este caso si es true
                }else{
                    holder.visto.setText("No"); //fijamos en el TextView visto el valor del boolean del atributo vistoPara del objeto mensaje, en este caso si es true
                }
                AlertDialog alertDialog = dialogVerRecibido(mensajes.get(position)); //creamos el dialogo emergente para ver el mensaje recibido por el usuario de la sesión
                alertDialog.show(); //mostramos el dialogo
            });
            if(mensajes.get(position).isVistoPara()){
                holder.visto.setText("Sí"); //fijamos en el TextView visto el valor del boolean del atributo vistoPara del objeto mensaje, en este caso si es true
            }else{
                holder.visto.setText("No"); //fijamos en el TextView visto el valor del boolean del atributo vistoPara del objeto mensaje, en este caso si es true
            }
        }
    }

    @Override
    public int getItemCount() { //cantidad de elementos que habrá en la lista, determina su tamaño
        return mensajes.size(); //devolvemos el tamaño que hay en el listado de mensajes
    }

    public class MensajesHolder extends  RecyclerView.ViewHolder { //Clase que determina la referencia de los views del layout que se utilizarán para trabajar en los elementos o items del adaptador, hereda de Recycler.ViewHolder
        TextView fecha, hora, de, para, asunto, accion, visto;
        public MensajesHolder(@NonNull View itemView) {  //Constructor de MensajesHolder
            super(itemView);
            fecha = (TextView) itemView.findViewById(R.id.txtFechaContMens); //TextView que contendrá la fecha del mensaje
            hora = (TextView) itemView.findViewById(R.id.txtHoraContMens); //TextView que contendrá la hora del mensaje
            de = (TextView) itemView.findViewById(R.id.txtDeContMens); //TextView que contendrá el correo de la persona que envía el mensaje
            para = (TextView) itemView.findViewById(R.id.txtParaContMens); //TextView que contendrá el correo de la persona que recibe el mensaje
            asunto = (TextView) itemView.findViewById(R.id.txtAsuntoContMens); //TextView que contendrá el asunto del mensaje
            accion = (TextView) itemView.findViewById(R.id.txtAccionContMens); //TextView que contendrá la acción del mensaje
            visto = (TextView) itemView.findViewById(R.id.txtVistoContMens); //TextView que contendrá si el mensaje se ha visto o no
        }
    }

    public AlertDialog dialogVerEnviado(Mensaje mensaje) { //Devuelve un objeto AlertDialog para ver el mensaje envíado pasado por parámetro
        AlertDialog.Builder builder = new AlertDialog.Builder(context); //Con un elemento Builder podremos definir las partes de la creación de un objeto de clase AlertDialog, con context le indicamos donde debe mostrar el dialogo emergente
        builder.setTitle("Mensaje de: " + mensaje.getDe() + "\n" + "Para: " + mensaje.getPara() + "\n" + "Fecha y Hora: " + mensaje.getFecha() + ", " + mensaje.getHora()) //fijamos la información que contendrá el título del AlertDialog
                .setMessage(mensaje.getContenido()) //Fijamos en el mensaje del AlertDialog el contenido del mensaje
                .setPositiveButton("OK", (dialog, which) -> { //al pulsar ok no haremos nada, solo salir del AlertDialog
                        });
        return builder.create(); //devolvemos el AlertDialog creado
    }

    public AlertDialog dialogVerRecibido(Mensaje mensaje) { //Devuelve un objeto AlertDialog para ver el mensaje recibido pasado por parámetro
        AlertDialog.Builder builder = new AlertDialog.Builder(context); //Con un elemento Builder podremos definir las partes de la creación de un objeto de clase AlertDialog, con context le indicamos donde debe mostrar el dialogo emergente
        builder.setTitle("Mensaje de: " + mensaje.getDe() + "\n" + "Para: " + mensaje.getPara() + "\n" + "Fecha y Hora: " + mensaje.getFecha() + ", " + mensaje.getHora()) //fijamos la información que contendrá el título del AlertDialog
                .setMessage(mensaje.getContenido()) //Fijamos en el mensaje del AlertDialog el contenido del mensaje
                .setPositiveButton("Responder", (dialog, which) -> {  //al pulsar responder, haremos un intent hacia el activity ResponderMensaje
                            Intent intentResponder = new Intent(context, ResponderMensaje.class);
                            intentResponder.putExtra("mensaje", mensaje);
                            intentResponder.putExtra("usuario", usuarioIntent);
                            intentResponder.putExtra("CorreosSpinner", correoUsuarios);
                            intentResponder.putExtra("CentrosSpinner", centrosUsuarios);
                            context.startActivity(intentResponder);
                        })
                .setNegativeButton("Cancelar", (dialog, which) -> { //al pulsar cancelar no haremos nada, solo salir del AlertDialog
                        });
        return builder.create(); //devolvemos el AlertDialog creado
    }
    private void mensajeVistoTrue(Mensaje mensaje, String destino) { //método que establece para el mensaje pasado que el atributo vistoDe o vistoPara sea true
        MensajeService mensajeService = Apis.getMensajeService();
        Call<Void> call;
        if(destino=="De"){ //si el destino es para fijar como true el vistoDe
            call = mensajeService.mensajeVistoDe(mensaje); //hacemos una llamada a la Api para que en la base de datos en el mensaje pasado establezca en vistoDe true
        }else if (destino=="Para"){ //si el destino es para fijar como true el vistoPara
             call = mensajeService.mensajeVistoPara(mensaje); //hacemos una llamada a la Api para que en la base de datos en el mensaje pasado establezca en vistoPara true
        }
        else {
            return;
        }
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("MensajeAdapter", "Fallo de conexión al fijar el visto");
            }
        });
    }
}
