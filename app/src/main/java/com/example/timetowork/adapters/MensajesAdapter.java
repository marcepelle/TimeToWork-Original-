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

public class MensajesAdapter extends RecyclerView.Adapter <MensajesAdapter.MensajesHolder> {

    Context context;
    ArrayList<Mensaje> mensajes;
    Usuario usuarioIntent;

    ArrayList<ArrayList<String>> correoUsuarios;

    String[] centrosUsuarios;

    public MensajesAdapter(Context context, ArrayList<Mensaje> mensajes, Usuario usuarioIntent, ArrayList<ArrayList<String>> correoUsuarios, String[] centrosUsuarios) {
        this.context = context;
        this.mensajes = mensajes;
        this.usuarioIntent = usuarioIntent;
        this.correoUsuarios = correoUsuarios;
        this.centrosUsuarios = centrosUsuarios;
    }

    @NonNull
    @Override
    public MensajesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_listado_mensajes, parent, false);
        return new MensajesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajesAdapter.MensajesHolder holder, int position) {
        holder.fecha.setText(mensajes.get(position).getFecha());
        holder.de.setText(mensajes.get(position).getDe());
        holder.para.setText(mensajes.get(position).getPara());
        holder.asunto.setText(mensajes.get(position).getAsunto());
        if(usuarioIntent.getCorreoUsuario().equals(mensajes.get(position).getDe())){
            holder.accion.setText("ver");
            holder.accion.setOnClickListener(v -> {
                if(!mensajes.get(position).isVistoDe()){
                    mensajeVistoTrue(mensajes.get(position), "De");
                    mensajes.get(position).setVistoDe(true);
                }
                holder.visto.setText(String.valueOf(mensajes.get(position).isVistoDe()));
                AlertDialog alertDialog = dialogVerEnviado(mensajes.get(position));
                alertDialog.show();
            });
            holder.visto.setText(String.valueOf(mensajes.get(position).isVistoDe()));
        }else{
            holder.accion.setText("ver");
            holder.accion.setOnClickListener(v -> {
                if(!mensajes.get(position).isVistoPara()){
                    mensajeVistoTrue(mensajes.get(position), "Para");
                    mensajes.get(position).setVistoPara(true);
                }
                holder.visto.setText(String.valueOf(mensajes.get(position).isVistoPara()));
                AlertDialog alertDialog = dialogVerRecibido(mensajes.get(position));
                alertDialog.show();
            });
            holder.visto.setText(String.valueOf(mensajes.get(position).isVistoPara()));
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public class MensajesHolder extends  RecyclerView.ViewHolder {
        TextView fecha, de, para, asunto, accion, visto;
        public MensajesHolder(@NonNull View itemView) {
            super(itemView);
            fecha = (TextView) itemView.findViewById(R.id.txtFechaContMens);
            de = (TextView) itemView.findViewById(R.id.txtDeContMens);
            para = (TextView) itemView.findViewById(R.id.txtParaContMens);
            asunto = (TextView) itemView.findViewById(R.id.txtAsuntoContMens);
            accion = (TextView) itemView.findViewById(R.id.txtAccionContMens);
            visto = (TextView) itemView.findViewById(R.id.txtVistoContMens);

        }
    }

    public AlertDialog dialogVerEnviado(Mensaje mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Mensaje de: " + mensaje.getDe() + "\n" + "Para: " + mensaje.getPara() + "\n" + "Fecha y Hora: " + mensaje.getFecha() + ", " + mensaje.getHora())
                .setMessage(mensaje.getContenido())
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogVerRecibido(Mensaje mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Mensaje de: " + mensaje.getDe() + "\n" + "Para: " + mensaje.getPara() + "\n" + "Fecha y Hora: " + mensaje.getFecha() + ", " + mensaje.getHora())
                .setMessage(mensaje.getContenido())
                .setPositiveButton("Responder",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentResponder = new Intent(context, ResponderMensaje.class);
                                intentResponder.putExtra("mensaje", mensaje);
                                intentResponder.putExtra("usuario", usuarioIntent);
                                intentResponder.putExtra("CorreosSpinner", correoUsuarios);
                                intentResponder.putExtra("CentrosSpinner", centrosUsuarios);
                                context.startActivity(intentResponder);
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }
    private void mensajeVistoTrue(Mensaje mensaje, String destino) {
        MensajeService mensajeService = Apis.getMensajeService();
        Call<Void> call = null;
        if(destino=="De"){
            call = mensajeService.mensajeVistoDe(mensaje);
        }else if (destino=="Para"){
             call = mensajeService.mensajeVistoPara(mensaje);
        }
        else {
            return;
        }
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("mensajeAdapter", "Mesnaje visto: "  + mensaje.getIdMensaje());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("MensajeAdapter", "Fallo de conexión");
            }
        });
    }
}
