package sv.edu.ues.fia.telollevoya.negocio.negocio;

import android.app.Activity;
import android.content.Intent;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import sv.edu.ues.fia.telollevoya.*;
import sv.edu.ues.fia.telollevoya.R;

public class EditarMiNegocioActivity extends Activity {
    Spinner spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    EditText editUbicacionDescripcion, editNegocioTelefono, editNegocioNombre;
    Restaurant negocio;
    Ubicacion ubicacion;
    Distrito distrito;
    Departamento departamento;
    Municipio municipio;
    int idUbicacion, idNegocio, idAdmin;
    ControlBD helper;
    Button btnApertura, btnCierre;
    String horaapertura = "07:00 AM";
    String horacierre = "07:00 AM";
    private final String urlNegocio = "https://telollevoya.000webhostapp.com/Negocio/actualizarNegocio.php";
    private final String urlUbicacion = "https://telollevoya.000webhostapp.com/Negocio/actualizarUbicacion.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_mi_negocio);

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
        editNegocioTelefono.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));

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

        Intent intent = getIntent();
        idNegocio = intent.getIntExtra("idNegocio",0);
        idUbicacion = intent.getIntExtra("idUbicacion", 5);
        idAdmin = intent.getIntExtra("idAdministrador", 1);


        //Toast.makeText(this, "Id admin " + idAdmin, Toast.LENGTH_SHORT).show();

        // Obtenemos el negocio desde el servicio web
        String url = "https://telollevoya.000webhostapp.com/Negocio/verNegocio.php?idNegocio=" + idNegocio;
        ControladorSevicio.obtenerNegocioDesdeServicio(url, this, new ControladorSevicio.OnRestaurantReceivedListener() {
            @Override
            public void onRestaurantReceived(Restaurant restaurant) {
                negocio = restaurant;
                if (negocio != null) {
                    editNegocioNombre.setText(negocio.getNombre());
                    editNegocioTelefono.setText(negocio.getTelefono());

                } else {
                    Toast.makeText(EditarMiNegocioActivity.this, "Negocio no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        //Obtenemos el negocio
        helper.abrir();
        negocio = helper.verNegocio(idNegocio);
        helper.cerrar();

        //Consultamos la descripcion de la ubicacion
        helper.abrir();
        ubicacion = helper.consultarUbicacion(Integer.toString(negocio.getIdUbicacion()));
        helper.cerrar();

        //Consultamos el idDistrito
        helper.abrir();
        distrito = helper.consultarDistritoId(Integer.toString(ubicacion.getDistrito().getIdDistrito()));
        helper.cerrar();

        //Consutamos el idMunicipio
        helper.abrir();
        municipio = helper.consultarMunicipioId(Integer.toString(distrito.getIdMunicipio()));
        helper.cerrar();

        //Consutamos el idDepartamento
        helper.abrir();
        departamento = helper.consultarDepartamentooId(Integer.toString(municipio.getIdDepartamento()));
        helper.cerrar();

        //Marcado el departamento
        ArrayAdapter<String> adapterD = (ArrayAdapter<String>) spinnerDepartamento.getAdapter();
        int index = adapterD.getPosition(departamento.getNombreDepartamento());
        spinnerDepartamento.setSelection(index);

        if (negocio != null){
            editNegocioNombre.setText(negocio.getNombre());
            editNegocioTelefono.setText(negocio.getTelefono());
            idUbicacion = negocio.getIdUbicacion();
            editUbicacionDescripcion.setText(ubicacion.getDescripcion());
        }*/
    }

    //Actualizando el negocio
    public void actualizarNegocio(View v) throws UnsupportedEncodingException {
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

        // Codificación de horarios
        String horaApertura = URLEncoder.encode(horaapertura, "UTF-8");
        String horaCierre = URLEncoder.encode(horacierre, "UTF-8");

        // Construcción de URL para obtener ID de ubicación
        StringBuilder urlUbicacionBuilder = new StringBuilder();
        urlUbicacionBuilder.append(urlUbicacion)
                .append("?idubicacion=").append(idUbicacion)
                .append("&nombredistrito=").append(distrito)
                .append("&descripcionubicacion=").append(descripcion);
        String urlUbicacion = urlUbicacionBuilder.toString();
        ControladorSevicio.actualizarUbicacion(urlUbicacion, this);

        // Construcción de URL para editar negocio
        StringBuilder urlNegocioBuilder = new StringBuilder();
        urlNegocioBuilder.append(urlNegocio)
                .append("?idNegocio=").append(idNegocio)
                .append("&nombreNegocio=").append(nombre)
                .append("&telefonoNegocio=").append(telefono)
                .append("&horarioApertura=").append(horaApertura)
                .append("&horarioCierre=").append(horaCierre);
        String urlNegocio = urlNegocioBuilder.toString();
        ControladorSevicio.generico(urlNegocio, this);


        // Navegación a MiNegocioActivity
        Intent intent = new Intent(this, MiNegocioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("idAdministrador", String.valueOf(idAdmin));

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