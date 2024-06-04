package sv.edu.ues.fia.telollevoya.pedidos.negocio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import sv.edu.ues.fia.telollevoya.Cliente;
import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.Pedido;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Repartidor;
import sv.edu.ues.fia.telollevoya.Ubicacion;
import sv.edu.ues.fia.telollevoya.seguridad.IniciarSesionActivity;

public class PedidosDelRepartidorActivity extends AppCompatActivity {

    private ArrayList<Pedido> pedidos;
    private ListView pedidosListView;
    private int idRepartidor;
    ControlBD controlBD;
    private final String URL_PEDIDOS_REPARTIDOR = "https://telollevoya.000webhostapp.com/Pedidos/pedidos_repartidor.php?repartidor=";
    private final String URL_ACTUALIZAR_ESTADOPEDIDO_SERVICIO = "https://telollevoya.000webhostapp.com/Pedidos/actualizar_estado_pedido.php?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_del_repartidor);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        pedidos = new ArrayList<>();
        pedidosListView = findViewById(R.id.pedidos_listView);
        controlBD = new ControlBD(PedidosDelRepartidorActivity.this);
        controlBD.abrir();
        idRepartidor = controlBD.consultaUsuario();
        controlBD.cerrar();
        getPedidos();
        PedidosAdapter adapter = new PedidosAdapter(PedidosDelRepartidorActivity.this, 0, pedidos);
        pedidosListView.setAdapter(adapter);
    }

    public void getPedidos(){
        String url = URL_PEDIDOS_REPARTIDOR+idRepartidor;
        String respuesta = ControladorSevicio.obtenerRespuestaPeticion(url, getApplicationContext());
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            for (int i = 0; i < jsonArray.length(); i++){
                Pedido pedido = new Pedido();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                pedido.setId(jsonObject.getInt("IDPEDIDO"));
                pedido.setTotalAPagar(Float.parseFloat(jsonObject.getString("TOTALAPAGAR")));
                pedido.setFechaPedido(Timestamp.valueOf(jsonObject.getString("FECHAPEDIDO")));
                pedido.setDescripcionOrden(jsonObject.getString("DESCRIPCIONORDEN"));

                Cliente cliente = new Cliente();
                cliente.setIdCliente(jsonObject.getString("IDCLIENTE"));
                pedido.setCliente(cliente);

                Ubicacion ub = new Ubicacion();
                ub.setDescripcion(jsonObject.getString("DESCRIPCIONUBICACION"));
                pedido.setUbicacion(ub);

                pedidos.add(pedido);
            }
//            String enviado = getIntent().getExtras().getString("idRepartidor");
//            if (enviado != null) {
//                int idRepartidor = Integer.parseInt(enviado);
//                controlBD.abrir();
//               pedidos = controlBD.getPedidosActivosPorRepartidor(idRepartidor);
//            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void backButton(View v){
        finish();
    }

    public class PedidosAdapter extends ArrayAdapter<Pedido> {
        public PedidosAdapter(@NonNull Context context, int resource, @NonNull List<Pedido> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.pedido_repartidor_card_custom, parent, false);
            TextView codTextView = convertView.findViewById(R.id.cod_textView);
            TextView descripTextView = convertView.findViewById(R.id.desc_textView);
            TextView montoTextView = convertView.findViewById(R.id.monto_textView);
            TextView fechaPedidoTextView = convertView.findViewById(R.id.fechaPedido_textView);
            TextView ubicacionTextView = convertView.findViewById(R.id.ubicacion_textView);
            Button finalizarBtn = convertView.findViewById(R.id.finalizar_btn);

            Pedido pedido = pedidos.get(position);
            codTextView.setText(Integer.toString(pedido.getId()));
            descripTextView.setText(pedido.getDescripcionOrden());
            montoTextView.setText("$"+Float.toString(pedido.getTotalAPagar()));
            fechaPedidoTextView.setText(pedido.getFechaPedido().toString());
            ubicacionTextView.setText(pedido.getUbicacion().getDescripcion());

            finalizarBtn.setTag(pedido.getId());
            finalizarBtn.setOnClickListener(v ->{
                controlBD.abrir();
                int idPedido = (int) v.getTag();
                String url = URL_ACTUALIZAR_ESTADOPEDIDO_SERVICIO+"pedido="+idPedido+"&estado=3";//Dando por ENTREGADO el pedido con la fecha actual
                String respuesta = ControladorSevicio.obtenerRespuestaPeticion(url, getApplicationContext());
                if (respuesta.toLowerCase().contains("actualizado")){
                    Toast.makeText(PedidosDelRepartidorActivity.this, "Estado de pedido actualizado a REALIZADO!!", Toast.LENGTH_SHORT).show();
                    v.setEnabled(false);
                    v.setBackgroundColor(Color.GRAY);
                }
                else Toast.makeText(PedidosDelRepartidorActivity.this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
            });
            return convertView;
        }
    }

    //----------------------------------------------------------------------------------------------
    //                          METODO PARA CERRAR SESION
    //----------------------------------------------------------------------------------------------
    public void cerrarSesion(View v) {
        // Limpio base de usuarios disponibles
        controlBD = new ControlBD(this);
        controlBD.abrir();
        controlBD.LimpiarUsuario();
        controlBD.cerrar();
        Toast.makeText(this, "Vuelve pronto", Toast.LENGTH_SHORT).show();

        // Crear un Intent para iniciar sesion "Iniciar Sesion"
        Intent intent = new Intent(this, IniciarSesionActivity.class);
        intent.putExtra("desdeInicioApp", false);
        startActivity(intent);
        finish();
    }
}