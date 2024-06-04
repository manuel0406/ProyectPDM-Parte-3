package sv.edu.ues.fia.telollevoya.negocio.negocio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.*;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.negocio.producto.ProductosActivity;
import sv.edu.ues.fia.telollevoya.pedidos.negocio.PedidosPendientesActivity;

public class MiNegocioOpcionesActivity extends Activity {
    TextView texto1;
    String idNegocioRecuperado;
    int idNegocio = -1, idUbicacion,idAdmin;
    String name;
    ControlBD helper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_negocio_opciones);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        helper = new ControlBD(this);
        texto1 = findViewById(R.id.txtRaa);

        //Recuperamos id de Negocio
        Intent intent = getIntent();
        idNegocio = intent.getIntExtra("idNegocio", 5);
        idUbicacion = intent.getIntExtra("idUbicacion", 5);
        idAdmin = intent.getIntExtra("idAdministrador", 1);
        name = intent.getStringExtra("name");
        String negocioNombre = getResources().getString(R.string.negocio_nombre);
        texto1.setText(negocioNombre + " " + name);

        //Toast.makeText(this, "id Admin " + idAdmin, Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        String negocioNombre = getResources().getString(R.string.negocio_nombre);
        texto1.setText(negocioNombre + " " + name);
    }

    public void irActualizarNegocio(View v) {
        Intent intent = new Intent(this, EditarMiNegocioActivity.class);
        intent.putExtra("idNegocio", idNegocio);
        intent.putExtra("idUbicacion", idUbicacion);
        intent.putExtra("idAdministrador", idAdmin);
        startActivity(intent);
    }

    public void irMisProductos(View v) {
        Intent intent = new Intent(this, ProductosActivity.class);
        intent.putExtra("idNegocioRecuperado", idNegocio);
        startActivity(intent);
    }

    public void irMisPedidos(View v) {
        Intent intent = new Intent(this, PedidosPendientesActivity.class);
        intent.putExtra("idNegocioRecuperado", idNegocio);
        startActivity(intent);
    }

    public void eliminarNegocio(View v) {
        String urlUbicacion = "https://telollevoya.000webhostapp.com/Negocio/eliminarNegocio.php?idNegocio=" + idNegocio;
        ControladorSevicio.generico(urlUbicacion, this);
        onBackPressed();
    }

}