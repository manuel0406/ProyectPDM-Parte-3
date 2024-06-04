package sv.edu.ues.fia.telollevoya.seguridad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import sv.edu.ues.fia.telollevoya.*;
import sv.edu.ues.fia.telollevoya.R;

public class registrarUbicacionActivity extends Activity {

    private Spinner spinnerDepartamento, spinnerMunicipio, spinnerDistrito;
    EditText txtDescripcion;
    String  descripcion, distrito;
    long idUbicacionL;
    int idCliente,idDistrito, idUbicacion;
    ControlBD helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_ubicacion);
        helper = new ControlBD(this);
        spinnerDepartamento = findViewById(R.id.spinnerDepartamento);
        spinnerMunicipio = findViewById(R.id.spinnerMunicipio);
        spinnerDistrito = findViewById(R.id.spinnerDistrito);
        txtDescripcion= findViewById(R.id.txtUbicacionExtra);

        //Recibo el idCliente
        Intent intent = getIntent();
        String idClienteStr = intent.getStringExtra("idCliente");
        idCliente = Integer.parseInt(idClienteStr);

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
                // Manejar el caso en que no se seleccione nada
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
                // Manejar el caso en que no se seleccione nada
            }
        });

    }

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

    //Cargo los distritos
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

    public void almacenarUbicacion(View v){
        //Variables a usar
        distrito = spinnerDistrito.getSelectedItem().toString();
        descripcion = txtDescripcion.getText().toString();

        //Obtengo el id del distrito
        helper.abrir();
        Distrito validadorDistrito = helper.consultarDistrito(distrito);
        helper.cerrar();
        idDistrito = validadorDistrito.getIdDistrito();

        //Insertamos la nueva ubicacion
        Ubicacion ubicacionAInsertar = new Ubicacion();
        Distrito insersionDistrito = new Distrito();
        insersionDistrito.setNombreDistrito(distrito);
        insersionDistrito.setIdDistrito(idDistrito);
        ubicacionAInsertar.setDistrito(insersionDistrito);
        ubicacionAInsertar.setDescripcion(descripcion);
        helper.abrir();
        idUbicacionL = helper.insertar(ubicacionAInsertar);
        helper.cerrar();

        //Obtenemos el idUbicacion
        idUbicacion = (int) idUbicacionL;

        //Relacion de tabla cliente-ubicacion
        helper.abrir();
        helper.insertarSeEncuetra(idCliente, idUbicacion);
        helper.cerrar();

        //Me voy a iniciar sesion
        Intent intent = new Intent(this, IniciarSesionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("desdeInicioApp", false);
        startActivity(intent);

    }

}