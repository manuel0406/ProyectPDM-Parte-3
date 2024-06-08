package sv.edu.ues.fia.telollevoya.pedidos.cliente;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.EstadoOrden;
import sv.edu.ues.fia.telollevoya.Pedido;
import sv.edu.ues.fia.telollevoya.Producto;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Repartidor;
import sv.edu.ues.fia.telollevoya.Reservaciones.ReservacionesConsultarActivity;
import sv.edu.ues.fia.telollevoya.pedidos.negocio.NegociosActivity;
import sv.edu.ues.fia.telollevoya.seguridad.IniciarSesionActivity;

public class MisPedidosActivity extends Activity {

    //Esta variable almacena el idCliente
    //String idCliente;
    private ArrayList<Pedido> pedidosActivosList;
    private ArrayList<Pedido> pedidosRelizadosList;
    private ListView pedidosActivosListView;
    private ListView pedidosRealizListView;
    private final int PEDIDO_ACTIVO = 1;
    private final int PEDIDO_CANCELADO = 2;
    private final int PEDIDO_REALIZADO = 3;
    private int idCliente;
    public String idClienteStr;
    private final String URL_PEDIDOS_CLIENTE_SERVICIO = "https://telollevoya.000webhostapp.com/Pedidos/pedidos_cliente.php?cliente=";
    private final String URL_ACTUALIZAR_ESTADOPEDIDO_SERVICIO = "https://telollevoya.000webhostapp.com/Pedidos/actualizar_estado_pedido.php?";

    ControlBD controlBD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pedidos);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Aqui recibe el idCliente desde la pantalla iniciar sesión
