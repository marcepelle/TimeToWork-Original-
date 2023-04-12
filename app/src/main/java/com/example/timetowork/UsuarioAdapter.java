package com.example.timetowork;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    ArrayList<Usuario> listaUsuario;


    public UsuarioAdapter(ArrayList<Usuario> listaUsuario) {
        this.listaUsuario = listaUsuario;

    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //indicara el modelo que tendrá que tener en cuenta para crear la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_gestion_usuarios, parent, false);
        return  new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.UsuarioViewHolder holder, int position) { // adjudicara a cada view su contenido
        Log.d("Adapter", "onBindViewHolder: " + listaUsuario.get(position).getNombreUsuario());
        holder.txtNombre.setText(listaUsuario.get(position).getNombreUsuario());
        holder.txtApellidos.setText(listaUsuario.get(position).getApellidosUsuario());
        holder.txtLugartrabajo.setText(listaUsuario.get(position).getLugarTrabajo());
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
