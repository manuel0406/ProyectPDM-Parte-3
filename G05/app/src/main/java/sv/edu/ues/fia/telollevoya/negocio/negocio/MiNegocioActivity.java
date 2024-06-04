package sv.edu.ues.fia.telollevoya.negocio.negocio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.pedidos.negocio.NegociosActivity;
import sv.edu.ues.fia.telollevoya.seguridad.IniciarSesionActivity;

public class MiNegocioActivity extends Activity {
    RecyclerView listaNegocios;
    ListaNegociosAdapter adapter;
    String idAdministrador;
    int idAdmin;
    private boolean primeraCarga = true; // Flag para controlar la primera carga
    ControlBD helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_negocio);

        Intent intent = getIntent();
        idAdministrador = intent.getStringExtra("idAdministrador");
        idAdmin = Integer.parseInt(idAdministrador);
        // idAdmin = 12; // Aseguramos que idAdmin tenga un valor, aunque este valor es estático

        listaNegocios = findViewById(R.id.rvListaNegocios);
        listaNegocios.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListaNegociosAdapter(new ArrayList<>());
        listaNegocios.setAdapter(adapter);

        cargarNegocios(); // Primera carga de datos
    }

    //----------------------------------------------------------------------------------------------
    //                          METODO PARA CERRAR SESION
    //----------------------------------------------------------------------------------------------
    public void cerrarSesion(View v) {
        // Limpio base de usuarios disponibles
        helper = new ControlBD(this);
        helper.abrir();
        helper.LimpiarUsuario();
        helper.cerrar();
        Toast.makeText(this, "Vuelve pronto", Toast.LENGTH_SHORT).show();

        // Crear un Intent para iniciar sesion "Iniciar Sesion"
        Intent intent = new Intent(this, IniciarSesionActivity.class);
        intent.putExtra("desdeInicioApp", false);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!primeraCarga) {
            cargarNegocios(); // Recargar datos al volver a la actividad
        }
        primeraCarga = false; // Marcar como falsa después de la primera carga
    }

    private void cargarNegocios() {
        Intent intent = getIntent();
        idAdministrador = intent.getStringExtra("idAdministrador");
        idAdmin = Integer.parseInt(idAdministrador);
        String url = "https://telollevoya.000webhostapp.com/Negocio/obtener_negocios_por_administrador.php?idAdministrador=" + idAdmin;
        new ObtenerRestaurantesTask(this).execute(url);
    }

    private class ObtenerRestaurantesTask extends AsyncTask<String, Void, ArrayList<Restaurant>> {
        private Context context;
        private boolean errorServidor = false; // Flag para detectar errores del servidor

        public ObtenerRestaurantesTask(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Restaurant> doInBackground(String... urls) {
            String url = urls[0];
            try {
                return ControladorSevicio.obtenerRestaurantesDesdeServicio(url, context);
            } catch (Exception e) {
                errorServidor = true; // Indicar que ocurrió un error
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> listaRestaurantes) {
            if (errorServidor) {
                Toast.makeText(context, "Error del servidor. La aplicación se cerrará.", Toast.LENGTH_LONG).show();
                finishAndRemoveTask(); // Cerrar la aplicación
                return;
            }

            if (listaRestaurantes != null && !listaRestaurantes.isEmpty()) {
                adapter.setNegocios(listaRestaurantes);
            } else {
                adapter.setNegocios(new ArrayList<>());
                if (!primeraCarga) { // Mostrar Snackbar solo después de la primera carga
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "No tienes negocios asociados. Añade un negocio para comenzar.", Snackbar.LENGTH_LONG)
                            .setDuration(4000) // Duración de 4 segundos
                            .setAction("Añadir", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    irNuevoNegocio(v);
                                }
                            }).show();
                }
            }
        }
    }

    public void irNuevoNegocio(View v) {
        //Intent intent = new Intent(this, NegociosActivity.class);
        Intent intent = new Intent(this, CrearNegocioActivity.class);
        intent.putExtra("idAdministradorRecuperado", idAdmin);
        startActivity(intent);
    }
}
