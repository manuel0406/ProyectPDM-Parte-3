package sv.edu.ues.fia.telollevoya.negocio.producto;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.R;

public class EditarProductoActivity extends AppCompatActivity {
    ControlBD helper;
    EditText editProductoNombre, editProductoPrecio, editProductoDescripción;
    Switch switchExistencias;
    RadioGroup radioGroupTipo;
    RadioButton rbBebida, rbAlimento;
    int id = 4;
    Product producto;
    private final String urlProducto = "https://telollevoya.000webhostapp.com/Producto/actualizarProducto.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Onda rara
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        helper = new ControlBD(EditarProductoActivity.this);
        editProductoNombre = (EditText) findViewById(R.id.editProductoNombre);
        editProductoPrecio = (EditText) findViewById(R.id.editProductoPrecio);
        editProductoPrecio.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        editProductoDescripción = (EditText) findViewById(R.id.editProductoDescripción);
        switchExistencias = (Switch) findViewById(R.id.switchExistencias);
        radioGroupTipo = (RadioGroup) findViewById(R.id.radioGroupTipo);
        rbBebida = (RadioButton) findViewById(R.id.rbBebida);
        rbAlimento = (RadioButton) findViewById(R.id.rbComida);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                id = Integer.parseInt(null);
            } else {
                id = extras.getInt("idProducto");
            }
        } else {
            id = (int) savedInstanceState.getSerializable("idProducto");
        }


        //Toast.makeText(this, "idproducto " + id, Toast.LENGTH_SHORT).show();

        String url = "https://telollevoya.000webhostapp.com/Producto/verProducto.php?idProducto=" + id;
        producto = ControladorSevicio.obtenerProducto(url, this);
        /*helper.abrir();
        producto = helper.verProducto(id);
        helper.cerrar();*/

        if (producto != null) {
            editProductoNombre.setText(producto.getNombreProducto());
            editProductoPrecio.setText(String.valueOf(producto.getPrecioProducto()));
            editProductoDescripción.setText(producto.getDescripcionProducto());
            switchExistencias.setChecked(producto.isExistenciaProducto());

            if ("Bebida".equals(producto.getTipoProducto()))
                radioGroupTipo.check(R.id.rbBebida);
            else if ("Alimento".equals(producto.getTipoProducto()))
                radioGroupTipo.check(R.id.rbComida);
        }
    }

    public void editarProducto(View v) throws UnsupportedEncodingException {
        // Verificar si los campos requeridos están vacíos
        if (editProductoNombre.getText().toString().isEmpty() || editProductoPrecio.getText().toString().isEmpty() || editProductoDescripción.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        //https://telollevoya.000webhostapp.com/Producto/actualizarProducto.php?idProducto=1&nombreProducto=Pacaya
        // &tipoProducto=Comida&descripcionProducto=Amarga&precioProducto=9.99&existenciaProducto=1


        // Obtener el texto del RadioButton seleccionado
        int selectedRadioButtonId = radioGroupTipo.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        String nombre = URLEncoder.encode(editProductoNombre.getText().toString(), "UTF-8");
        float precio = Float.parseFloat(editProductoPrecio.getText().toString());
        String descripcion = URLEncoder.encode(editProductoDescripción.getText().toString(), "UTF-8");
        //boolean existencia = switchExistencias.isChecked();
        int existencia = switchExistencias.isChecked() ? 1 : 0;
        String tipo = URLEncoder.encode(selectedRadioButton.getText().toString(), "UTF-8");

        //https://telollevoya.000webhostapp.com/Producto/actualizarProducto.php?idProducto=1&nombreProducto=Pacaya
        // &tipoProducto=Comida&descripcionProducto=Amarga&precioProducto=9.99&existenciaProducto=1
        StringBuilder urlNegocioBuilder = new StringBuilder();
        urlNegocioBuilder.append(urlProducto)
                .append("?idProducto=").append(id)
                .append("&nombreProducto=").append(nombre)
                .append("&tipoProducto=").append(tipo)
                .append("&descripcionProducto=").append(descripcion)
                .append("&precioProducto=").append(precio)
                .append("&existenciaProducto=").append(existencia);
        String urlProducto = urlNegocioBuilder.toString();
        ControladorSevicio.generico(urlProducto, this);
        onBackPressed();

        /*
        String nombre = editProductoNombre.getText().toString();
        float precio = Float.parseFloat(editProductoPrecio.getText().toString());
        String descripcion = editProductoDescripción.getText().toString();
        boolean existencia = switchExistencias.isChecked();

        // Obtener el texto del RadioButton seleccionado
        int selectedRadioButtonId = radioGroupTipo.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String tipo = selectedRadioButton.getText().toString();

        Product producto = new Product();
        producto.setIdProducto(id);
        producto.setNombreProducto(nombre);
        producto.setPrecioProducto(precio);
        producto.setDescripcionProducto(descripcion);
        producto.setExistenciaProducto(existencia);
        producto.setTipoProducto(tipo);

        helper.abrir();
        String mensaje = helper.actualizar(producto);
        helper.cerrar();
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        onBackPressed();*/
    }

    public void eliminarProducto(View v) {
        //try {

            String urlEliminar = "https://telollevoya.000webhostapp.com/Producto/eliminarProducto.php?idProducto=" + id;
            //producto = ControladorSevicio.obtenerProducto(url, this);
            ControladorSevicio.generico(urlEliminar, this);
            onBackPressed();
            // Navegación a MiNegocioActivity


            /*Intent intent = new Intent(this, ProductosActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.putExtra("idAdministrador", String.valueOf(idAdministradorRecuperado));
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(this, "Error al insertar el negocio: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }*/
        /*
        helper.abrir();
        String mensaje = helper.eliminarProducto(id);
        helper.cerrar();
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();*/
        //onBackPressed();
    }
}