package com.example.timetowork.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timetowork.R;
import com.example.timetowork.models.Horario;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class HorarioMesAdapter extends RecyclerView.Adapter<HorarioMesAdapter.HorarioViewHolder> { //Está clase que hereda de RecyclerView.Adapter creará el modelo de datos para el listado de horarios

    Context context; //Contexto del layout donde se insertará el adaptador
    String month; //Mes del horario en forma de texto

    int numMonth; //Mes del horario en forma de entero
    int year; //Año del horario en forma de entero
    ArrayList<Horario> trabajadorHorario = new ArrayList<Horario>(); //Horarios del trabajador
    public HorarioMesAdapter(Context context, String month, int numMonth, int year, ArrayList<Horario> trabajadorHorario) { //Constructor de HorarioMesAdapter
        this.context = context;
        this.month = month;
        this.year = year;
        this.trabajadorHorario.addAll(trabajadorHorario);
        this.numMonth=numMonth;
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Este metódo será llamado cada vez que se requiera crear un item o elemento del listado, creará la vista del item
        View view = LayoutInflater.from(context).inflate(R.layout.content_listado_horariosmes, parent, false); //inflamos el layout del item, pasandole por parámetro el layout base de creación para la vista, parent que es el ViewGroup en el que se agregará la nueva Vista e indicamos como false que adjunte instantaneamente la vista en el parent para evitar un IllegalStateException
        return new HorarioViewHolder(view); //devolvemos el HorarioViewHolder
    }


    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) { //Este método va a vincular los datos al ViewHolder para una posición dada en el listado del RecyclerView
        holder.txtDia.setText(String.valueOf(position + 1 ) + "-" + month); //fijamos el número de día del mes y el mes en el TextView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.txtNombreDia.setText(String.valueOf(LocalDate.of(year,numMonth + 1, position + 1).getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "ES")))); //fijamos en el Textview el nombre del día de la semana
            for (int i=0; i<trabajadorHorario.size();i++){ //recorremos el listado de horarios del trabajador
                if(LocalDate.parse(trabajadorHorario.get(i).getFecha()).equals(LocalDate.of(year,numMonth + 1, position + 1))){ //si la fecha del horario iterado coincide con la fecha de la posición del item en el listado del RecyclerView...
                    holder.txtTrabajador.setText(" Empleado: " + trabajadorHorario.get(i).getEmpleado() + " Horario("+ trabajadorHorario.get(i).getHoraEntrada() + " - " + trabajadorHorario.get(i).getHoraSalida() + ")."); //Rellenamos la información del horario en el TextView, nombre y apellidos del trabajador y horario de entrada y salida
                    return ;
                }
            }
            holder.txtTrabajador.setText(""); //si no hay horario para la fecha de la posición del elemento en el listado, dejamos el TextView vacío
        }
    }

    @Override
    public int getItemCount() { //cantidad de elementos que habrá en la lista, determina su tamaño
        return numDiasMes(month, year); //obtenemos el número de días que hay para el mes y año pasado
    }

    public class HorarioViewHolder extends RecyclerView.ViewHolder{ //Clase que determina la referencia de los views del layout que se utilizarán para trabajar en los elementos o items del adaptador, hereda de Recycler.ViewHolder

        TextView txtDia, txtNombreDia, txtTrabajador;
        public HorarioViewHolder(@NonNull View itemView) { //Constructor de HorarioViewHolder
            super(itemView);
            txtDia = (TextView) itemView.findViewById(R.id.txtNumDiaHorMes); //TextView que contendrá el número de día del horario y el mes
            txtNombreDia = (TextView) itemView.findViewById(R.id.txtNombreDiaHorMes); //TextView que contendrá el nombre del día de la semana
            txtTrabajador = (TextView) itemView.findViewById(R.id.txtTrabajadorHorMes); //TextView que contendrá la información del horario del trabajador
        }

    }
    public static int numDiasMes(String month, int year) { //Devuelve el número entero de días que hay para el mes y año pasado
        int numDias = 0;
        if(month.equals("Enero")||month.equals("Marzo")||month.equals("Mayo")||month.equals("Julio")||month.equals("Agosto")||month.equals("Octubre")||month.equals("Diciembre")){ //si el mes pasado es alguno de los que se comprueba en el if será 31
            numDias = 31;
        } else if (month.equals("Abril")||month.equals("Junio")||month.equals("Septiembre")||month.equals("Noviembre")) { //si el mes pasado es alguno de los que se comprueba en el else if será 30
           numDias = 30;
        } else if (month.equals("Febrero")) { //si el mes pasado es febrero será...
            if (isBisiesto(year)) {
                numDias = 29; //29 días si el año pasado es bisiesto
            } else {
                numDias = 28; //28 días si el año pasado no es bisiesto
            }
        }
        return numDias; //devolvemos el número de días
    }
    public static boolean isBisiesto(int year) { //comprobamos sí el año pasado es bisiesto devolviendo un boolean, true si es bisiesto o false si no lo es

        GregorianCalendar calendar = new GregorianCalendar(); //Obtenemos un objeto GregorianCalendar para hacer la comprobación
        boolean isBisiesto = false; //por defecto será false
        if (calendar.isLeapYear(year)) { //comprobamos con el método isLeapYear sí es bisiesto, sí lo es devolvemos true
            isBisiesto = true;
        }
        return isBisiesto; //devolvemos el boolean

    }
}
