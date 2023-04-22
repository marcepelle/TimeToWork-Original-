package com.example.timetowork;

import android.content.Context;
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

    public MensajesAdapter(Context context, ArrayList<Mensaje> mensajes, Usuario usuarioIntent) {
        this.context = context;
        this.mensajes = mensajes;
        this.usuarioIntent = usuarioIntent;
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
        holder.accion.setText("ver");
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
            accion = (TextView) itemView.findViewById(R.id.txtVistoContMens);
            visto = (TextView) itemView.findViewById(R.id.txtVistoContMens);

        }
    }
}
