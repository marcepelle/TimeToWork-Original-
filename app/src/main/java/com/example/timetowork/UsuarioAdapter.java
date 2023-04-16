package com.example.timetowork;


import static android.view.MotionEvent.ACTION_HOVER_ENTER;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timetowork.models.Usuario;

import java.util.ArrayList;
import java.util.Iterator;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    Context context;
    ArrayList<Usuario> listaUsuario;


    public UsuarioAdapter(Context context, ArrayList<Usuario> listaUsuario) {
        this.listaUsuario = listaUsuario;
        for(int i =0; i<this.listaUsuario.size();i++){
            if(this.listaUsuario.get(i).isEsAdmin()){
                this.listaUsuario.remove(i);
            }
        }
        this.context = context;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //indicara el modelo que tendrá que tener en cuenta para crear la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_listado_usuarios, parent, false);
        return  new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) { // adjudicara a cada view su contenido
        Log.d("Adapter", "onBindViewHolder: " + listaUsuario.get(position).getNombreUsuario());
        holder.txtNombre.append(" " + listaUsuario.get(position).getNombreUsuario());
        holder.txtApellidos.append(" " + listaUsuario.get(position).getApellidosUsuario());
        holder.txtLugartrabajo.append(" " + listaUsuario.get(position).getLugarTrabajo());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.itemView.setTooltipText("Click para ver Empleado");
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intentGestUs = new Intent(context, GestionUsuario.class);
            intentGestUs.putExtra("usuario", listaUsuario.get(0));
            intentGestUs.putExtra("usuarioGestionado", listaUsuario.get(position));
            context.startActivity(intentGestUs);
        });

    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtApellidos, txtLugartrabajo;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.nombreUser);
            txtApellidos = (TextView) itemView.findViewById(R.id.apellidosUser);
            txtLugartrabajo = (TextView) itemView.findViewById(R.id.lugarTrabajo);
        }
    }
}
