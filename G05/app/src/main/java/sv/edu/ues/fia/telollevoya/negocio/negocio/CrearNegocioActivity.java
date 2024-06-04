package sv.edu.ues.fia.telollevoya.negocio.negocio;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.text.method.DigitsKeyListener;
import android.annotation.SuppressLint;
import android.os.StrictMode;

import sv.edu.ues.fia.telollevoya.ControlBD;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.R;

@SuppressLint("NewApi")
public class CrearNegocioActivity extends Activity {
    Spinner spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    EditText editUbicacionDescripcion, editNegocioTelefono, editNegocioNombre;
    Button btnApertura, btnCierre;
    ControlBD helper;
    String horaapertura = "07:00 AM";
    String horacierre = "07:00 AM";
    private final String urlNegocio = "https://telollevoya.000webhostapp.com/Negocio/insertarNegocio.php";
    private final String urlUbicacion = "https://telollevoya.000webhostapp.com/Negocio/insertarUbicacion.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_negocio);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        helper = new ControlBD(this);
        btnApertura = (Button) findViewById(R.id.btnHoraApertura);
        btnCierre = findViewById(R.id.btnHoraCierre);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        spinnerDistrito = findViewById(R.id.spinnerDistrito);
        editUbicacionDescripcion = findViewById(R.id.editUbicacionDescripcion);
        editNegocioNombre = findViewById(R.id.editNegocioNombre);
        editNegocioTelefono = findViewById(R.id.editNegocioTelefono);
        editNegocioTelefono.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        cargarDepartamentos();

        // Listener para el spinner de departamentos
        spinnerDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String departamentoSeleccionado = (String) spinnerDepartamento.getSelectedItem();
                cargarMunicipios(departamentoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Listener para el spinner de municipios
        spinnerMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String municipioSeleccionado = (String) spinnerMunicipio.getSelectedItem();
                cargarDistritos(municipioSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void insertarNegocio(View v) {
        try {
            // Validación de campos
            if (editNegocioNombre.getText().toString().isEmpty() ||
                    editNegocioTelefono.getText().toString().isEmpty() ||
                    editUbicacionDescripcion.getText().toString().isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtención de valores
            String nombre = URLEncoder.encode(editNegocioNombre.getText().toString(), "UTF-8");
            String telefono = URLEncoder.encode(editNegocioTelefono.getText().toString(), "UTF-8");
            String distrito = URLEncoder.encode((String) spinnerDistrito.getSelectedItem(), "UTF-8");
            String descripcion = URLEncoder.encode(editUbicacionDescripcion.getText().toString(), "UTF-8");
            int idAdministradorRecuperado = getIntent().getIntExtra("idAdministradorRecuperado", 5);

            // Codificación de horarios
            String horaApertura = URLEncoder.encode(horaapertura, "UTF-8");
            String horaCierre = URLEncoder.encode(horacierre, "UTF-8");

            // Construcción de URL para obtener ID de ubicación
            StringBuilder urlUbicacionBuilder = new StringBuilder();
            urlUbicacionBuilder.append(urlUbicacion)
                    .append("?nombredistrito=").append(distrito)
                    .append("&descripcionubicacion=").append(descripcion);
            String urlUbicacion = urlUbicacionBuilder.toString();

            int idUbicacion = ControladorSevicio.obtenerIdUbicacion(urlUbicacion, this);
            //Toast.makeText(this, "ID de la Ubicación insertada: " + idUbicacion, Toast.LENGTH_LONG).show();

            // Construcción de URL para insertar negocio
            StringBuilder urlNegocioBuilder = new StringBuilder();
            urlNegocioBuilder.append(urlNegocio)
                    .append("?idubicacion=").append(idUbicacion)
                    .append("&idadministrador=").append(idAdministradorRecuperado)
                    .append("&nombrenegocio=").append(nombre)
                    .append("&telefononegocio=").append(telefono)
                    .append("&horarioapertura=").append(horaApertura)
                    .append("&horariocierre=").append(horaCierre);
            String urlNegocio = urlNegocioBuilder.toString();

            ControladorSevicio.generico(urlNegocio, this);

            // Navegación a MiNegocioActivity
            Intent intent = new Intent(this, MiNegocioActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("idAdministrador", String.valueOf(idAdministradorRecuperado));
            startActivity(intent);

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Error en la codificación de los datos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al insertar el negocio: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void abrirTimePicker(View v) {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this, R.style.DialogTheme, (view, hourOfDay, minute) -> {
            String amPm;
            if (hourOfDay < 12) {
                amPm = "AM";
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
            } else {
                amPm = "PM";
                if (hourOfDay > 12) {
                    hourOfDay %= 12;
                }
            }
            horaapertura = String.format(Locale.US, "%02d:%02d %s", hourOfDay, minute, amPm);
            btnApertura.setText("Hora apertura: " + horaapertura);
        }, hora, minuto, false);

        tpd.show();
    }

    public void abrirTimePicker2(View v) {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this, R.style.DialogTheme, (view, hourOfDay, minute) -> {
            String amPm;
            if (hourOfDay < 12) {
                amPm = "AM";
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
            } else {
                amPm = "PM";
                if (hourOfDay > 12) {
                    hourOfDay %= 12;
                }
            }
            horacierre = String.format(Locale.US, "%02d:%02d %s", hourOfDay, minute, amPm);
            btnCierre.setText("Hora apertura: " + horacierre);
        }, hora, minuto, false);

        tpd.show();
    }

    //Cargando direcciones
    private void cargarDepartamentos() {
        ArrayList<String> listaArrayDepartamentos;

        // Inicializar la base de datos
        helper = new ControlBD(this);
        // Obtener la lista de nombres de departamentos
        helper.abrir();
        listaArrayDepartamentos = helper.obtenerNombresDepartamentos();
        helper.cerrar();
        // Crear un adaptador para el spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaArrayDepartamentos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Establecer el adaptador en el spinner
        spinnerDepartamento.setAdapter(adapter);
    }

    private void cargarMunicipios(String departamento) {
        ArrayList<String> listaArrayMunicipios;

        // Inicializar la base de datos
        helper = new ControlBD(this);
        // Obtener la lista de nombres de departamentos
        helper.abrir();
        listaArrayMunicipios = helper.obtenerNombresMunicipios(departamento);
        helper.cerrar();
        // Crear un adaptador para el spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaArrayMunicipios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Establecer el adaptador en el spinner
        spinnerMunicipio.setAdapter(adapter);
    }

    private void cargarDistritos(String municipio) {
        ArrayList<String> listaArrayDistritos;

        // Inicializar la base de datos
        helper = new ControlBD(this);
        // Obtener la lista de nombres de departamentos
        helper.abrir();
        listaArrayDistritos = helper.obtenerNombresDistritos(municipio);
        helper.cerrar();
        // Crear un adaptador para el spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaArrayDistritos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Establecer el adaptador en el spinner
        spinnerDistrito.setAdapter(adapter);
    }
}