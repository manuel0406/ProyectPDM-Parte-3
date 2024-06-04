package sv.edu.ues.fia.telollevoya.pago;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sv.edu.ues.fia.telollevoya.DetallePedido;
import sv.edu.ues.fia.telollevoya.R;

public class AdaptadorProductosAPagar extends RecyclerView.Adapter<AdaptadorProductosAPagar.ProductoViewHolder> {

    private List<DetallePedido> detallesPedido;

    public AdaptadorProductosAPagar(List<DetallePedido> detallesPedido) {
        this.detallesPedido = detallesPedido;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vistaProducto = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_pago_card_custom, parent, false);
        return new ProductoViewHolder(vistaProducto);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        DetallePedido detallePedido = detallesPedido.get(position);
        holder.cantidadProducto.setText(String.valueOf(detallePedido.getCantidad()));
        holder.nombreProducto.setText(detallePedido.getProducto().getNombre());
        holder.subtotalProducto.setText("$" + String.format("%.2f", detallePedido.getSubTotal()));
    }

    @Override
    public int getItemCount() {
        return detallesPedido.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView cantidadProducto;
        TextView nombreProducto;
        TextView subtotalProducto;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            cantidadProducto = itemView.findViewById(R.id.cantidadProducto);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            subtotalProducto = itemView.findViewById(R.id.subtotalProducto);
        }
    }
}
