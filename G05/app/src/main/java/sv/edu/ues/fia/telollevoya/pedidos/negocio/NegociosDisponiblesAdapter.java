package sv.edu.ues.fia.telollevoya.pedidos.negocio;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.negocio.negocio.Restaurant;

public class NegociosDisponiblesAdapter extends RecyclerView.Adapter<NegociosDisponiblesAdapter.NegociosViewHolder> {
    private ArrayList<Restaurant> listaNegocios;

    public NegociosDisponiblesAdapter(ArrayList<Restaurant> listaRestaurantes) {
        this.listaNegocios = listaRestaurantes;
    }

    @NonNull
    @Override
    public NegociosDisponiblesAdapter.NegociosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_negocio, null, false);
        return new NegociosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NegociosDisponiblesAdapter.NegociosViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        holder.txtNegocioNombre.setText(context.getString(R.string.negocio_nombre) + " " + listaNegocios.get(position).getNombre());
        holder.txtNegocioId.setText(context.getString(R.string.negocio_id) + " " + listaNegocios.get(position).getIdNegocio());
        holder.txtNegocioUbicacionId.setText(context.getString(R.string.negocio_ubicacion_id) + " " + listaNegocios.get(position).getIdUbicacion());
        holder.txtNegocioAdministradorId.setText(context.getString(R.string.negocio_administrador_id) + " " + listaNegocios.get(position).getIdAdministrador());
        holder.txtNegocioTelefono.setText(context.getString(R.string.negocio_telefono) + " " + listaNegocios.get(position).getTelefono());
        holder.txtNegocioApertura.setText(context.getString(R.string.negocio_apertura) + " " + listaNegocios.get(position).getHorarioApertura());
        holder.txtNegocioCierre.setText(context.getString(R.string.negocio_cierre) + " " + listaNegocios.get(position).getHorarioCierre());
        holder.txtNegocioDireccion.setText(context.getString(R.string.negocio_direccion) + " " + listaNegocios.get(position).getDescripcionUbicacion());
    }


    @Override
    public int getItemCount() {
        return listaNegocios.size();
    }

    public void setNegocios(ArrayList<Restaurant> nuevosNegocios) {
        listaNegocios = nuevosNegocios;
        notifyDataSetChanged();
    }

    public class NegociosViewHolder extends RecyclerView.ViewHolder {
        TextView txtNegocioNombre, txtNegocioId, txtNegocioUbicacionId, txtNegocioAdministradorId,
                txtNegocioTelefono, txtNegocioApertura, txtNegocioCierre, txtNegocioDireccion;

        public NegociosViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNegocioNombre = itemView.findViewById(R.id.txtNegocioNombre);
            txtNegocioId = itemView.findViewById(R.id.txtNegocioId);
            txtNegocioUbicacionId = itemView.findViewById(R.id.txtNegocioUbicacionId);
            txtNegocioAdministradorId = itemView.findViewById(R.id.txtNegocioAdministradorId);
            txtNegocioTelefono = itemView.findViewById(R.id.txtNegocioTelefono);
            txtNegocioApertura = itemView.findViewById(R.id.txtNegocioApertura);
            txtNegocioCierre = itemView.findViewById(R.id.txtNegocioCierre);
            txtNegocioDireccion = itemView.findViewById(R.id.txtNegocioDireccion);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, NegociosOpcionesActivity.class);
                    int idNegocio = listaNegocios.get(getAdapterPosition()).getIdNegocio();
                    String name = listaNegocios.get(getAdapterPosition()).getNombre();
                    int idUbicacion = listaNegocios.get(getAdapterPosition()).getIdUbicacion();
                    int idAdmin = listaNegocios.get(getAdapterPosition()).getIdAdministrador();
                    intent.putExtra("idUbicacion", idUbicacion);
                    intent.putExtra("idNegocio", idNegocio);
                    intent.putExtra("idAdministrador", idAdmin);
                    intent.putExtra("name", name);

                    context.startActivity(intent);
                }
            });
        }
    }
}
