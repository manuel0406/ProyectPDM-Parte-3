package sv.edu.ues.fia.telollevoya.seguridad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.negocio.negocio.MiNegocioActivity;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Usuario;
import sv.edu.ues.fia.telollevoya.pedidos.negocio.NegociosActivity;
import sv.edu.ues.fia.telollevoya.pedidos.negocio.PedidosDelRepartidorActivity;

public class IniciarSesionActivity extends Activity {

    private final String urlConsultarCLiente = "https://telollevoya.000webhostapp.com/Seguridad/cliente_consulta.php";
    private final String urlConsultarAdministrador = "https://telollevoya.000webhostapp.com/Seguridad/administrador_consulta.php";
    private final String urlConsultarRepartidor = "https://telollevoya.000webhostapp.com/Seguridad/repartidor_consulta.php";

    ControlBD helper;
    EditText txtCorreo, txtContra;
    boolean  desdeInicioApp;
    String contra,correo,rol,idUsuario;

    ControlBD BDhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        desdeInicioApp = getIntent().getBooleanExtra("desdeInicioApp", true);

        txtCorreo = findViewById(R.id.txtCorreo);
        txtContra = findViewById(R.id.txtContra);

        BDhelper= new ControlBD(this);
        try{
            Class<?> clase=Class.forName("sv.edu.ues.fia.telollevoya");
            Intent inte = new Intent(this,clase);
            this.startActivity(inte);

        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }

        BDhelper.abrir();
        String tost=BDhelper.llenarBD();
        BDhelper.cerrar();

        if(tost.equals("Guardado correctamente"))
        {
            tost = "¡Bienvenido!";
        }
        else{
            tost = "¡Error en BD!";
        }

        //Validamos si es por inicio de aplicacion
        if(desdeInicioApp)
        {
            Toast.makeText(this, tost, Toast.LENGTH_SHORT).show();
        }

