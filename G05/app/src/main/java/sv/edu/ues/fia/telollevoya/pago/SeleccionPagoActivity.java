package sv.edu.ues.fia.telollevoya.pago;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.Factura;
import sv.edu.ues.fia.telollevoya.Pedido;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Reservacion;
import sv.edu.ues.fia.telollevoya.Reservaciones.DetallePedidoR;
import sv.edu.ues.fia.telollevoya.pedidos.cliente.MisPedidosActivity;

public class SeleccionPagoActivity extends AppCompatActivity {

    // Variables de instancia
    private EditText numeroTarjeta;
    private EditText fechaExpiracion;
    private EditText cvc;
    private EditText nombreTitular;
    private EditText correo;
    private RadioGroup radioGroupMetodoPago;
    private RadioButton rbEfectivo;
    private RadioButton rbTarjeta;
    private RadioButton rbBitcoin;
    private ConstraintLayout layoutFormularioTarjetaCredito;
    private ConstraintLayout layoutFormularioBitcoin;
    private ConstraintLayout layoutFormularioCorreo;
    private ControlBD controlBD;
    private MetodoPago metodoPago;
    private boolean tipoPagoSeleccionado;
    private ImageView codigoQR;
    private TextView txtDireccionBitcoin;
    private ImageView imgCopiarDireccion;
    Pedido pedido;
    Reservacion reservacion;
    ArrayList<DetallePedidoR> detallesPedidoR;
    Factura factura;
    private String urlFactura = "https://telollevoya.000webhostapp.com/Pago/insertar_factura.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_seleccion);

        // Inicialización de variables
        controlBD = new ControlBD(SeleccionPagoActivity.this);
        radioGroupMetodoPago = findViewById(R.id.radioGroupMetodoPago);
        rbEfectivo = findViewById(R.id.radioButtonEfectivo);
        rbTarjeta = findViewById(R.id.radioButtonTarjeta);
        rbBitcoin = findViewById(R.id.radioButtonBitcoin);
        layoutFormularioTarjetaCredito = findViewById(R.id.layoutFormularioTarjetaCredito);
        layoutFormularioTarjetaCredito.setVisibility(View.GONE);
        layoutFormularioBitcoin = findViewById(R.id.layoutFormularioBitcoin);
        layoutFormularioBitcoin.setVisibility(View.GONE);
        layoutFormularioCorreo = findViewById(R.id.layoutFormularioCorreo);
        codigoQR = findViewById(R.id.imgCodigoQR);
        txtDireccionBitcoin = findViewById(R.id.txtDireccionBitcoin);
        imgCopiarDireccion = findViewById(R.id.imgCopiarDireccion);
        tipoPagoSeleccionado = false;
        metodoPago = new MetodoPago();
        factura = new Factura();

        codigoQR.setImageResource(R.drawable.codigo_qr);

        if (getIntent().getExtras() != null) {
            pedido = (Pedido) getIntent().getExtras().getSerializable("pedido");
            reservacion = (Reservacion) getIntent().getExtras().getSerializable("reservacion");
            detallesPedidoR = getIntent().getParcelableArrayListExtra("listaDetalle");
        }

        if (pedido != null) {
            float totalAPagar = pedido.getTotalAPagar() + pedido.getCostoEnvio();
            pedido.setTotalAPagar(totalAPagar);
        }

        // Configuración de listeners y otros componentes
        radioGroupMetodoPago.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                layoutFormularioTarjetaCredito.setVisibility(View.GONE);
                layoutFormularioBitcoin.setVisibility(View.GONE);

                if (checkedId == R.id.radioButtonEfectivo) {
                    metodoPago.setId(1);
                    metodoPago.setTipoPago(rbEfectivo.getText().toString());
                } else if (checkedId == R.id.radioButtonTarjeta) {
                    layoutFormularioTarjetaCredito.setVisibility(View.VISIBLE);
                    metodoPago.setId(2);
                    metodoPago.setTipoPago(rbTarjeta.getText().toString());
                } else if (checkedId == R.id.radioButtonBitcoin) {
                    layoutFormularioBitcoin.setVisibility(View.VISIBLE);
                    metodoPago.setId(3);
                    metodoPago.setTipoPago(rbBitcoin.getText().toString());
                }

