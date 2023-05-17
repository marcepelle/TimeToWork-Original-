package com.example.timetowork.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timetowork.activities.perfil.GestionUsuario;
import com.example.timetowork.R;
import com.example.timetowork.models.Usuario;

import java.util.ArrayList;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> { //Está clase que hereda de RecyclerView.Adapter creará el modelo de datos para el listado de usuarios
    Context context; //Contexto del layout donde se insertará el adaptador
    ArrayList<Usuario> listaUsuario; //Listado de usuarios
    Usuario usuarioIntent; //usuario de la sesión
    public UsuarioAdapter(Context context, ArrayList<Usuario> listaUsuario, Usuario usuario) { //Constructor de UsuarioAdapter
        this.listaUsuario = listaUsuario;
        for(int i =0; i<this.listaUsuario.size();i++){
            if(this.listaUsuario.get(i).isEsAdmin()){
                this.listaUsuario.remove(i); //quitamos al administrador de la lista de usuarios
            }
        }
        this.usuarioIntent=usuario;
        this.context = context;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Este metódo será llamado cada vez que se requiera crear un item o elemento del listado, creará la vista del item
        View view = LayoutInflater.from(context).inflate(R.layout.content_listado_usuarios, parent, false); //inflamos el layout del item, pasandole por parámetro el layout base de creación para la vista, parent que es el ViewGroup en el que se agregará la nueva Vista e indicamos como false que adjunte instantaneamente la vista en el parent para evitar un IllegalStateException
        return  new UsuarioViewHolder(view); //devolvemos el UsuarioViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) { //Este método va a vincular los datos al ViewHolder para una posición dada en el listado del RecyclerView
        holder.txtNombre.append(" " + listaUsuario.get(position).getNombreUsuario()); //Fijamos el nombre del usuario de la posición en el listado en el TextView
        holder.txtApellidos.append(" " + listaUsuario.get(position).getApellidosUsuario()); //Fijamos los apellidos del usuario de la posición en el listado en el TextView
        holder.txtLugartrabajo.append(" " + listaUsuario.get(position).getLugarTrabajo()); //Fijamos los apellidos del usuario de la posición en el listado en el TextView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.itemView.setTooltipText("Click para ver Empleado"); //Fijamos para el elemento del listado un toolTip
        }
        holder.itemView.setOnClickListener(v -> { //Al hacer clic en el elemento del listado hacemos un intent hacia el activity GestionUsuario
            Intent intentGestUs = new Intent(context, GestionUsuario.class);
            intentGestUs.putExtra("usuario", usuarioIntent);
            intentGestUs.putExtra("usuarioGestionado", listaUsuario.get(position));
            context.startActivity(intentGestUs);
        });

    }

    @Override
    public int getItemCount() { //cantidad de elementos que habrá en la lista, determina su tamaño
        return listaUsuario.size();  //devolvemos el tamaño que hay en el listado de usuarios
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder { //Clase que determina la referencia de los views del layout que se utilizarán para trabajar en los elementos o items del adaptador, hereda de Recycler.ViewHolder
        TextView txtNombre, txtApellidos, txtLugartrabajo;
        public UsuarioViewHolder(@NonNull View itemView) { //Constructor de UsuarioViewHolder
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.nombreUser); //TextView que contendrá el nombre del usuario
            txtApellidos = (TextView) itemView.findViewById(R.id.apellidosUser); //TextView que contendrá los apellidos del usuario
            txtLugartrabajo = (TextView) itemView.findViewById(R.id.lugarTrabajo); //TextView que contendrá el centro de trabajo del usuario
        }
    }
}
