package sv.edu.ues.fia.telollevoya.pedidos.cliente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.DetallePedido;
import sv.edu.ues.fia.telollevoya.Producto;
import sv.edu.ues.fia.telollevoya.R;

public class SeleccionarProductoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView prod_listView;
    private ImageButton carritoBtn;
    private SearchView searchView;
    ArrayList<Producto> productos;
    ArrayList<DetallePedido> detallesPedidosList;
    ProductoCardAdapter adapter;
    ControlBD db;
    private int idNegocio;
    private final String URL_SERVICIO_PRODUCTOS = "https://telollevoya.000webhostapp.com/Pedidos/productos_negocio.php?negocio=";
    private final String URL_SERVICIO_HORA = "https://telollevoya.000webhostapp.com/Pedidos/hora.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_producto);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        idNegocio = getIntent().getIntExtra("idNegocio", 1);
        db = new ControlBD(SeleccionarProductoActivity.this);
        prod_listView = findViewById(R.id.productos_listView);
        carritoBtn = findViewById(R.id.carrito_btn);
        searchView = findViewById(R.id.searchView);
        productos = new ArrayList<>();
        detallesPedidosList = new ArrayList<>();
        getProductosPorNegocio();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    getProductosPorNegocio();
                    prod_listView.setVisibility(View.VISIBLE);
                }
                else {
                    ArrayList<Producto> nuevosProd = (ArrayList<Producto>) productos.stream().filter(producto ->
                            producto.getNombre().toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList());
                    if(!nuevosProd.isEmpty()){
                        productos.clear();
                        productos.addAll(nuevosProd);
                        adapter.notifyDataSetChanged();
                        prod_listView.setVisibility(View.VISIBLE);
                    }else{
                        prod_listView.setVisibility(View.INVISIBLE);
                    }
                }
                return false;
            }
        });
    }

    public void agregarProducto(int posicion) {
        int hour  = getHoraActual();
        Producto producto = productos.get(posicion);
        if(producto.getNombre().toLowerCase().contains("pupusa")){
            if((hour >= 6 && hour <= 9)){
                int cantidadDefault = 1;
                DetallePedido detallePedido = new DetallePedido( cantidadDefault, producto.getPrecio()*cantidadDefault);
                detallePedido.setProducto(producto);
                Toast.makeText(SeleccionarProductoActivity.this, "Producto agregado al carrito :)", Toast.LENGTH_SHORT).show();
                detallesPedidosList.add(detallePedido);
            }
            else
                Toast.makeText(SeleccionarProductoActivity.this, "No se puede seleccionar pupusas a esta hora", Toast.LENGTH_SHORT).show();

        }else{
            int cantidadDefault = 1;
            DetallePedido detallePedido = new DetallePedido( cantidadDefault, producto.getPrecio()*cantidadDefault);
            detallePedido.setProducto(producto);
            Toast.makeText(SeleccionarProductoActivity.this, "Producto agregado al carrito :)", Toast.LENGTH_SHORT).show();
            detallesPedidosList.add(detallePedido);
        }
    }

    public void irCarrito(View v){
        Bundle extras = new Bundle();
        extras.putSerializable("detalles",detallesPedidosList);
        Intent intent = new Intent(SeleccionarProductoActivity.this, CrearPedidoActivity.class);
        intent.putExtras(extras);
        this.startActivityForResult(intent, 1);
    }

    public void irMisPedidos(View v){
        Intent intent = new Intent(SeleccionarProductoActivity.this, MisPedidosActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            switch (data.getIntExtra("codigo", -1)){
                case 1://eliminar detalle de la lista
                    ArrayList<Integer> posiciones = (ArrayList) data.getSerializableExtra("positions");
                    int contposDesplazado = 0;
                    for(int pos = 0; pos < posiciones.size(); pos++){
                        int posActual = pos - contposDesplazado;
                        detallesPedidosList.remove(posActual);
                        Toast.makeText(this, "A eliminar " + pos, Toast.LENGTH_SHORT).show();
                        contposDesplazado++;
                    }
                    break;
                default:
                    break;
            }
        }

        //Lectura de texto ingresado por Voz
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            assert results != null;
            searchView.setQuery(results.get(0), true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getProductosPorNegocio(){
        String url = URL_SERVICIO_PRODUCTOS + idNegocio;
        String json = ControladorSevicio.obtenerRespuestaPeticion(url,getApplicationContext());
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Producto producto = new Producto();
                producto.setId(jsonObject.getInt("IDPRODUCTO"));
                producto.setIdNegocio(jsonObject.getInt("IDNEGOCIO"));
                producto.setNombre(jsonObject.getString("NOMBREPRODUCTO"));
                producto.setTipo(jsonObject.getString("TIPOPRODUCTO"));
                producto.setDescripcion(jsonObject.getString("DESCRIPCIONPRODUCTO"));
                producto.setPrecio(Float.parseFloat(jsonObject.getString("PRECIOPRODUCTO")));
                producto.setExistencia(jsonObject.getInt("EXISTENCIAPRODUCTO") > 0);
                productos.add(producto);
            }
            adapter = new ProductoCardAdapter(this, productos);
            prod_listView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getHoraActual(){
        String json = ControladorSevicio.obtenerRespuestaPeticion(URL_SERVICIO_HORA,getApplicationContext());
        try {
            JSONArray jsonArray = new JSONArray(json);
            return jsonArray.getJSONObject(0).getInt("hora");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void backButton(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmar);
        builder.setMessage(R.string.confirmar_msg);
        builder.setPositiveButton(R.string.Si, (dialog, which) -> finish());
        builder.setNegativeButton(R.string.No, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmar);
        builder.setMessage(R.string.confirmar_msg);
        builder.setPositiveButton(R.string.Si, (dialog, which) -> super.onBackPressed());
        builder.setNegativeButton(R.string.No, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class ProductoCardAdapter extends ArrayAdapter<Producto> {
        List<Producto> productos;
        public  ProductoCardAdapter(@NonNull Context context, List<Producto> productos){
            super(context, 0, productos);
            this.productos = productos;
        }
        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.activity_producto_card_custom, parent, false);
            TextView nomProdTextView = convertView.findViewById(R.id.nomProd_textView);
            TextView precioProdTextView = convertView.findViewById(R.id.precioProd_textView);
            TextView descrProdTextView = convertView.findViewById(R.id.descProd_textView);
            Button agregarBTn = convertView.findViewById(R.id.agregar_btn);

            Producto p = productos.get(position);
            nomProdTextView.setText(p.getNombre());
            precioProdTextView.setText("$"+Float.toString(p.getPrecio()));
            descrProdTextView.setText(p.getDescripcion());
            agregarBTn.setTag(productos.indexOf(p));
            try {
                agregarBTn.setOnClickListener(v -> agregarProducto((int) v.getTag()));
            }catch (Exception ex){ex.printStackTrace();}
            return convertView;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Crea intent para abrir pantalla de reconocimiento de voz
    public void reconocerVoz(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }
}