                tipoPagoSeleccionado = true; // Ya se seleccionó un tipo de pago
            }
        });

        numeroTarjeta = findViewById(R.id.editNumeroTarjeta);
        fechaExpiracion = findViewById(R.id.editFechaExpiracion);
        cvc = findViewById(R.id.editCVC);
        nombreTitular = findViewById(R.id.editNombreTitular);
        correo = findViewById(R.id.editCorreo);

        // Manejo de formatos para el campo de número de tarjeta de crédito o débito
        numeroTarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!TextUtils.isEmpty(input)) {
                    // Eliminar guiones actuales para evitar duplicados
                    String cleanString = input.replace("-", "");
                    // Agregar guiones cada 4 dígitos
                    StringBuilder formattedString = new StringBuilder();
                    int index = 0;
                    while (index < cleanString.length()) {
                        formattedString.append(cleanString, index, Math.min(index + 4, cleanString.length()));
                        if (index + 4 < cleanString.length()) {
                            formattedString.append("-");
                        }
                        index += 4;
                    }
                    // Actualizar el texto del EditText
                    numeroTarjeta.removeTextChangedListener(this);
                    numeroTarjeta.setText(formattedString.toString());
                    numeroTarjeta.setSelection(formattedString.length());
                    numeroTarjeta.addTextChangedListener(this);
                }
            }
        });

        // Manejo de formato para el campo de fecha de expiración de la tarjeta de crédito/débito
        fechaExpiracion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!TextUtils.isEmpty(input) && input.length() == 2 && !input.contains("/")) {
                    // Si el usuario ingresa los dos primeros dígitos del mes, automáticamente agrega una barra ("/")
                    fechaExpiracion.setText(input + "/");
                    fechaExpiracion.setSelection(fechaExpiracion.getText().length());
                }
            }
        });
    }

    public void procesarPago(View view) {
        String numeroTarjetaStr = numeroTarjeta.getText().toString();
        String fechaExpiracionStr = fechaExpiracion.getText().toString();
        String cvcStr = cvc.getText().toString();
        String nombreTitularStr = nombreTitular.getText().toString();
        String correoStr = correo.getText().toString();

        boolean datosValidos = validarDatos(numeroTarjetaStr, fechaExpiracionStr, cvcStr, nombreTitularStr);

        if (!tipoPagoSeleccionado) {
            Toast.makeText(this, "Debe seleccionar un tipo de pago", Toast.LENGTH_SHORT).show();
        } else if (!datosValidos) {
            Toast.makeText(this, "Error, datos de la tarjeta no son válidos", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(correoStr)) {
            Toast.makeText(this, "Error, ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
        } else {

            if (pedido != null) { //si se trata de un pedido

                //generarFactura(); // Insertar factura en BD SQLite
                insertarFactura(view); //Insertar factura usando Web Service

                String correoElectronico = correoStr;

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE); // Recuperar el ID de la factura de SharedPreferences
                int lastFacturaId = sharedPreferences.getInt("lastFacturaId", -1);
                factura.setId(lastFacturaId);

                pedido.setFactura(factura);//se le asigna la factura al pedido
                Intent intent = new Intent(this, PagoAprobadoPActivity.class);
                intent.putExtra("CORREO_ELECTRONICO", correoElectronico);
                intent.putExtra("pedido", pedido);
                startActivity(intent);
                finish();
//                Bundle extra = new Bundle();
//                extra.putSerializable("reservacion", reservacion);
//                intent.putParcelableArrayListExtra("listaDetalle",  listDetalle);
//                intent.putExtra("idNegocio", idNegocio);
//                intent.putExtras(extra);

            } else {
                //Entonces se trata de una reservacion
                String correoElectronico = correoStr;
                Intent intent = new Intent(this, PagoAprobadoRActivity.class);
                intent.putExtra("reservacion", reservacion);
                intent.putExtra("CORREO_ELECTRONICO", correoElectronico);
                intent.putParcelableArrayListExtra("listaDetalle", detallesPedidoR);
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean validarDatos(String numeroTarjeta, String fechaExpiracion, String cvc, String nombreTitular) {
        if (layoutFormularioTarjetaCredito.getVisibility() == View.GONE) {
            return true; // No se requiere validación para efectivo
        } else {
            // Si la tarjeta de crédito está seleccionada, se validan los campos relacionados
            return !numeroTarjeta.isEmpty() && !fechaExpiracion.isEmpty() && !cvc.isEmpty() && !nombreTitular.isEmpty() && numeroTarjeta.length() == 19 && cvc.length() == 3 && fechaExpiracion.length() == 5;
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

//    private void generarFactura() { // Generar factura para insertar en BD SQLite
//        factura.setMetodoPago(metodoPago);
//        factura.setTotalPagado(pedido.getTotalAPagar());
//        factura.setFechaEmision(pedido.getFechaEntrega());
//
//        pedido.setFactura(factura);
//        controlBD.abrir();
//        controlBD.insertar(factura, pedido);
//        controlBD.cerrar();
//    }

    public void copiarDireccionBitcoin(View view) {
        String direccionBitcoin = txtDireccionBitcoin.getText().toString().split(": ")[1];
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Dirección Bitcoin", direccionBitcoin);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Dirección Bitcoin copiada al portapapeles", Toast.LENGTH_SHORT).show();
    }

    public void insertarFactura(View v) {

        String fechaEmision = "";
        Date fechaActual = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        fechaEmision = dateFormat.format(fechaActual);

        if (metodoPago.getId() == 2) { //si el tipo de pago es tarjeta
            String numeroTarjetaStr = numeroTarjeta.getText().toString();
            String ultimosCuatroDigitos = numeroTarjetaStr.substring(numeroTarjetaStr.length() - 4);
            metodoPago.setTipoPago(metodoPago.getTipoPago() + " " + ultimosCuatroDigitos);
        }
        factura.setMetodoPago(metodoPago);
        factura.setFechaEmision(fechaActual);
        factura.setTotalPagado(pedido.getTotalAPagar());

        String idPago = String.valueOf(factura.getMetodoPago().getId());
        String totalPagado = String.valueOf(factura.getTotalPagado());

        String idPagoCodificado = "";
        String totalPagadoCodificado = "";
        String fechaEmisionCodificada = "";

        try {
            idPagoCodificado = URLEncoder.encode(idPago, "UTF-8");
            totalPagadoCodificado = URLEncoder.encode(totalPagado, "UTF-8");
            fechaEmisionCodificada = URLEncoder.encode(fechaEmision, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = urlFactura + "?idPago=" + idPagoCodificado + "&totalPagado=" + totalPagadoCodificado + "&fechaEmision=" + fechaEmisionCodificada;
        Log.v("URL INSERTAR FACTURA: ", url);
        ControladorSevicio.insertarFactura(url, this);

    }

    public void regresarACarrito(View view) {
        finish(); //regresamos a pantalla anterior
    }


}


