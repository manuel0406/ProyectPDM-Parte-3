package sv.edu.ues.fia.telollevoya.Reservaciones;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Reservacion;
@SuppressLint("NewApi")
public class ReservacionesConsultarActivity extends Activity {
    List<Reservacion> listaReservaciones;
    RecyclerView recyclerView;
    ArrayList<Reservacion> reservacion;
    ControlBD db;
    MyAdapter adapter;
    int idCliente;
    int idNegocio;
    String urlReservaciones="https://telollevoya.000webhostapp.com/Reservaciones/reservacion_query.php";
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservaciones_consultar);
        Intent intent= getIntent();
        idNegocio = intent.getIntExtra("idNegocio", 5);


//        intent = new Intent(ReservacionesConsultarActivity.this, ReservacionActualizarActivity.class);
//        intent.putExtra("idNegocio", idNegocio);
       // idCliente="1";

        db= new ControlBD(this);


                StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listaReservaciones = new ArrayList<Reservacion>();

        recyclerView= findViewById(R.id.recycolerView);
        reservacion = obtenerReservaciones();
        adapter = new MyAdapter(this, reservacion);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        displaydata();


    }
    private ArrayList<Reservacion> obtenerReservaciones() {
        //obtengo el id del cliente
        db.abrir();
        idCliente= db.consultaUsuario();
        db.cerrar();
       String url = urlReservaciones + "?IDCLIENTE="+ idCliente+ "&IDNEGOCIO="+idNegocio;
        String reservacion = ControladorSevicio.obtenerRepuestaPeticion(url,this);

        Log.v("UrlConsulta",url);
        Log.v("UrlConsulta",reservacion);

        try {
            listaReservaciones.addAll(ControladorSevicio.obtenerReservaciones(reservacion, this));
            //actualizarListView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Reservacion> reservaciones = new ArrayList<>();
//        db.abrir();
//        reservaciones=db.consultaReservacion();
//        db.cerrar();
       /*
        reservaciones.add(new Reservacion(1, "Negocio 1", " 12/02/24"));
        reservaciones.add(new Reservacion(2, "Negocio 2", ""));
        reservaciones.add(new Reservacion(3, "Negocio 3", " "));
        */
        return (ArrayList<Reservacion>) listaReservaciones;
    }
    private void displaydata(){
        //Cursor cursor= db.query();
    }

    public void cambiarPantalla(View v){
//        Toast.makeText(this, idCliente,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ReservacionesConsultarActivity.this, ReservacionInsertarActivity.class);
        intent.putExtra("idNegocio", idNegocio);
        startActivity(intent);
    }
}