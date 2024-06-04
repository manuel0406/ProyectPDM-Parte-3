package sv.edu.ues.fia.telollevoya.pedidos.cliente;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sv.edu.ues.fia.telollevoya.Cliente;
import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.Departamento;
import sv.edu.ues.fia.telollevoya.DetallePedido;
import sv.edu.ues.fia.telollevoya.Distrito;
import sv.edu.ues.fia.telollevoya.Municipio;
import sv.edu.ues.fia.telollevoya.Pedido;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Ubicacion;
import sv.edu.ues.fia.telollevoya.pago.SeleccionPagoActivity;

public class CrearPedidoActivity extends AppCompatActivity {

    private ListView detallesListView;
    private TextView totPagarTextView;
    private float totPagar;
    private ArrayList<Integer> detallesEliminar;
    ArrayList<DetallePedido> detallePedidosList;
    DetalleProductoCardAdapter adapter;
    private ControlBD controlBD;
    int[] idLugares = new int[3];
    String[] nombresLugares = new String[3];
    int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_pedido);

        detallesListView = findViewById(R.id.detalles_listView);
        totPagarTextView = findViewById(R.id.tot_pagar_textView);
        controlBD = new ControlBD(CrearPedidoActivity.this);
        controlBD.abrir();
        idCliente = controlBD.consultaUsuario();
        controlBD.cerrar();
        Bundle objetoRecibido = getIntent().getExtras();
        if (objetoRecibido != null | !objetoRecibido.isEmpty()) {
            detallePedidosList = (ArrayList) objetoRecibido.getSerializable("detalles");
            adapter = new DetalleProductoCardAdapter(CrearPedidoActivity.this, 0, detallePedidosList);
            detallesListView.setAdapter(adapter);
            modificarTotAPagar();
            detallesEliminar = new ArrayList<>();
        } else {
            TextView mensaje = new TextView(CrearPedidoActivity.this);
            mensaje.setText("Nada por ordenar :0");
            mensaje.setTextSize(28);
            mensaje.setGravity(Gravity.CENTER);
            detallesListView.addView(mensaje);
        }
    }

    public void modificarCantidadProducto(int posicion, int cantidad, float subtotal){
        DetallePedido dp = detallePedidosList.get(posicion);
        //Validando la cantidad de pupusas
        if(dp.getProducto().getNombre().toLowerCase().contains("pupusa")){
            if(dp.getCantidad() <= 19){
                detallePedidosList.get(posicion).setCantidad(cantidad);
                detallePedidosList.get(posicion).setSubTotal(subtotal);
                modificarTotAPagar();
            }
        }else{
            detallePedidosList.get(posicion).setCantidad(cantidad);
            detallePedidosList.get(posicion).setSubTotal(subtotal);
            modificarTotAPagar();
        }
    }

    public void modificarTotAPagar(){
        totPagar = 0;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        detallePedidosList.forEach(dp ->{
            totPagar = totPagar + dp.getSubTotal();
        });
        totPagarTextView.setText("$" + df.format(totPagar));
    }

    public void eliminarDetalle(int posicion){
        adapter.remove(detallePedidosList.get(posicion));
        modificarTotAPagar();
        detallesEliminar.add(posicion);
        Bundle extras = new Bundle();
        extras.putSerializable("positions", detallesEliminar);//posiciones en la lista detalles a eliminar
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.putExtra("codigo", 1);//codigo de eliminar detalle del carrito
        setResult(RESULT_OK, intent);
    }

    public void crearPedido(View view){
        if(!detallePedidosList.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(CrearPedidoActivity.this);
            builder.setTitle("Escoge Destino de tu pedido");

            ////////////////////
            controlBD.abrir();
            ArrayList<Departamento> departamentos = controlBD.getDepartamentos();
            String[] nombresDepart = new String[departamentos.size()];
            for(int i = 0; i<departamentos.size(); i++){
                nombresDepart[i] = departamentos.get(i).getNombreDepartamento();
            }
            builder.setItems(nombresDepart, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    idLugares[0] = departamentos.get(which).getIdDepartamento();
                    nombresLugares[0] = departamentos.get(which).getNombreDepartamento();
                    ingresarMunicipio(idLugares[0]);
                }
            });
            builder.show();
        }
    }

    public void ingresarMunicipio(int idDepto){
        AlertDialog.Builder builder = new AlertDialog.Builder(CrearPedidoActivity.this);
        builder.setTitle("Escoge tu municipio de tu pedido");

        ////////////////////
        ArrayList<Municipio> municipios = controlBD.getMunicipioPorDepto(idDepto);
        String[] nombresMuni = new String[municipios.size()];
        for(int i = 0; i<municipios.size(); i++){
            nombresMuni[i] = municipios.get(i).getNombreMunicipio();
        }
        builder.setItems(nombresMuni, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                idLugares[1] = municipios.get(which).getIdMunicipio();
                nombresLugares[1] = municipios.get(which).getNombreMunicipio();
                ingresarDistrito(idLugares[1]);
            }
        });
        builder.show();
    }

    public void ingresarDistrito(int idMunicipio){
        AlertDialog.Builder builder = new AlertDialog.Builder(CrearPedidoActivity.this);
        builder.setTitle("Escoge tu Distrito");

        ////////////////////
        ArrayList<Distrito> distritos = controlBD.getDistritoPorMunicipio(idMunicipio);
        String[] nombresDist = new String[distritos.size()];
        for(int i = 0; i<distritos.size(); i++){
            nombresDist[i] = distritos.get(i).getNombreDistrito();
        }
        builder.setItems(nombresDist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                idLugares[2] = distritos.get(which).getIdDistrito();
                nombresLugares[2] = distritos.get(which).getNombreDistrito();
                controlBD.cerrar();

                //LLenado de la Ubicación dada
                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(idLugares[0]);
                Municipio municipio = new Municipio();
                municipio.setIdMunicipio(idLugares[1]);
                Distrito distrito = new Distrito();
                distrito.setIdDistrito(idLugares[2]);
                Ubicacion ubicacion = new Ubicacion();
                ubicacion.setDistrito(distrito);
                ubicacion.setDescripcion(nombresLugares[2] + ", " + nombresLugares[1] + ", " + nombresLugares[0]);

                Cliente cliente = new Cliente();
                cliente.setIdCliente(Integer.toString(idCliente));
                Pedido pedido = new Pedido();
                String descripcion = "";
                float totPagar = 0;
                for(int i = 0; i < detallePedidosList.size(); i++){
                    descripcion = descripcion.concat(detallePedidosList.get(i).getProducto().getNombre() + "," +
                            " ");
                    totPagar += detallePedidosList.get(i).getSubTotal();

                }
                pedido.setTotalAPagar(totPagar);
                pedido.setDescripcionOrden(descripcion);
                pedido.setDetallePedidoList(detallePedidosList);
                pedido.setUbicacion(ubicacion);
                pedido.setCliente(cliente);
                pedido.setFechaPedido(new Date());
                pedido.setCostoEnvio(1.0f); //un dolar por el momento

                //Llamada a la pantalla de Metodos de pago
                Log.v("TOTAL PREVIO> ", ""+ totPagar);
                Intent intent = new Intent(CrearPedidoActivity.this, SeleccionPagoActivity.class);
                Bundle extra = new Bundle();
                extra.putSerializable("pedido",pedido);
                intent.putExtras(extra);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    public void backButton(View v){
        finish();
    }

    public class DetalleProductoCardAdapter extends ArrayAdapter<DetallePedido>{
        List<DetallePedido> detalles;
        public DetalleProductoCardAdapter(@NonNull Context context, int resource, @NonNull List<DetallePedido> objects) {
            super(context, resource, objects);
            detalles = objects;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.detalle_card_custom, parent, false);
            TextView nomProductoTextView = convertView.findViewById(R.id.nomProd_textView);
            TextView precioProductoTextView = convertView.findViewById(R.id.precio_textView);
            ImageView productoImageView = convertView.findViewById(R.id.prod_imgView);
            ImageButton minusBtn = convertView.findViewById(R.id.imgMinus);
            ImageButton plusBtn = convertView.findViewById(R.id.imgPlus);
            TextView cantProductoTextView = convertView.findViewById(R.id.txtNumbers);
            ImageButton eliminarBtn = convertView.findViewById(R.id.eliminar_btn);

            DetallePedido dp = detalles.get(position);
            nomProductoTextView.setText(dp.getProducto().getNombre());
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            precioProductoTextView.setText(df.format(dp.getProducto().getPrecio()));
            cantProductoTextView.setText(Integer.toString(dp.getCantidad()));
            minusBtn.setTag(detalles.indexOf(dp));
            plusBtn.setTag(detalles.indexOf(dp));
            eliminarBtn.setTag(detalles.indexOf(dp));
            minusBtn.setOnClickListener(v ->{
                int cant = Integer.parseInt(cantProductoTextView.getText().toString());
                cant = cant > 1? cant - 1: 1;
                float subtotal = dp.getProducto().getPrecio() * cant;
                cantProductoTextView.setText(df.format(cant));
                precioProductoTextView.setText(df.format(subtotal));
                modificarCantidadProducto((int)v.getTag(), cant, subtotal);
            });
            plusBtn.setOnClickListener(v ->{
                int cant = Integer.parseInt(cantProductoTextView.getText().toString());
                cant++;
                float subtotal = dp.getProducto().getPrecio() * cant;
                cantProductoTextView.setText(df.format(cant));
                precioProductoTextView.setText(df.format(subtotal));
                modificarCantidadProducto((int)v.getTag(), cant, subtotal);
            });
            eliminarBtn.setOnClickListener(v ->{
                AlertDialog dialogo = new AlertDialog
                        .Builder(CrearPedidoActivity.this)
                        .setPositiveButton(R.string.Si, (dialog, which) -> eliminarDetalle((int) v.getTag()))
                        .setNegativeButton(R.string.No, (dialog, which) -> dialog.dismiss())
                .setTitle(R.string.confirmar)
                .setMessage(R.string.confirmar_elimn_msg)
                .create();
                dialogo.show();
            });

            return convertView;
        }
    }
}