package sv.edu.ues.fia.telollevoya.Reservaciones;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;

import sv.edu.ues.fia.telollevoya.Producto;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.ReminderReceiver;
import sv.edu.ues.fia.telollevoya.Reservacion;
import sv.edu.ues.fia.telollevoya.pago.SeleccionPagoActivity;

@SuppressLint("NewApi")
public class ReservacionInsertarActivity extends AppCompatActivity implements ControladorSevicio.ReservacionInsertListener {

    private LinearLayout contenedorElementos;
    ArrayList<String> listaSpinner= new ArrayList<>();
    ArrayList<Integer> listaCantidad= new ArrayList<Integer>();
    ArrayList<DetallePedidoR> listDetalle = new ArrayList<>();
    Reservacion reservacion;
    ControlBD helper;
   // private int contadorElementos=0;
   List<Producto> listaproductos;
    EditText edtFecha;
    EditText  edtTotal;
    EditText edtPagoMinimo;
    EditText edtDescripcion;
    EditText edtAnticipo;
    EditText edtHora;
    Button btnPagar;
    double total, precio,cantidad;
    double pagoMinimo=0;
    private  int dia, mes, ano, hora, minutos;
    int idCliente;
    int idNegocio;
    ScrollView scrollView;
    private int contadorElementos = 0;
    int idReservacionR;
    private String urlReservacion="https://telollevoya.000webhostapp.com/Reservaciones/reservacion_insert.php";
    private String urldetalle="https://telollevoya.000webhostapp.com/Reservaciones/reservacion_detalle_insertar.php";
    private  String urlProducto="https://telollevoya.000webhostapp.com/Reservaciones/productos_query.php";
    @Override
    public void onReservacionInserted(int idReservacion) {

        idReservacionR = idReservacion;
        Log.v("idReservacionInt", String.valueOf(idReservacionR));

        //insertarDetallePedido(idReservacionR);


    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_reservacion_insertar);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        listaproductos= new ArrayList<Producto>();

       reservacion = new Reservacion();
        contenedorElementos = findViewById(R.id.contenedorElementos);
        FloatingActionButton agregarBoton = findViewById(R.id.floatingActionButton);
        scrollView = findViewById(R.id.scrollView2);

        helper= new ControlBD(this);
        edtFecha= (EditText) findViewById(R.id.editTextDateReservacion);
        edtHora=(EditText) findViewById(R.id.edtHora);
        edtTotal = (EditText) findViewById(R.id.editTextTotal);
        edtPagoMinimo= (EditText) findViewById(R.id.editTextPagoMinimo);
        edtDescripcion =(EditText) findViewById(R.id.editTextTextMultiLine);
        edtAnticipo= (EditText) findViewById(R.id.editTextAnticipo);


        helper.abrir();
        idCliente= helper.consultaUsuario();
        helper.cerrar();
        Toast.makeText(this, String.valueOf(idCliente),Toast.LENGTH_SHORT).show();
        Intent intent= getIntent();
        idNegocio = intent.getIntExtra("idNegocio", 5);



        agregarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                agregarElementos();
            }
        });

    }


    public void fecha(View v) {

        if (v==edtFecha){
            final Calendar c=Calendar.getInstance();
            dia=c.get(Calendar.DAY_OF_MONTH);
            mes=c.get(Calendar.MONTH);
            ano=c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog;
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    edtFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

                }

            }, ano, mes, dia);
            datePickerDialog.show();;

        }
        if (v== edtHora){
            final Calendar c= Calendar.getInstance();
            hora= c.get(Calendar.HOUR_OF_DAY);
            minutos= c.get(Calendar.MINUTE);


            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    edtHora.setText(hourOfDay+":"+minute);

                }
            },hora,minutos,false);
            timePickerDialog.show();

        }
    }
    public void programarRecordatorio() {
        String fechaTexto = edtFecha.getText().toString();
        String horaTexto = edtHora.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date fechaHora = dateFormat.parse(fechaTexto + " " + horaTexto);
            long timeInMillis = fechaHora.getTime();
            setReminder(this, timeInMillis);
            Toast.makeText(this, "Recordatorio programado", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setReminder(Context context, long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE); // o PendingIntent.FLAG_MUTABLE

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }
    public void limpiarTexto(){
        edtFecha.setText("");
        edtHora.setText("");
        edtTotal.setText("");
        edtPagoMinimo.setText("");
        edtAnticipo.setText("");
        edtDescripcion.setText("");
    }

    public void insertarReservacion(View v){


        String regInsertados;
        String fechaEntrega= edtFecha.getText().toString();
        String horaEntrega=edtHora.getText().toString();
        double totalReservacion =Double.parseDouble( edtTotal.getText().toString());
        double Anticipo= Double.parseDouble(edtAnticipo.getText().toString());
        double pagoMinimo= Double.parseDouble(edtPagoMinimo.getText().toString());
        String descripcionRe= edtDescripcion.getText().toString();

        String fechaEntregar="";
        SimpleDateFormat dateFormatEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatSalida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            java.util.Date fecha = dateFormatEntrada.parse(fechaEntrega);
            fechaEntregar = dateFormatSalida.format(fecha);

        } catch (ParseException e) {
            e.printStackTrace();
            // Manejar el error de análisis de fecha aquí
        }

        if (Anticipo >= pagoMinimo && Anticipo <= totalReservacion){

            reservacion.setIdCliente(idCliente);
            reservacion.setDescripcionReservacion(descripcionRe);
            reservacion.setAnticipoReservacion(Anticipo);
            reservacion.setMontoPediente(totalReservacion-Anticipo);
            reservacion.setFechaEntregaR(fechaEntrega);
            reservacion.setHoraEntrega(horaEntrega);
            reservacion.setTotalRerservacion(totalReservacion);
            insertarDetallePedido();

            programarRecordatorio();
            Intent intent = new Intent(ReservacionInsertarActivity.this, SeleccionPagoActivity.class);
            Bundle extra = new Bundle();
            extra.putSerializable("reservacion", reservacion);
            intent.putParcelableArrayListExtra("listaDetalle",  listDetalle);
            //intent.putExtra("idNegocio", idNegocio);//parece que no se necesita
            intent.putExtras(extra);

            startActivity(intent);
            finish();
            limpiarTexto();

        }else{
            Toast.makeText(this, "El anticipo debe de ser mayor o igual al 50% del costo total, pero no mayor al total",Toast.LENGTH_SHORT).show();
        }



    }
    public void insertarDetallePedido() {
        obtenerDatos();
        int contador = 0;

        // Verificar que las listas estén sincronizadas
        if (listaSpinner.size() != listaCantidad.size()) {
            Log.e("insertarDetallePedido", "La cantidad de elementos en listaSpinner y listaCantidad no coinciden");
            return;
        }

        for (int i = 0; i < listaSpinner.size(); i++) {
            String nombreProducto = listaSpinner.get(i);
            int cantidad = listaCantidad.get(i);

            for (Producto producto : listaproductos) {
                if (producto.getNombre().equals(nombreProducto)) {
                    contador++;
                    Log.v("Contador", String.valueOf(contador));

                    DetallePedidoR detallePedidoR = new DetallePedidoR();
                    detallePedidoR.setIdProducto(producto.getId());
                  //  detallePedidoR.setIdReservacion(idReservacion);
                    detallePedidoR.setCantidadDetalle(cantidad);
                    detallePedidoR.setSubTotal(producto.getPrecio() * cantidad);
                    listDetalle.add(detallePedidoR);
                    // Salir del bucle interno una vez que se ha encontrado y procesado el producto
                    break;
                }
            }
        }
    }


    private void agregarElementos() {
        String url = urlProducto + "?IDNEGOCIO=" + idNegocio;
        String productosR = ControladorSevicio.obtenerRepuestaPeticion(url, this);
        Log.v("urlProdctos",url);
        try {
            listaproductos.addAll(ControladorSevicio.obtenerProductos(productosR, this));
            //actualizarListView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout nuevoLayout = new LinearLayout(this);
        nuevoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        nuevoLayout.setOrientation(LinearLayout.HORIZONTAL);

        Spinner nuevoSpinner = new Spinner(this);
        nuevoSpinner.setTag("Spinner_" + contadorElementos);

        LinearLayout.LayoutParams spinnerLayoutParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        nuevoSpinner.setLayoutParams(spinnerLayoutParams);

        ArrayList<String> nombreProductos = new ArrayList<>();
        for (Producto producto : listaproductos) {
            if (producto.isExistencia()) {
                nombreProductos.add(producto.getNombre());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, nombreProductos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        nuevoSpinner.setAdapter(adapter);
        nuevoLayout.addView(nuevoSpinner);

        EditText nuevoEditText = new EditText(this);
        nuevoEditText.setTag("EditText_" + contadorElementos);
        LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        nuevoEditText.setLayoutParams(editTextLayoutParams);
        nuevoEditText.setHint("0");
        nuevoEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        nuevoLayout.addView(nuevoEditText);

        // Añadir TextWatcher para actualizar el total
        nuevoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No necesitas hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Llama a actualizarTotal() cada vez que cambia el texto
                actualizarTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No necesitas hacer nada aquí
            }
        });

        ImageButton botonEliminar = new ImageButton(this);
        botonEliminar.setImageResource(R.drawable.borrar);

        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenedorElementos.removeView(nuevoLayout);
                contadorElementos--;
                actualizarTotal();
            }
        });
        nuevoLayout.addView(botonEliminar);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 16, 0, 0);
        nuevoLayout.setLayoutParams(layoutParams);

        contenedorElementos.addView(nuevoLayout);
        contadorElementos++;

        actualizarTotal();
    }

    // Método para obtener el texto seleccionado en el Spinner y el valor introducido en el EditText
    private void obtenerDatos() {
        listaSpinner.clear();
        listaCantidad.clear();

        // Iterar sobre los elementos en el contenedor principal
        for (int i = 0; i < contenedorElementos.getChildCount(); i++) {
            View childView = contenedorElementos.getChildAt(i);

            if (childView instanceof LinearLayout) {
                // Obtener el Spinner y el EditText utilizando las etiquetas (tags)
                Spinner spinner = ((LinearLayout) childView).findViewWithTag("Spinner_" + i);
                EditText editText = ((LinearLayout) childView).findViewWithTag("EditText_" + i);

                //Log.v("producto",spinner.getSelectedItem().toString());
              //  Log.v("Cantidad",editText.getText().toString());

                // Obtener los datos seleccionados e introducidos
               listaSpinner.add( spinner.getSelectedItem().toString());
               listaCantidad.add(Integer.parseInt(editText.getText().toString()));


                // Utilizar los datos como desees

            }
        }
        Log.v("obtenerDatos", "Tamaño de listaSpinner: " + listaSpinner.size());
        Log.v("obtenerDatos", "Tamaño de listaCantidad: " + listaCantidad.size());
    }

    private void actualizarTotal() {

        double totalActualizado = 0;
        // Recorrer todos los elementos para calcular el total
        for (int i = 0; i < contenedorElementos.getChildCount(); i++) {
            View childView = contenedorElementos.getChildAt(i);
            if (childView instanceof LinearLayout) {
                Spinner spinner = ((LinearLayout) childView).findViewWithTag("Spinner_" + i);
                EditText editText = ((LinearLayout) childView).findViewWithTag("EditText_" + i);


                if (spinner != null && editText != null) {
                    // Resto del código aquí


                    // Obtener el producto seleccionado
                    String productoSeleccionado = spinner.getSelectedItem().toString();
                    // Obtener la cantidad seleccionada
                    String cantidadString = editText.getText().toString().trim(); // Obtener el texto y eliminar espacios en blanco
                    if (!cantidadString.isEmpty()) { // Verificar si el texto no está vacío
                        int cantidad = Integer.parseInt(cantidadString);
                        // Encontrar el precio del producto seleccionado
                        double precioProducto = 0;
                        for (Producto producto : listaproductos) {
                            if (producto.getNombre().equals(productoSeleccionado)) {
                                precioProducto = producto.getPrecio();
                                break;
                            }
                        }

                        // Sumar al total actualizado
                        totalActualizado += precioProducto * cantidad;
                    }
                }
            }
        }

        // Actualizar los campos de texto del total y el pago mínimo con los nuevos valores calculados
        total = totalActualizado;
        BigDecimal t = new BigDecimal(total);
        t=t.setScale(2, RoundingMode.DOWN);
        double totalCorregido= t.doubleValue();

        pagoMinimo = total * 0.5;
        BigDecimal p= new BigDecimal(pagoMinimo);
        p= p.setScale(2, RoundingMode.DOWN);
        double pagoCorregido= p.doubleValue();
        
        edtTotal.setText(String.valueOf(totalCorregido));
        edtPagoMinimo.setText(String.valueOf(pagoCorregido));
    }


}


