package com.example.timetowork;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class HorarioMesAdapter extends RecyclerView.Adapter<HorarioMesAdapter.HorarioViewHolder> {

    Context context;
    String month;

    int numMonth;
    int year;
    ArrayList<String> trabajadorHorario;

    public HorarioMesAdapter(Context context, String month, int numMonth, int year, ArrayList<String> trabajadorHorario) {
        this.context = context;
        this.month = month;
        this.year = year;
        this.trabajadorHorario = trabajadorHorario;
        this.numMonth=numMonth;
    }

    @NonNull
    @Override
    public HorarioMesAdapter.HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_listado_horariosmes, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        holder.txtDia.setText(String.valueOf(position + 1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.txtNombreDia.setText(String.valueOf(LocalDate.of(year,numMonth + 1, position + 1).getDayOfWeek()));
        }
    }

    @Override
    public int getItemCount() {

        return numDiasMes(month, year);
    }

    public class HorarioViewHolder extends RecyclerView.ViewHolder{

        TextView txtDia, txtNombreDia, txtTrabajador;
        public HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDia = (TextView) itemView.findViewById(R.id.txtNumDiaHorMes);
            txtNombreDia = (TextView) itemView.findViewById(R.id.txtNombreDiaHorMes);
            txtTrabajador = (TextView) itemView.findViewById(R.id.txtTrabajadorHorMes);
        }
    }
    public static int numDiasMes(String month, int year) {
        int numDias = 0;
        if(month.equals("Enero")||month.equals("Marzo")||month.equals("Mayo")||month.equals("Julio")||month.equals("Agosto")||month.equals("Octubre")||month.equals("Diciembre")){
            numDias = 31;
        } else if (month.equals("Abril")||month.equals("Junio")||month.equals("Septiembre")||month.equals("Noviembre")) {
           numDias = 30;
        } else if (month.equals("Febrero")) {
            if (isBisiesto(year)) {
                numDias = 29;
            } else {
                numDias = 28;
            }
        }
        return numDias;
    }
    public static boolean isBisiesto(int year) {

        GregorianCalendar calendar = new GregorianCalendar();
        boolean isBisiesto = false;
        if (calendar.isLeapYear(year)) {
            isBisiesto = true;
        }
        return isBisiesto;

    }

}