//        Intent intent = getIntent();
//        idClienteStr = intent.getStringExtra("idCliente");
//        idCliente = Integer.parseInt(idClienteStr);
        controlBD = new ControlBD(MisPedidosActivity.this);
        pedidosRelizadosList = new ArrayList<>();
        pedidosActivosList = new ArrayList<>();
        pedidosActivosListView = findViewById(R.id.pedidos_activos_listView);
        pedidosRealizListView = findViewById(R.id.pedidos_realiz_listView);
        getPedidos();
        PedidosActivosAdapter activosAdapter = new PedidosActivosAdapter(MisPedidosActivity.this, 0, pedidosActivosList);
        pedidosActivosListView.setAdapter(activosAdapter);
        PedidosRealizadosAdapter realizadosAdapter = new PedidosRealizadosAdapter(MisPedidosActivity.this, 0, pedidosRelizadosList);
        pedidosRealizListView.setAdapter(realizadosAdapter);

    }

    public void getPedidos(){
        try {
            controlBD.abrir();
                idCliente = controlBD.consultaUsuario();
                controlBD.cerrar();
                String url = URL_PEDIDOS_CLIENTE_SERVICIO + idCliente;
                String json = ControladorSevicio.obtenerRespuestaPeticion(url, getApplicationContext());
                try{
                    JSONArray jsonArray = new JSONArray(json);
                    for(int i = 0; i <= jsonArray.length() - 1; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Pedido pedido = new Pedido();
                        pedido.setId(jsonObject.getInt("IDPEDIDO"));

                        EstadoOrden estado = new EstadoOrden();
                        estado.setId(jsonObject.getInt("IDESTADO"));
                        estado.setTipo(jsonObject.getString("TIPOESTADO"));
                        pedido.setEstadoOrden(estado);

                        Repartidor rep = new Repartidor();
                        rep.setIdRepartidor(jsonObject.getString("IDREPARTIDOR"));
                        pedido.setRepartidor(rep);

                        pedido.setTotalAPagar(Float.parseFloat(jsonObject.getString("TOTALAPAGAR")));
                        pedido.setDescripcionOrden(jsonObject.getString("DESCRIPCIONORDEN"));

                        //Añadiendo a realizados-cancelados o a Activos
                        if(pedido.getEstadoOrden().getId() == 1)
                            pedidosActivosList.add(pedido);
                        else
                            pedidosRelizadosList.add(pedido);

                        String fechaPed = jsonObject.getString("FECHAPEDIDO");
                        if (fechaPed != null)
                            pedido.setFechaPedido(Timestamp.valueOf(fechaPed));
                        else
                            pedido.setFechaPedido(null);

                        String fechaEn = jsonObject.getString("FECHAENTREGAP");
                        if (!fechaEn.equalsIgnoreCase("null"))
                            pedido.setFechaEntrega(Date.valueOf(fechaEn));
                        else
                            pedido.setFechaEntrega(null);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
        }catch(NullPointerException ex){
        ex.printStackTrace();
            Toast.makeText(MisPedidosActivity.this, "No  hay nada por mostrar", Toast.LENGTH_SHORT);
        }
    }


    //----------------------------------------------------------------------------------------------
    //                          METODO PARA CERRAR SESION
    //----------------------------------------------------------------------------------------------
    public void cerrarSesion(View v) {

        //Limpio base de usuarios disponibles
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


    public boolean cancelarPedido(int idPedido){
        String url = URL_ACTUALIZAR_ESTADOPEDIDO_SERVICIO + "pedido=" + idPedido + "&estado="+2;
        String respuesta = ControladorSevicio.obtenerRespuestaPeticion(url, getApplicationContext());
        return respuesta.toLowerCase().contains("act");
    }

    public void backButton(View v){
        finish();
    }

    public void irSeleccionProducto(View v){
        Intent intent = new Intent(MisPedidosActivity.this, NegociosActivity.class);
        intent.putExtra("idCliente", idCliente);
        startActivity(intent);
    }

    public void irReservacionesConsultar(View v){

      //  Toast.makeText(this, idClienteStr,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MisPedidosActivity.this, ReservacionesConsultarActivity.class);
        //intent.putExtra("idCliente", idClienteStr);

        startActivity(intent);
    }

    public class PedidosActivosAdapter extends ArrayAdapter<Pedido>{

        public PedidosActivosAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.pedido_card_custom, parent, false);
            TextView codTextView = convertView.findViewById(R.id.cod_textView);
            TextView descripTextView = convertView.findViewById(R.id.desc_textView);
            TextView montoTextView = convertView.findViewById(R.id.monto_textView);
            TextView fechaPedido = convertView.findViewById(R.id.fechaPedido_textView);
            TextView estado =convertView.findViewById(R.id.estado_textView);
            Button cancelarBtn = convertView.findViewById(R.id.cancelar_btn);

            Pedido pedido = pedidosActivosList.get(position);
            codTextView.setText(Integer.toString(pedido.getId()));
            descripTextView.setText(pedido.getDescripcionOrden());
            montoTextView.setText("$"+Float.toString(pedido.getTotalAPagar()));
            fechaPedido.setText(pedido.getFechaPedido().toString());
            estado.setText("Activo");

            cancelarBtn.setTag(pedido.getId());
            cancelarBtn.setOnClickListener(v ->{
                //Solicitud al server a modificar estado de pedido
                int idPedido = (int) v.getTag();
                boolean result = cancelarPedido(idPedido);
                if(result) {
                    v.setEnabled(false);
                    v.setBackgroundColor(Color.GRAY);
                    //Mostrar notificación Push
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),"Channel")
                            .setSmallIcon(R.drawable.logo_general)
                            .setContentTitle("Pedido Cancelado")
                            .setContentText("Haz cancelado con éxito tu pedido")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "Channel";// The user-visible name of the channel.
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel mChannel = new NotificationChannel("Channel", name, importance);
                        notificationManager.createNotificationChannel(mChannel);
                    }
                    notificationManager.notify(1, builder.build());
                }
                else
                    Toast.makeText(MisPedidosActivity.this, "No se pudo cancelar :(", Toast.LENGTH_SHORT).show();

            });

            return convertView;
        }
    }

    public class PedidosRealizadosAdapter extends ArrayAdapter<Pedido>{
        public PedidosRealizadosAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.pedido_realizado_card_custom, parent, false);
            TextView codTextView = convertView.findViewById(R.id.cod_textView);
            TextView descripTextView = convertView.findViewById(R.id.desc_textView);
            TextView montoTextView = convertView.findViewById(R.id.monto_textView);
            TextView estado =convertView.findViewById(R.id.estado_textView);
            TextView fechaEntregado = convertView.findViewById(R.id.fechaEntregado_textView);

            Pedido pedido = pedidosRelizadosList.get(position);
            codTextView.setText(Integer.toString(pedido.getId()));
            descripTextView.setText(pedido.getDescripcionOrden());
            switch(pedido.getEstadoOrden().getId()){
                case PEDIDO_REALIZADO:
                    estado.setText("Realizado");
                    break;
                case PEDIDO_CANCELADO:
                    estado.setText("Cancelado");
                    break;
            }
            montoTextView.setText("$" + Float.toString(pedido.getTotalAPagar()));
            fechaEntregado.setText(pedido.getFechaPedido().toString());

            return convertView;
        }
    }
}