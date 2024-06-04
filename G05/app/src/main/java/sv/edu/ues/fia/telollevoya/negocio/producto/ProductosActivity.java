package sv.edu.ues.fia.telollevoya.negocio.producto;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.R;

public class ProductosActivity extends AppCompatActivity {
    RecyclerView listaProductos;
    ListaProductosAdaptader adapter;
    ControlBD helper;
    int idNegocioRecuperado;
    boolean firstLoad = true; // Variable para controlar la primera carga

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listaProductos = findViewById(R.id.rvListaProductos);
        listaProductos.setLayoutManager(new LinearLayoutManager(this));

        idNegocioRecuperado = getIntent().getIntExtra("idNegocioRecuperado", 5);

        adapter = new ListaProductosAdaptader(new ArrayList<>());
        listaProductos.setAdapter(adapter);

        cargarProductos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstLoad) {
            cargarProductos();
        }
        firstLoad = false; // Cambiar bandera después de la primera carga
    }

    private void cargarProductos() {
        String url = "https://telollevoya.000webhostapp.com/Producto/obtenerProductosPorIdNegocio.php?idNegocio=" + idNegocioRecuperado;
        new ObtenerProductosTask(this).execute(url);
    }

    private class ObtenerProductosTask extends AsyncTask<String, Void, ArrayList<Product>> {
        private Context context;

        public ObtenerProductosTask(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Product> doInBackground(String... urls) {
            String url = urls[0];
            return ControladorSevicio.obtenerProductosPorIdNegocio(url, context);
        }

        @Override
        protected void onPostExecute(ArrayList<Product> productos) {
            if (productos != null && !productos.isEmpty()) {
                adapter.setProductos(productos);
            } else {
                adapter.setProductos(new ArrayList<>());
                if (!firstLoad) { // Mostrar el Toast solo si no es la primera carga
                    //Toast.makeText(context, "No tienes ningun producto", Toast.LENGTH_LONG).show();
                    View rootView = findViewById(android.R.id.content);

                    Snackbar.make(rootView, "No tienes productos en este negocio. Añade un producto para comenzar.", Snackbar.LENGTH_LONG)
                            .setDuration(4000) // Duración de 3 segundos en milisegundos
                            .setAction("Añadir", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    irNuevoProducto(v);
                                    // Acción a realizar cuando se pulsa el botón "Añadir"
                                }
                            }).show();
                }
            }
        }
    }

    public void irNuevoProducto(View v) {
        Intent intent = new Intent(this, CrearProductoActivity.class);
        intent.putExtra("idNegocioRecuperado", idNegocioRecuperado);
        startActivity(intent);
    }
}
