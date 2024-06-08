package sv.edu.ues.fia.telollevoya.pedidos.cliente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
    ArrayList<String> imagenesProductos;
    ArrayList<String> imagenesProdSelec;
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
        imagenesProdSelec = new ArrayList<>();
        imagenesProductos = new ArrayList<>();
        getProductosPorNegocio();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    productos.clear();
                    imagenesProductos.clear();
                    getProductosPorNegocio();
                    prod_listView.setVisibility(View.VISIBLE);
                }
                else {
                    ArrayList<Producto> nuevosProd = new ArrayList<>();
                    ArrayList<String> nuevasImg = new ArrayList<>();
                    // Recorrer el ArrayList y aplicar el filtro
                    for (int i = 0; i < productos.size(); i++) {
                        Producto producto = productos.get(i);
                        if (producto.getNombre().toLowerCase().contains(newText.toLowerCase())) {
                            nuevosProd.add(producto);
                            nuevasImg.add(imagenesProductos.get(i));
                        }
                    }
                    if(!nuevosProd.isEmpty()){
                        productos.clear();
                        productos.addAll(nuevosProd);
                        imagenesProductos.clear();
                        imagenesProductos.addAll(nuevasImg);
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
                //agregando imagen relacionada al producto en cuestion
                imagenesProdSelec.add(imagenesProductos.get(posicion));
            }
            else
                Toast.makeText(SeleccionarProductoActivity.this, "No se puede seleccionar pupusas a esta hora", Toast.LENGTH_SHORT).show();

        }else{
            int cantidadDefault = 1;
            DetallePedido detallePedido = new DetallePedido( cantidadDefault, producto.getPrecio()*cantidadDefault);
            detallePedido.setProducto(producto);
            Toast.makeText(SeleccionarProductoActivity.this, "Producto agregado al carrito :)", Toast.LENGTH_SHORT).show();
            detallesPedidosList.add(detallePedido);
            //agregando imagen relacionada al producto en cuestion
            imagenesProdSelec.add(imagenesProductos.get(posicion));
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
                        int posActual = posiciones.get(pos) - contposDesplazado;
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
                producto.setId(jsonObject.getInt("idProducto"));
                producto.setIdNegocio(jsonObject.getInt("idNegocio"));
                producto.setNombre(jsonObject.getString("nombreProducto"));
                producto.setTipo(jsonObject.getString("tipoProducto"));
                producto.setDescripcion(jsonObject.getString("descripcionProducto"));
                producto.setPrecio(Float.parseFloat(jsonObject.getString("precioProducto")));
                producto.setExistencia(jsonObject.getInt("existenciaProducto") > 0);
                productos.add(producto);
                imagenesProductos.add(jsonObject.getString("imagenProducto"));
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
            ImageView prodImg = convertView.findViewById(R.id.producto_img);

            Producto p = productos.get(position);
            nomProdTextView.setText(p.getNombre());
            precioProdTextView.setText("$"+Float.toString(p.getPrecio()));
            descrProdTextView.setText(p.getDescripcion());
            agregarBTn.setTag(productos.indexOf(p));
            try {
                agregarBTn.setOnClickListener(v -> agregarProducto((int) v.getTag()));
            }catch (Exception ex){ex.printStackTrace();}

            //Cargando Imagen de producto
            String imagen = imagenesProductos.get(position);
            if(!imagen.isEmpty() && imagen != null){
                byte[] decodedBytes = new byte[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    decodedBytes = Base64.getDecoder().decode(imagen);
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                // Crear un archivo temporal en el almacenamiento interno
                File cacheDir = getApplicationContext().getCacheDir();
                File tempFile = new File(cacheDir, "temp_image_"+p.getId()+".jpg");
                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Picasso.get()
                        .load(Uri.fromFile(tempFile))
                        .error(R.drawable.logo_general)
                        .into(prodImg);
            }
            else Picasso.get().load(R.drawable.logo_general).into(prodImg);
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