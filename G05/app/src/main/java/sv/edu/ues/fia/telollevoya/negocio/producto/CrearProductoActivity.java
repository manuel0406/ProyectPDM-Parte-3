package sv.edu.ues.fia.telollevoya.negocio.producto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.R;

public class CrearProductoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    EditText editProductoNombre, editProductoPrecio, editProductoDescripción;
    Switch switchExistencias;
    RadioGroup radioGroupTipo;
    ImageView imgProducto;
    Button btnSeleccionarImagen;
    Bitmap bitmap;
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

        editProductoNombre = findViewById(R.id.editProductoNombre);
        editProductoPrecio = findViewById(R.id.editProductoPrecio);
        editProductoDescripción = findViewById(R.id.editProductoDescripción);
        switchExistencias = findViewById(R.id.switchExistencias);
        radioGroupTipo = findViewById(R.id.radioGroupTipo);
        imgProducto = findViewById(R.id.imgProducto);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        editProductoPrecio.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        idNegocioRecuperado = getIntent().getIntExtra("idNegocioRecuperado", 5);

        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());
    }

    private void seleccionarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imgProducto.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void insertarProducto(View v) throws UnsupportedEncodingException {
        if (editProductoNombre.getText().toString().isEmpty() ||
                editProductoPrecio.getText().toString().isEmpty() ||
                editProductoDescripción.getText().toString().isEmpty() ||
                bitmap == null) {
            Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedRadioButtonId = radioGroupTipo.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        String nombre = editProductoNombre.getText().toString();
        float precio = Float.parseFloat(editProductoPrecio.getText().toString());
        String descripcion = editProductoDescripción.getText().toString();
        int existencia = switchExistencias.isChecked() ? 1 : 0;
        String tipo = selectedRadioButton.getText().toString();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imagenBase64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

        JSONObject productoJson = new JSONObject();
        try {
            productoJson.put("idNegocio", idNegocioRecuperado);
            productoJson.put("nombreProducto", nombre);
            productoJson.put("tipoProducto", tipo);
            productoJson.put("descripcionProducto", descripcion);
            productoJson.put("precioProducto", precio);
            productoJson.put("existenciaProducto", existencia);
            productoJson.put("imagenProducto", imagenBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonInputString = productoJson.toString();
        ControladorSevicio.generico2(urlProducto, jsonInputString, this);
        onBackPressed();
    }


}
