package sv.edu.ues.fia.telollevoya.negocio.producto;

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
import android.util.Base64;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class ListaProductosAdaptader extends RecyclerView.Adapter<ListaProductosAdaptader.ProductosViewHolder> {
    ArrayList<Product> listaProductos;

    public ListaProductosAdaptader(ArrayList<Product> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ListaProductosAdaptader.ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemproducto, null, false);
        return new ProductosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaProductosAdaptader.ProductosViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Product producto = listaProductos.get(position);

        holder.txtProductoID.setText(context.getString(R.string.producto_id) + " " + producto.getIdProducto());
        holder.txtNegocioID.setText(context.getString(R.string.negocio_id) + " " + producto.getIdNegocio());
        holder.txtNombre.setText(context.getString(R.string.producto_nombre) + " " + producto.getNombreProducto());
        holder.txtPrecio.setText(context.getString(R.string.producto_precio) + " " + String.valueOf(producto.getPrecioProducto()));
        holder.txtDescripcion.setText(context.getString(R.string.producto_descripcion) + " " + producto.getDescripcionProducto());
        holder.txtTipo.setText(context.getString(R.string.producto_tipo) + " " + producto.getTipoProducto());

        String existenciaString = producto.isExistenciaProducto() ?
                context.getString(R.string.producto_disponible) :
                context.getString(R.string.producto_no_disponible);
        holder.txtExistencias.setText(context.getString(R.string.producto_existencias) + " " + existenciaString);

        // Decodificar la imagen base64 y cargarla con Glide
        String imagenBase64 = producto.getImagenProducto();
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            byte[] imageByteArray = Base64.decode(imagenBase64, Base64.DEFAULT);
            Glide.with(context)
                    .asBitmap()
                    .load(imageByteArray)
                    .into(holder.imgProducto);
        } else {
            // Manejar el caso cuando la imagen es null, por ejemplo, establecer una imagen predeterminada
            holder.imgProducto.setImageResource(R.drawable.logo_general);  // Asegúrate de tener esta imagen en tus recursos
        }
    }



    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public void setProductos(ArrayList<Product> productos) {
        this.listaProductos = productos;
        notifyDataSetChanged();
    }

    public class ProductosViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductoID, txtNegocioID, txtNombre, txtPrecio, txtDescripcion, txtTipo, txtExistencias;
        ImageView imgProducto; // Nueva referencia para la imagen

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductoID = itemView.findViewById(R.id.txtProductoId);
            txtNegocioID = itemView.findViewById(R.id.txtProductoNegocioId);
            txtNombre = itemView.findViewById(R.id.txtProductoNombre);
            txtPrecio = itemView.findViewById(R.id.txtProductoPrecio);
            txtDescripcion = itemView.findViewById(R.id.txtProductoDescripcion);
            txtTipo = itemView.findViewById(R.id.txtProductoTipo);
            txtExistencias = itemView.findViewById(R.id.txtProductoExistencias);
            imgProducto = itemView.findViewById(R.id.imgProducto); // Nueva referencia para la imagen

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Context context = v.getContext();
                    Intent intent = new Intent(context, EditarProductoActivity.class);
                    intent.putExtra("idProducto", listaProductos.get(getAdapterPosition()).getIdProducto());
                    context.startActivity(intent);*/
                }
            });
        }
    }
}
