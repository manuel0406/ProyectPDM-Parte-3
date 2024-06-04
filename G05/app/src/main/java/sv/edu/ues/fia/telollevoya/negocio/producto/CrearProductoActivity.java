package sv.edu.ues.fia.telollevoya.negocio.producto;

import android.os.Bundle;
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

public class CrearProductoActivity extends AppCompatActivity {
    ControlBD helper;
    EditText editProductoNombre, editProductoPrecio, editProductoDescripción;
    Switch switchExistencias;
    RadioGroup radioGroupTipo;
    int idNegocioRecuperado;
    private final String urlProducto = "https://telollevoya.000webhostapp.com/Producto/insertarProducto.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = new ControlBD(this);
        editProductoNombre = (EditText) findViewById(R.id.editProductoNombre);
        editProductoPrecio = (EditText) findViewById(R.id.editProductoPrecio);
        editProductoDescripción = (EditText) findViewById(R.id.editProductoDescripción);
        switchExistencias = (Switch) findViewById(R.id.switchExistencias);
        radioGroupTipo = (RadioGroup) findViewById(R.id.radioGroupTipo);
        editProductoPrecio.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        idNegocioRecuperado = getIntent().getIntExtra("idNegocioRecuperado", 5);
        //Toast.makeText(this, "idNegocio " + idNegocioRecuperado, Toast.LENGTH_LONG).show();

    }

    public void insertarProducto(View v) throws UnsupportedEncodingException {
        // Verificar si los campos requeridos están vacíos
        if (editProductoNombre.getText().toString().isEmpty() || editProductoPrecio.getText().toString().isEmpty() || editProductoDescripción.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el texto del RadioButton seleccionado
        int selectedRadioButtonId = radioGroupTipo.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        String nombre = URLEncoder.encode(editProductoNombre.getText().toString(), "UTF-8");
        float precio =  Float.parseFloat(editProductoPrecio.getText().toString());
        String descripcion = URLEncoder.encode(editProductoDescripción.getText().toString(), "UTF-8");
        int existencia = switchExistencias.isChecked() ? 1 : 0;
        String tipo = URLEncoder.encode(selectedRadioButton.getText().toString(), "UTF-8");

        //Toast.makeText(this, "existencias " + existencia, Toast.LENGTH_LONG).show();

        StringBuilder urlNegocioBuilder = new StringBuilder();
        urlNegocioBuilder.append(urlProducto)
                .append("?idNegocio=").append(idNegocioRecuperado)
                .append("&nombreProducto=").append(nombre)
                .append("&tipoProducto=").append(tipo)
                .append("&descripcionProducto=").append(descripcion)
                .append("&precioProducto=").append(precio)
                .append("&existenciaProducto=").append(existencia);
        String urlProducto = urlNegocioBuilder.toString();
        ControladorSevicio.generico(urlProducto, this);
        onBackPressed();
    }
}