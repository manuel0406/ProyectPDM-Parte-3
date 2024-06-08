package sv.edu.ues.fia.telollevoya.pago;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.DetallePedido;
import sv.edu.ues.fia.telollevoya.EstadoOrden;
import sv.edu.ues.fia.telollevoya.Pedido;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.pedidos.cliente.MisPedidosActivity;

public class PagoAprobadoPActivity extends AppCompatActivity implements ControladorSevicio.PedidoInsertListener {
    Pedido pedido;
    ArrayList<DetallePedido> detallesPedido;
    private String urlUbicacion = "https://telollevoya.000webhostapp.com/Pago/ubicacion_insertar.php";
    private String urlPedido="https://telollevoya.000webhostapp.com/Pago/pedido_insertar.php";
    private String urlDetalle ="https://telollevoya.000webhostapp.com/Pago/pedido_detalle_insertar.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_aprobado);

        pedido = (Pedido) getIntent().getSerializableExtra("pedido");
        detallesPedido = pedido.getDetallePedidoList();
        Log.v("DetallesPedido size", String.valueOf(detallesPedido.size()));
        Log.v("DetallesPedido elemento1", detallesPedido.get(0).getProducto().getNombre());

        EstadoOrden estado = new EstadoOrden(1, "ACTIVO");
        pedido.setEstadoOrden(estado); //al aprobarse el pago inmediatamente la orden pasa a estar activa

        insertarUbicacion(); //Insertar ubicacion usando Web Service
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE); // Recuperar el ID de la ubicacion de SharedPreferences
        int lastUbicacionId = sharedPreferences.getInt("lastUbicacionId", -1);
        pedido.getUbicacion().setId(lastUbicacionId);

        insertarPedido();

