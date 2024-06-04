package sv.edu.ues.fia.telollevoya.seguridad;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.*;

public class rolRegistrarseActivity extends AppCompatActivity {


    //Enlaces de insersion.
    private final String urlInsertarCLiente = "https://telollevoya.000webhostapp.com/Seguridad/cliente_insertar.php";
    private final String urlInsertarAdministrador = "https://telollevoya.000webhostapp.com/Seguridad/administrador_insertar.php";
    private final String urlInsertarRepartidor = "https://telollevoya.000webhostapp.com/Seguridad/repartidor_insertar.php";

    //Variables recuperadas
    String correo, nombre, apellido, contra, sexo, nacimiento;
    ImageView imgCliente, imgAdministrador, imgRepartidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rol_registrarse);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Obtenemos la imagen
        imgCliente = findViewById(R.id.imgCliente);
        imgAdministrador = findViewById(R.id.imgAdministrador);
        imgRepartidor = findViewById(R.id.imgRepartidor);

        //Recupero informacion
        Intent intent = getIntent();
        correo = intent.getStringExtra("correo");
        nombre = intent.getStringExtra("nombre");
        apellido = intent.getStringExtra("apellido");
        contra = intent.getStringExtra("contraseña");
        sexo = intent.getStringExtra("sexo");
        nacimiento = intent.getStringExtra("nacimiento");


        //Transformacion de apellidos o nombre con espacios
        try {
            nombre = URLEncoder.encode(nombre,"UTF-8");
            apellido = URLEncoder.encode(apellido,"UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    //Se asigna el rol cliente --------------------------------------------------------------------
    public void soyCliente(View v) {
        String url, result = "", idCliente= "";
        JSONObject jsonExtraido;
        url = urlInsertarCLiente + "?NOMBRECLIENTE=" + nombre + "&CORREOCLIENTE=" + correo + "&CONTRACLIENTE=" + contra + "&APELLIDOSCLIENTE=" + apellido + "&SEXOCLIENTE=" + sexo + "&FECHANACIMIENTOC="+ nacimiento;
        jsonExtraido = ControladorSevicio.insertarCliente(url, this);

        try {
            Thread.sleep(5000); // Esperar 5 segundos (5000 milisegundos)
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Obtenenos el resultado
        try {
            idCliente = jsonExtraido.getString("idCliente");
            result = jsonExtraido.getString("resultado");

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error procesando la respuesta del servidor", Toast.LENGTH_LONG).show();
        }

        Intent intent;
        if ("Cliente guardado con exito".equals(result)){

            intent = new Intent(this, registrarUbicacionActivity.class);
            intent.putExtra("idCliente", idCliente);
            startActivity(intent);
            finish();
        }
    }

    //Se asigna el rol administrador-----------------------------------------------------
    public void soyAdministrador(View v){
        String url, resul;
        url = urlInsertarAdministrador + "?NOMBREADMIN=" + nombre + "&CORREOADMINISTRADOR=" + correo + "&CONTRAADMINISTRADOR=" + contra + "&APELLIDOSADMIN=" + apellido + "&SEXOADMINISTRADOR=" + sexo + "&FECHANACIMIENTO="+ nacimiento;
        resul = ControladorSevicio.insertarUsuario(url, this);

        try {
            Thread.sleep(5000); // Esperar 5 segundos (5000 milisegundos)
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Intent intent;
        if ("Administrador guardado con exito".equals(resul)){

            intent = new Intent(this, IniciarSesionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("desdeInicioApp", false);
            startActivity(intent);
            finish();
        }

    }

    //Se asigna el rol repartidor ---------------------------------------------------------------
    public void soyRepartidor(View v){
        String url, resul;
        url = urlInsertarRepartidor + "?NOMBREREPARTIDOR=" + nombre + "&CORREOREPARTIDOR=" + correo + "&CONTRAREPARTIDOR=" + contra + "&APELLIDOREPARTIDOR=" + apellido + "&SEXOREPARTIDOR=" + sexo + "&FECHANACIMIENTOR=" + nacimiento;
        resul = ControladorSevicio.insertarUsuario(url, this);

        try {
            Thread.sleep(5000); // Esperar 5 segundos (5000 milisegundos)
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Intent intent;
        if ("Repartidor guardado con éxito".equals(resul)){

            intent = new Intent(this, IniciarSesionActivity.class);
            intent.putExtra("desdeInicioApp", false);
            startActivity(intent);
            finish();
        }
    }
}