package sv.edu.ues.fia.telollevoya.pago;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.DetallePedido;
import sv.edu.ues.fia.telollevoya.Pedido;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.pedidos.cliente.MisPedidosActivity;

public class FacturaActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private AdaptadorProductosAPagar adaptador;
    private ControlBD helper;
    private Pedido pedido;
    TextView txTotal;
    TextView txTitulo;
    TextView txNumPedidoValue;
    TextView txDireccionValue;
    TextView txFechaEntregaValue;
    TextView txMetodoPagoValue;
    TextView txSumaDeSubtotalesValue;
    Double sumaSubtotales = 0.0;
    TextView txcostoEnvioValue;
    TextView txTotalValue;

    String urlDetallesPedido = "https://telollevoya.000webhostapp.com/Pago/obtener_detalles_pedido.php?idPedido=";
    String urlSumaSubtotales = "https://telollevoya.000webhostapp.com/Pago/calcular_suma_subtotales.php?idPedido=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_factura);

        //helper = new ControlBD(this);

        //se recibe el pedido desde la pantalla anterior
        pedido = (Pedido) getIntent().getSerializableExtra("pedido");

        //helper.abrir(); //abriendo la base de datos
        //pedido = new Pedido();// solo necesito un pedido para hacer pruebas del modulo de Pago
        List<DetallePedido> detallesPedido = new ArrayList<>();
        //detallesPedido = helper.consultarDetallesPedido(pedido.getId());

        //detallesPedido = ControladorSevicio.obtenerDetallesPedidoPorId(urlDetallesPedido + pedido.getId(), this);
        detallesPedido = ControladorSevicio.obtenerDetallesPedidoPorId(urlDetallesPedido + pedido.getId(), this);

        txTitulo = (TextView) findViewById(R.id.txtTituloResumen);
        txTitulo.setText("Resumen de Pedido");

        txDireccionValue = (TextView) findViewById(R.id.txtValue3);
        txDireccionValue.setText(pedido.getUbicacion().toString());

        txNumPedidoValue = (TextView) findViewById(R.id.txt1Value);
        txNumPedidoValue.setText(String.valueOf(pedido.getId()));

        txFechaEntregaValue = (TextView) findViewById(R.id.txt2Value);
        Date fechaEntrega = pedido.getFechaEntrega();

        if (fechaEntrega != null) {
            txFechaEntregaValue.setText(String.valueOf(fechaEntrega));
        } else {
            // Si la fecha de entrega es nula, establece la fecha actual
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaActual = dateFormat.format(new Date());
            txFechaEntregaValue.setText(fechaActual);
        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptador = new AdaptadorProductosAPagar(detallesPedido);
        recyclerView.setAdapter(adaptador);

        txSumaDeSubtotalesValue = (TextView) findViewById(R.id.txt4Value);
        //double suma = calcularSumaSubtotales(detallesPedido);

        String url = urlSumaSubtotales + pedido.getId();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Llamada al servicio en segundo plano
                sumaSubtotales = ControladorSevicio.calcularSumaSubtotales(url, FacturaActivity.this);

                // Actualización de la UI en el hilo principal
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(FacturaActivity.this, "La suma de los subtotales es: " + sumaSubtotales, Toast.LENGTH_LONG).show();
                        txSumaDeSubtotalesValue.setText(String.format("$%.2f", sumaSubtotales));

                        // Calcular el total una vez que sumaSubtotales haya sido asignado
                        double sumaTotal = sumaSubtotales + pedido.getCostoEnvio();
                        txTotalValue.setText(String.format("$%.2f", sumaTotal));
                    }
                });
            }
        });


        txcostoEnvioValue = (TextView) findViewById(R.id.txt5Value);
        txcostoEnvioValue.setText(String.format("$%.2f",pedido.getCostoEnvio()));

        txTotalValue = (TextView) findViewById(R.id.txt6Value);
        //double sumaTotal = calcularTotal(detallesPedido); //Metodo para obtener total a pagar
        //double sumaTotal = pedido.getTotalAPagar(); //Metodo alternativo para obtener el total a pagar.
        //double sumaTotal = pedido.getFactura().getTotalPagado(); Otra alternativa para obtener el total a pagar.

        txMetodoPagoValue = (TextView) findViewById(R.id.txtMetodoPagoValue);
        txMetodoPagoValue.setText(pedido.getFactura().getMetodoPago().toString());

        txTotal = (TextView) findViewById(R.id.txt6);
        SpannableString spannableString = new SpannableString("Total A Pagar ");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 13, 0);
        txTotal.setText(spannableString);

    }

//    //Métodos que ya no usaré
//    private double calcularSumaSubtotales(List<DetallePedidoR> detallesPedido) {
//        double suma = 0.0;
//        for (DetallePedidoR dp : detallesPedido) {
//            suma += dp.getSubTotal();
//        }
//        return suma;
//    }
//
//    private double calcularTotal(List<DetallePedidoR> detallesPedido) {
//        double total = 0.0;
//        double costoEnvio = 1.0; //un dolar por default
//        total = calcularSumaSubtotales(detallesPedido) + costoEnvio;
//        return total;
//    }

    public void regresarAPantallaInicial(View view) {
        Intent intent = new Intent(this, MisPedidosActivity.class);
        startActivity(intent);
    }

}
