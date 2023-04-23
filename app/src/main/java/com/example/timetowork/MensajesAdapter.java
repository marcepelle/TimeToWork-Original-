package com.example.timetowork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timetowork.models.Mensaje;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

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
                AlertDialog alertDialog = dialogVerEnviado(mensajes.get(position).getContenido(),mensajes.get(position).getDe().toString());
                alertDialog.show();
            });
        }else{
            holder.accion.setText("ver");
            holder.accion.setOnClickListener(v -> {
                AlertDialog alertDialog = dialogVerRecibido(mensajes.get(position).getContenido(),mensajes.get(position).getDe().toString(), position);
                alertDialog.show();
            });
        }
        holder.visto.setText(String.valueOf(mensajes.get(position).isVisto()));

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

    public AlertDialog dialogVerEnviado(String mensaje, String de) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Mensaje de: " + de)
                .setMessage(mensaje)
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

    public AlertDialog dialogVerRecibido(String mensaje, String de, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Mensaje de: " + de)
                .setMessage(mensaje)
                .setPositiveButton("Responder",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentResponder = new Intent(context, ResponderMensaje.class);
                                intentResponder.putExtra("mensaje", mensajes.get(position));
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
}
