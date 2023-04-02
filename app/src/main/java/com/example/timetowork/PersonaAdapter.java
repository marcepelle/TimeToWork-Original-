package com.example.timetowork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timetowork.models.Persona;

import org.w3c.dom.Text;

import java.util.List;

public class PersonaAdapter extends ArrayAdapter<Persona> {


    private Context context;
    private List<Persona> personas;

    public PersonaAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Persona> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context=context;
        this.personas=objects;

    }

    public PersonaAdapter(@NonNull Context context, int resource,@NonNull List<Persona> objects) {
        super(context, resource, objects);
        this.context=context;
        this.personas=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Crea una instancia de un archivo XML de diseño en sus objetos de vista correspondientes
        View rowView=layoutInflater.inflate(R.layout.content_main, parent, false); //creará las filas del view correspondiente

        TextView txtidPersona=(TextView) rowView.findViewById(R.id.IdPersona);
        TextView txtNombre=(TextView) rowView.findViewById(R.id.Nombre);
        TextView txtApellidos=(TextView) rowView.findViewById(R.id.Apellidos);

        //asignamos los datos a los textview
        txtidPersona.setText(String.format("ID:%s", personas.get(position).getId()));
        txtNombre.setText(String.format("Nombre:%s", personas.get(position).getNombres()));
        txtApellidos.setText(String.format("Apellido:%s", personas.get(position).getApellidos()));

        return rowView; //devolvemos todas las filas
    }
}