//        //Código para hacer pruebas nada más
//        factura = pedido.getFactura();
//        Toast.makeText(this, "id Factura: "+ factura.getId(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "tipoPago: "+ factura.getMetodoPago().getTipoPago(), Toast.LENGTH_SHORT).show();
//        consultarFactura();


    }

    //Metodos para los botones
    public void verResumenDePedido(View view) {
        Intent intent = new Intent(this, FacturaActivity.class);
        intent.putExtra("pedido", (Serializable) pedido);
        startActivity(intent);
    }

    public void verEstadoPedido(View view) {
        Intent intent = new Intent(this, MisPedidosActivity.class);
        intent.putExtra("idCliente", pedido.getCliente().getIdCliente());
        startActivity(intent);
    }


    public void insertarPedido() {
        String fechaPedido = "";
        SimpleDateFormat dateFormatSalida = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date fecha = pedido.getFechaPedido();
        fechaPedido = dateFormatSalida.format(fecha);

        // Aquí empieza a tratar los datos para no tener problemas con espacios y luego se insertan
        String idUbicacionCodificado = "", idEstadoCodificado = "", idFacturaCodificado = "", idClienteCodificado = "",
                totalAPagarCodificado = "", fechaPedidoCodificado = "", descripcionOrdenCodificado = "";
        try {
            idUbicacionCodificado = URLEncoder.encode(String.valueOf(pedido.getUbicacion().getId()), "UTF-8");
            idEstadoCodificado = URLEncoder.encode(String.valueOf(pedido.getEstadoOrden().getId()), "UTF-8");
            idFacturaCodificado = URLEncoder.encode(String.valueOf(pedido.getFactura().getId()), "UTF-8");
            idClienteCodificado = URLEncoder.encode(String.valueOf(pedido.getCliente().getIdCliente()), "UTF-8");
            totalAPagarCodificado = URLEncoder.encode(String.valueOf(pedido.getTotalAPagar()), "UTF-8");
            fechaPedidoCodificado = URLEncoder.encode(fechaPedido, "UTF-8");
            descripcionOrdenCodificado = URLEncoder.encode(String.valueOf(pedido.getDescripcionOrden()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = urlPedido + "?IDUBICACION=" + idUbicacionCodificado + "&IDESTADO=" +
                idEstadoCodificado + "&IDFACTURA=" + idFacturaCodificado + "&IDCLIENTE=" +
                idClienteCodificado + "&TOTALAPAGAR=" + totalAPagarCodificado + "&FECHAPEDIDO=" + fechaPedidoCodificado +
                "&DESCRIPCIONORDEN=" + descripcionOrdenCodificado;

        Log.v("URL completa", url); // Añadir esta línea para verificar la URL completa
        ControladorSevicio.insertarPedido(url, this, this);
    }


    @Override
    public void onPedidoInserted(int idPedido) {
        Log.v("idPedidoInt", String.valueOf(idPedido));
        pedido.setId(idPedido);
        Log.v("DetallesPedido size", String.valueOf(detallesPedido.size()));
        insertarDetallePedido(idPedido);
    }


    public void insertarDetallePedido(int idPedido){
        for (DetallePedido detallePedido : detallesPedido) {
            String idPedidoCodificado = "", idProductoCodificado = "", cantidadDetalleCodificado = "", subTotalCodificado = "";
            try {
                idPedidoCodificado = URLEncoder.encode(String.valueOf(idPedido), "UTF-8");
                idProductoCodificado = URLEncoder.encode(String.valueOf(detallePedido.getProducto().getId()), "UTF-8");
                cantidadDetalleCodificado = URLEncoder.encode(String.valueOf(detallePedido.getCantidad()), "UTF-8");
                subTotalCodificado = URLEncoder.encode(String.valueOf(detallePedido.getSubTotal()), "UTF-8");

                String url = urlDetalle + "?IDPEDIDO=" + idPedidoCodificado + "&IDPRODUCTO=" +
                        idProductoCodificado + "&CANTIDADDETALLE=" + cantidadDetalleCodificado + "&SUBTOTAL=" + subTotalCodificado;

                Log.v("DetallePedidoR", detallePedido.toString());
                ControladorSevicio.insertarDetallePedido(url, this);
                Log.v("Url Detalle", url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    public void insertarUbicacion() {
        // Obtener valores de los atributos de la ubicación
        String idDistrito = String.valueOf(pedido.getUbicacion().getDistrito().getIdDistrito());
        String descripcionUbicacion = String.valueOf(pedido.getUbicacion().getDescripcion());

        // Inicialización de variables codificadas
        String idDistritoCodificado = "";
        String descripcionUbicacionCodificada = "";
        try {
            // Codificar los valores para incluirlos en la URL
            idDistritoCodificado = URLEncoder.encode(idDistrito, "UTF-8");
            descripcionUbicacionCodificada = URLEncoder.encode(descripcionUbicacion, "UTF-8");

            // Construir la URL con los parámetros codificados
            String url = urlUbicacion + "?idDistrito=" + idDistritoCodificado + "&descripcionUbicacion=" + descripcionUbicacionCodificada;

            Log.v("Url Ubicación: ", url);
            ControladorSevicio.insertarUbicacion(url, this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Error al codificar la URL para la inserción de ubicación", Toast.LENGTH_LONG).show();
        }
    }


//    private void consultarFactura() {
//        int idFactura = factura.getId();
//        String urlConsulta = urlFactura + "?idFactura=" + idFactura;
//        Factura facturaConsultada = ControladorSevicio.obtenerFacturaPorId(urlConsulta, this);
//
//        if (facturaConsultada != null) {
//            mostrarDatosFactura(facturaConsultada);
//        } else {
//            Toast.makeText(this, "Error al obtener la factura", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void mostrarDatosFactura(Factura factura) {
//        String mensaje = "ID Factura: " + factura.getId() + "\n" ;
//               // "Total Pagado: " + factura.getTotalPagado() + "\n" +
//               // "Fecha Emisión: " + factura.getFechaEmision() + "\n";
//
//        // Si tienes el tipo de pago disponible en la factura
//        if (factura.getMetodoPago() != null) {
//            mensaje += //"IdPago: "+ factura.getMetodoPago().getId()+
//                    "Tipo de Pago: " + factura.getMetodoPago().getTipoPago() + "\n";
//        }
//
//        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
//    }

}