        ultimoInicio();
    }

    //----------------------------------------------------------------------------------------------
    // METODO PARA SI HAY ULTIMO INICIO DE SESION
    //----------------------------------------------------------------------------------------------

    public void ultimoInicio(){
        Usuario usuario;

        helper = new ControlBD(this);
        helper.abrir();
        usuario = helper.consultarUsuarioActivo("Activo");
        helper.cerrar();

        if (usuario != null) {
            if ("Cliente".equals(usuario.getRol())) {
                Intent intent = new Intent(this, NegociosActivity.class);
                startActivity(intent);
                finish();
            } else if ("Administrador".equals(usuario.getRol())) {
                Intent intent = new Intent(this, MiNegocioActivity.class);
                // Enviar información
                intent.putExtra("idAdministrador", usuario.getIdUsuario());
                startActivity(intent);
                finish();
            } else if ("Repartidor".equals(usuario.getRol())) {
                // Código para la actividad de Repartidor
                Intent intent = new Intent(this, PedidosDelRepartidorActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // METODO PARA REGISTRARSE YA SEA COMO ADMINISTRADOR, CLIENTE O REPARTIDOR
    //----------------------------------------------------------------------------------------------
    public void irRegistrarse(View v){
        // Crear un Intent para iniciar la actividad "RegistrarseActivity"
        Intent intent = new Intent(this, RegistrarseActivity.class);
        startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------
    // METODO PARA INICIAR SESION YA SEA COMO ADMINISTRADOR, CLIENTE O REPARTIDOR
    //----------------------------------------------------------------------------------------------
    public void irRolIniciarSesion(View v){
        //Obtener informacion de los campos
        int banderaCorreo = 0, banderaContra=0;
        correo = txtCorreo.getText().toString().trim();
        contra = txtContra.getText().toString().trim();

        // REGEX para validar correo
        String patronCorreo = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        // Compilar el patrón en un objeto Pattern
        Pattern pattern = Pattern.compile(patronCorreo);// Crear un objeto Matcher para aplicar el patrón al correo
        Matcher matcher = pattern.matcher(correo);

        // Validar si el correo cumple con el patrón ---------------------------------
        if (matcher.matches()) {
            banderaCorreo = 1;
        } else {
            txtCorreo.setText("");
            txtCorreo.setHintTextColor(ContextCompat.getColor(this, R.color.error)); // Cambia el color del hint a rojo
            txtCorreo.setHint("¡Correo invalido!"); // Cambia el texto del hint a "FALTA CORREO"
        }

        //Validamos compos de contraseño----------------------------
        if (!contra.isEmpty()) {
            banderaContra = 1;
        } else {
            txtContra.setHintTextColor(ContextCompat.getColor(this, R.color.error)); // Cambia el color del hint a rojo
            txtContra.setHint("¡Contraseña invalido!");
        }

        //VALIDAMOS SI DAMOS EL PASE----------------------------
        if (banderaCorreo==1 && banderaContra==1) {
            seleccionarInicio();
        }
    }

    //----------------------------------------------------------------------------------------------
    // METODO PARA BUSCAR ROL DE INICIAR SESION
    //----------------------------------------------------------------------------------------------
    public void seleccionarInicio(){
        Toast.makeText(this,"Estamos buscando tu usuario...",Toast.LENGTH_LONG).show();
        //Variables
        String urlC,urlA, urlR, contraJSON, correoJSON;

        //Peticion a BD 000WebHost
        urlC = urlConsultarCLiente + "?CORREOCLIENTE=" + correo + "&CONTRACLIENTE=" + contra;
        urlA = urlConsultarAdministrador + "?CORREOADMINISTRADOR=" + correo + "&CONTRAADMINISTRADOR=" + contra;
        urlR = urlConsultarRepartidor + "?CORREOREPARTIDOR=" + correo + "&CONTRAREPARTIDOR=" + contra;
        String jsonCliente = ControladorSevicio.obtenerRespuestaPeticion(urlC,this);
        String jsonAdministrador = ControladorSevicio.obtenerRespuestaPeticion(urlA,this);
        String jsonRepartidor = ControladorSevicio.obtenerRespuestaPeticion(urlR,this);

        try {
            Thread.sleep(5000); // Esperar 5 segundos (5000 milisegundos)
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Obtenenos el resultado
        try {
            // Convertir la respuesta en un JSONArray
            JSONArray jsonArrayC = new JSONArray(jsonCliente);
            JSONArray jsonArrayA = new JSONArray(jsonAdministrador);
            JSONArray jsonArrayR = new JSONArray(jsonRepartidor);

            // Obtener el primer Cliente
            if (jsonArrayC.length() > 0) {
                JSONObject respuesta = jsonArrayC.getJSONObject(0);

                // Extraer los valores del JSONObject
                correoJSON = respuesta.getString("CORREOCLIENTE");
                contraJSON = respuesta.getString("CONTRACLIENTE");
                idUsuario = respuesta.getString("IDCLIENTE");
                rol = "Cliente";

                // Verificar si el correo es correcto
                if (correoJSON.equalsIgnoreCase(correo) && contraJSON.equalsIgnoreCase(contra)) {
                    Toast.makeText(this, "Sus credenciales como cliente son correctas", Toast.LENGTH_LONG).show();
                    int bandera = consultarUsuario();

                    if (bandera==0)
                    {
                        insertarUsuario();
                    }
                    else
                    {
                        switch (rol) {
                            case "Cliente":
                                Toast.makeText(this, "Ya puede empezar a pedir", Toast.LENGTH_LONG).show();
                                break;
                            case "Administrador":
                                Toast.makeText(this, "Ya puede empezar a gestionar tu negocio", Toast.LENGTH_LONG).show();
                                break;
                            case "Repartidor":
                                Toast.makeText(this, "Ya puede empezar a repartir", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    Intent intent = new Intent(this, NegociosActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Sus credenciales como cliente son incorrectas", Toast.LENGTH_LONG).show();
                }
            }
            // Obtener el primer Administrador
            else if (jsonArrayA.length() > 0) {
                JSONObject respuesta = jsonArrayA.getJSONObject(0);

                // Extraer los valores del JSONObject
                correoJSON = respuesta.getString("CORREOADMINISTRADOR");
                contraJSON = respuesta.getString("CONTRAADMINISTRADOR");
                idUsuario = respuesta.getString("IDADMINISTRADOR");
                rol = "Administrador";

                // Verificar si el correo es correcto
                if (correoJSON.equalsIgnoreCase(correo) && contraJSON.equalsIgnoreCase(contra)) {
                    Toast.makeText(this, "Sus credenciales como administrador son correctas", Toast.LENGTH_LONG).show();
                    int bandera = consultarUsuario();

                    if (bandera==0)
                    {
                        insertarUsuario();
                    }
                    else
                    {
                        switch (rol) {
                            case "Cliente":
                                Toast.makeText(this, "Ya puede empezar a pedir", Toast.LENGTH_LONG).show();
                                break;
                            case "Administrador":
                                Toast.makeText(this, "Ya puede empezar a gestionar tu negocio", Toast.LENGTH_LONG).show();
                                break;
                            case "Repartidor":
                                Toast.makeText(this, "Ya puede empezar a repartir", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    Intent intent = new Intent(this, MiNegocioActivity.class);
                    // Envio Informacion
                    intent.putExtra("idAdministrador", idUsuario);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Sus credenciales como administrador son incorrectas", Toast.LENGTH_LONG).show();
                }
            }
            // Obtener el primer Repartidor
            else if (jsonArrayR.length() > 0) {
                JSONObject respuesta = jsonArrayR.getJSONObject(0);

                // Extraer los valores del JSONObject
                correoJSON = respuesta.getString("CORREOREPARTIDOR");
                contraJSON = respuesta.getString("CONTRAREPARTIDOR");
                idUsuario = respuesta.getString("IDREPARTIDOR");
                rol = "Repartidor";

                // Verificar si el correo es correcto
                if (correoJSON.equalsIgnoreCase(correo) && contraJSON.equalsIgnoreCase(contra)) {
                    Toast.makeText(this, "Sus credenciales como repartidor son correctas", Toast.LENGTH_LONG).show();
                    int bandera = consultarUsuario();

                    if (bandera==0)
                    {
                        insertarUsuario();
                    }
                    else
                    {
                        switch (rol) {
                            case "Cliente":
                                Toast.makeText(this, "Ya puede empezar a pedir", Toast.LENGTH_LONG).show();
                                break;
                            case "Administrador":
                                Toast.makeText(this, "Ya puede empezar a gestionar tu negocio", Toast.LENGTH_LONG).show();
                                break;
                            case "Repartidor":
                                Toast.makeText(this, "Ya puede empezar a repartir", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                    Intent intent = new Intent(this, PedidosDelRepartidorActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Sus credenciales como repartidor son incorrectas", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(this, "Sus credenciales no existen", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Error al analizar JSON:", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, "Error al procesar la respuesta del servidor", Toast.LENGTH_LONG).show();
        }

    }

    //----------------------------------------------------------------------------------------------
    // METODO PARA INSERTAR CLIENTE EN SQLITE
    //----------------------------------------------------------------------------------------------
    public void insertarUsuario(){
        Usuario usuario = new Usuario();
        usuario.setClave(contra);
        usuario.setIdUsuario(idUsuario);
        usuario.setNombreUsuario(correo);
        usuario.setRol(rol);
        usuario.setEstado("Activo");

        helper = new ControlBD(this);
        helper.abrir();
        String idBase = helper.insertar(usuario);
        helper.cerrar();

        switch (rol) {
            case "Cliente":
                Toast.makeText(this, "Ya puede empezar a pedir", Toast.LENGTH_LONG).show();
                break;
            case "Administrador":
                Toast.makeText(this, "Ya puede empezar a gestionar tu negocio", Toast.LENGTH_LONG).show();
                break;
            case "Repartidor":
                Toast.makeText(this, "Ya puede empezar a repartir", Toast.LENGTH_LONG).show();
                break;
        }

    }

    public int consultarUsuario(){
        Usuario usuario;

        helper = new ControlBD(this);
        helper.abrir();
        usuario = helper.consultarUsuario(correo);
        helper.cerrar();

        if (usuario != null){
            return 1;
        }

        return 0;
    }

}
