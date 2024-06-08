package sv.edu.ues.fia.telollevoya.pago;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Reservacion;
import sv.edu.ues.fia.telollevoya.Reservaciones.DetallePedidoR;
import sv.edu.ues.fia.telollevoya.pedidos.cliente.MisPedidosActivity;
import sv.edu.ues.fia.telollevoya.pedidos.negocio.NegociosActivity;

public class PagoAprobadoRActivity extends AppCompatActivity implements ControladorSevicio.ReservacionInsertListener {
    Button btnVerResumen;
    Reservacion reservacion;
    ArrayList<DetallePedidoR> detallesPedidoR;
    TextView txAgradecimiento;
    //Factura factura;
    private String urlFactura = "https://telollevoya.000webhostapp.com/Pago/obtener_factura_por_id.php";
    private String urlReservacion="https://telollevoya.000webhostapp.com/Reservaciones/reservacion_insert.php";
    private String urldetalle="https://telollevoya.000webhostapp.com/Reservaciones/reservacion_detalle_insertar.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservacion_aprobada);

        txAgradecimiento = findViewById(R.id.txtAgradecimiento);
        btnVerResumen = findViewById(R.id.btnVerResumen);

        reservacion = (Reservacion) getIntent().getSerializableExtra("reservacion");
        detallesPedidoR = getIntent().getParcelableArrayListExtra("listaDetalle");

        insertarReservacion();

    }

    //Metodos para los botones
    public void verResumenDeReservacion(View view) {
        Intent intent = new Intent(this, FacturaReservacionActivity.class);
        intent.putExtra("reservacion", reservacion);
        intent.putParcelableArrayListExtra("listaDetalle",  detallesPedidoR);
        startActivity(intent);
    }

    public void regresarAListaDeNegocios(View view) {
        Intent intent = new Intent(this, NegociosActivity.class);
        startActivity(intent);
        finish();
    }

    public void insertarReservacion() {
        String fechaEntregar = "";
        SimpleDateFormat dateFormatEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatSalida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            java.util.Date fecha = dateFormatEntrada.parse(reservacion.getFechaEntregaR());
            fechaEntregar = dateFormatSalida.format(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            // Manejar el error de análisis de fecha aquí
        }

        // Aquí empieza a tratar los datos para no tener problemas con espacios y luego se insertan
        String idClienteCodificado = "", descripcionCodificado = "", anticipoCodificado = "", montoPendienteCodificado = "",
                fechaEntregaCodificado = "", horaEntegaCodificado = "", totalReservacionCodificado = "";
        try {
            idClienteCodificado = URLEncoder.encode(String.valueOf(reservacion.getIdCliente()), "UTF-8");
            descripcionCodificado = URLEncoder.encode(reservacion.getDescripcionReservacion(), "UTF-8");
            anticipoCodificado = URLEncoder.encode(String.valueOf(reservacion.getAnticipoReservacion()), "UTF-8");
            montoPendienteCodificado = URLEncoder.encode(String.valueOf(reservacion.getMontoPediente()), "UTF-8");
            fechaEntregaCodificado = fechaEntregar;
            horaEntegaCodificado = URLEncoder.encode(reservacion.getHoraEntrega(), "UTF-8");
            totalReservacionCodificado = URLEncoder.encode(String.valueOf(reservacion.getTotalRerservacion()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // return;
            // return "Error al codificar el nombre del departamento";
        }

        String url = urlReservacion + "?IDCLIENTE=" + idClienteCodificado + "&DESCRIPCIONRESERVACION=" +
                descripcionCodificado + "&ANTICIPORESERVACION=" + anticipoCodificado + "&MONTOPENDIENTE=" +
                montoPendienteCodificado + "&FECHAENTREGAR=" + fechaEntregaCodificado + "&HORAENTREGAR=" + horaEntegaCodificado +
                "&TOTALRESERVACION=" + totalReservacionCodificado;

        Log.v("URL completa", url); // Añadir esta línea para verificar la URL completa
        ControladorSevicio.insertarReservacion(url, this, this);
    }


    @Override
    public void onReservacionInserted(int idReservacion) {
        Log.v("idReservacionInt", String.valueOf(idReservacion));
        reservacion.setIdReservacion(idReservacion);
        Log.v("DetallesPedido size", String.valueOf(detallesPedidoR.size()));
        insertarDetallePedido(idReservacion);
    }


    public void insertarDetallePedido(int idReservacion){
        for (DetallePedidoR detallePedidoR : detallesPedidoR) {
            String idReservacionCodificado = "", idProductoCodificado = "", cantidadDetalleCodificado = "", subTotalCodificado = "";
            try {
                idReservacionCodificado = URLEncoder.encode(String.valueOf(idReservacion), "UTF-8");
                idProductoCodificado = URLEncoder.encode(String.valueOf(detallePedidoR.getIdProducto()), "UTF-8");
                cantidadDetalleCodificado = URLEncoder.encode(String.valueOf(detallePedidoR.getCantidadDetalle()), "UTF-8");
                subTotalCodificado = URLEncoder.encode(String.valueOf(detallePedidoR.getSubTotal()), "UTF-8");

                String url = urldetalle + "?IDRESERVACION=" + idReservacionCodificado + "&IDPRODUCTO=" +
                        idProductoCodificado + "&CANTIDADDETALLE=" + cantidadDetalleCodificado + "&SUBTOTAL=" + subTotalCodificado;

                Log.v("DetallePedidoR", detallePedidoR.toString());
                ControladorSevicio.insertarDetalle(url, this);
                Log.v("Url Detalle", url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


}

