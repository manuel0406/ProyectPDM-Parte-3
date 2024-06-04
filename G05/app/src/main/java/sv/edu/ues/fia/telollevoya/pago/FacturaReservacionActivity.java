package sv.edu.ues.fia.telollevoya.pago;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.DetallePedido;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Reservacion;
import sv.edu.ues.fia.telollevoya.Reservaciones.DetallePedidoR;
import sv.edu.ues.fia.telollevoya.pedidos.cliente.MisPedidosActivity;

public class FacturaReservacionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdaptadorProductosAPagar adaptador;
    private Reservacion reservacion;
    ArrayList<DetallePedidoR> detallesPedidoR;//la lista que se recibe de la vista anterior
    TextView txTitulo;
    TextView txNumReservacion;
    TextView txFechaEntrega;
    TextView txDescripcion;
    TextView txTotal;
    TextView txMontoAnticipado;
    TextView txMontoPendiente;
    TextView txMetodoPago;
    TextView txNumReservacionValue;
    TextView txFechaEntregaValue;
    TextView txDescripcionValue;
    TextView txTotalValue;
    TextView txMontoAnticipadoValue;
    TextView txMontoPendienteValue;
    TextView txMetodoPagoValue;

    String urlProducto = "https://telollevoya.000webhostapp.com/Pago/obtener_producto_por_id.php?idProducto=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_factura);

        //se recibe la reservacion y la lista de detalles desde la pantalla anterior
        reservacion = (Reservacion) getIntent().getSerializableExtra("reservacion");
        detallesPedidoR = getIntent().getParcelableArrayListExtra("listaDetalle");

        ArrayList<DetallePedido> listaAdaptada = new ArrayList<>(); //la lista de productos que se mostrará
        for(int i=0; i< detallesPedidoR.size(); i++){
            DetallePedido dp = new DetallePedido();
            dp.setId(detallesPedidoR.get(i).getIdDetallePedido());
            dp.setCantidad(detallesPedidoR.get(i).getCantidadDetalle());
            dp.setIdReservacion(detallesPedidoR.get(i).getIdReservacion());
            dp.setSubTotal((float)detallesPedidoR.get(i).getSubTotal());
            dp.setProducto(ControladorSevicio.obtenerProductoPorId(urlProducto+ detallesPedidoR.get(i).getIdProducto(), this));
            listaAdaptada.add(dp);
        }

        txTitulo = (TextView) findViewById(R.id.txtTituloResumen);
        txNumReservacion = (TextView) findViewById(R.id.txt1);
        txFechaEntrega = (TextView) findViewById(R.id.txt2);
        txDescripcion = (TextView) findViewById(R.id.txt3);
        txTotal = (TextView) findViewById(R.id.txt4);
        txMontoAnticipado = (TextView) findViewById(R.id.txt5);
        txMontoPendiente = (TextView) findViewById(R.id.txt6);
        txMetodoPago = (TextView) findViewById(R.id.txtMetodoPago);

        txNumReservacionValue = (TextView) findViewById(R.id.txt1Value);
        txFechaEntregaValue = (TextView) findViewById(R.id.txt2Value);
        txDescripcionValue = (TextView) findViewById(R.id.txtValue3);
        txTotalValue = (TextView) findViewById(R.id.txt4Value);
        txMontoAnticipadoValue = (TextView) findViewById(R.id.txt5Value);
        txMontoPendienteValue = (TextView) findViewById(R.id.txt6Value);
        txMetodoPagoValue = (TextView) findViewById(R.id.txtMetodoPagoValue);



        txTitulo.setText("Resumen de Reservacion");

        txNumReservacion.setText("Número de Reservación: ");
        txNumReservacionValue.setText(String.valueOf(reservacion.getIdReservacion()));

        txFechaEntrega.setText("Fecha de entrega: ");
        txFechaEntregaValue.setText(reservacion.getFechaEntregaR() + " a las "+ reservacion.getHoraEntrega());

        txDescripcion.setText("Descripción: ");
        txDescripcionValue.setText(reservacion.getDescripcionReservacion());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorProductosAPagar(listaAdaptada);
        recyclerView.setAdapter(adaptador);

        SpannableString spannableString = new SpannableString("Total A Pagar: ");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 13, 0);
        txTotal.setText(spannableString);
        txTotalValue.setText(String.format("$%.2f", reservacion.getTotalRerservacion()));

        txMontoAnticipado.setText("Monto Anticipado: ");
        txMontoAnticipadoValue.setText(String.format("$%.2f", reservacion.getAnticipoReservacion()));

        txMontoPendiente.setText("Monto Pendiente: ");
        txMontoPendienteValue.setText(String.format("$%.2f", reservacion.getMontoPediente()));

        txMetodoPago.setVisibility(View.GONE);
        txMetodoPagoValue.setVisibility(View.GONE);
    }

    public void regresarAPantallaInicial(View view) {
        Intent intent = new Intent(this, MisPedidosActivity.class);
        startActivity(intent);
    }

}