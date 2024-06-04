package sv.edu.ues.fia.telollevoya;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import sv.edu.ues.fia.telollevoya.negocio.negocio.Restaurant;
import sv.edu.ues.fia.telollevoya.negocio.producto.Product;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import sv.edu.ues.fia.telollevoya.Reservaciones.DetallePedidoR;

import sv.edu.ues.fia.telollevoya.pago.MetodoPago;
@SuppressLint("NewApi")
public class ControlBD {


    private final String urlDepartamento="https://telollevoya.000webhostapp.com/departamento_insertar.php";
    private  final  String urlMunicipio="https://telollevoya.000webhostapp.com/municipio_insertar.php";
    private  final  String urlDistrito="https://telollevoya.000webhostapp.com/distrito_insertar.php";
    private static final String[] camposDepartamento = new String[]{
            "idDepartamento", "nombreDepartamento"
    };

    private static final String[] camposMunicipios = new String[]{
            "idMunicipio", "idDepartamento", "nombreMunicipio"
    };

    private static final String[] camposDistrito = new String[]{
            "idDistrito", "idMunicipio", "nombreDistrito"
    };

    private static final String[] camposAccesoUsuario = new String[]{
            "idOpcion", "IdUsuario"
    };

    private static final String[] camposAdministrador = new String[]{
            "idAdministrador", "nombreAdmin", "apellidoAdmin", "sexoAdministrador", "correoAdministrador", "contraAdministrador", "fechaNacimientoAdmin"
    };

    private static final String[] camposCliente = new String[]{
            "idCliente", "nombreCliente", "apellidoCliente", "sexoCliente", "correoCliente", "contraCliente", "fechaNacimientoC"
    };

    private static final String[] camposDetallePedido = new String[]{
            "idDetallePedido", "idPedido", "IdReservacion", "idProducto", "cantidadDetalle", "subTotal"
    };

    private static final String[] camposEstadoOrden = new String[]{
            "idEstado", "tipoEstado"
    };
    private static final String[] camposFactura = new String[]{
            "idFactura", "idPago", "totalPagado", "fechaEmision"
    };
    private static final String[] camposMetodoPago = new String[]{
            "idPago", "tipoPago"
    };
    private static final String[] camposNegocio = new String[]{
            "idNegocio", "idUbicacion", "idAdministrador", "nombreNegocio", "telefonoNegocio", "horarioApertura", "horarioCierre"
    };

    private static final String[] camposOpcionCrud = new String[]{
            "idOpcion", "desOpcion", "numCrud"
    };
    private static final String[] camposPedido = new String[]{
            "idPedido", "idUbicacion", "idEstado", "idFactura", "idRepartidor", "idCliente", "totalApagar", "tiempoEstimado",
            "fechaPedido", "fechaEntrega", "descripcionOrden"
    };
    private static final String[] camposProducto = new String[]{
            "idProducto", "idNegocio", "nombreProducto", "tipoProducto", "descripcionProducto", "precioProducto", "existenciaProducto"
    };
    private static final String[] camposRepartidor = new String[]{
            "idRepartidor", "nombreRepartidor", "apellidoRepartidor", "sexoRepartidor", "correoRepartidor", "contraRepartidor", "fechaNacimientoR"
    };
    private static final String[] camposReservacion = new String[]{
            "idReservacion", "idCliente", "descripcionReservacion", "anticipoReservacion", "montoPediente", "fechaEntregaR","horaEntrega", "totalReservacion"
    };

    private static final String[] camposSeEncuentra = new String[]{
            "idPublicacion", "idCliente"
    };

    private static final String[] camposUbicacion = new String[]{
            "idUbicacion", "idDistrito", "descripcionUbicacion"
    };
    private static final String[] camposUsuario = new String[]{
            "idUsuario", "nombreUsuario", "clave", "rol", "estado"
    };


    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public ControlBD(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(context);

    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String BASE_DATOS = "teLoLlevoya.s3db";
        private static final int VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, BASE_DATOS, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL("CREATE TABLE AccesoUsuario (\n" +
                        " idOpcion INTEGER,\n" +
                        "   idUsuario INTEGER,\n" +
                        "   FOREIGN KEY (idOpcion) REFERENCES OpcionCrud(idOpcion),\n" +
                        "   FOREIGN KEY (idOpcion) REFERENCES usuario(idUsuario)\n" +
                        ");");
                db.execSQL("CREATE TABLE Administrador (\n" +
                        "   idAdministrador INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   nombreAdmin VARCHAR(30),\n" +
                        "   apellidoAdmin VARCHAR(30),\n" +
                        "   sexoAdministrador VARCHAR(30),\n" +
                        "   correoAdministrador VARCHAR(30),\n" +
                        "   contraAdministrador VARCHAR(30),\n" +
                        "   fechaNacimientoAdmin DATE\n" +
                        ");");
                db.execSQL("CREATE TABLE Cliente (\n" +
                        "   idCliente INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   nombreCliente VARCHAR(30),\n" +
                        "   apellidoCliente VARCHAR(30),\n" +
                        "   sexoCliente VARCHAR(30),\n" +
                        "   correoCliente VARCHAR(30),\n" +
                        "   contraCliente VARCHAR(30),\n" +
                        "   fechaNacimientoC DATE \n" +
                        ");");
                db.execSQL("CREATE TABLE Departamento (\n" +
                        "   idDepartamento INTEGER PRIMARY KEY,\n" +
                        "   nombreDepartamento VARCHAR(30)\n" +
                        ");");
                db.execSQL("CREATE TABLE DetallePedido (\n" +
                        "   idDetallePedido INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   idPedido INTEGER,\n" +
                        "   idReservacion INTEGER,\n" +
                        "   idProducto INTEGER,\n" +
                        "   cantidadDetalle INTEGER,\n" +
                        "   subTotal FLOAT,\n" +
                        "   FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido),\n" +
                        "   FOREIGN KEY (idReservacion) REFERENCES Reservacion(idReservacion)\n" +
                        ");");
                db.execSQL("CREATE TABLE Distrito (\n" +
                        "   idDistrito INTEGER PRIMARY KEY,\n" +
                        "   idMunicipio INTEGER,\n" +
                        "   nombreDistrito VARCHAR(30),\n" +
                        "   FOREIGN KEY (idMunicipio) REFERENCES MUNICIPIO(idMunicipio)\n" +
                        ");");
                db.execSQL("CREATE TABLE EstadoOrden (\n" +
                        "   idEstado INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   tipoEstado VARCHAR(10)\n" +
                        ");");
                db.execSQL("CREATE TABLE Factura (\n" +
                        "   idFactura INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   idPago INTEGER,\n" +
                        "   totalPagado FLOAT,\n" +
                        "   fechaEmision DATE,\n" +
                        "   FOREIGN KEY (idPago) REFERENCES MetodoPago(idPago)\n" +
                        ");");
                db.execSQL("CREATE TABLE MetodoPago (\n" +
                        "   idPago INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   tipoPago VARCHAR(10)\n" +
                        ");");
                db.execSQL("CREATE TABLE Municipio (\n" +
                        "   idMunicipio INTEGER PRIMARY KEY ,\n" +
                        "   idDepartamento INTEGER,\n" +
                        "   nombreMunicipio VARCHAR(30),\n" +
                        "   FOREIGN KEY (idDepartamento) REFERENCES Departamento(idDepartamento)\n" +
                        ");");
                db.execSQL("CREATE TABLE Negocio (\n" +
                        "   idNegocio INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   idUbicacion INTEGER,\n" +
                        "   idAdministrador INTEGER,\n" +
                        "   nombreNegocio VARCHAR(10),\n" +
                        "   telefonoNegocio VARCHAR(10),\n" +
                        "   horarioApertura VARCHAR(20),\n" +
                        "   horarioCierre VARCHAR(20),\n" +
                        "   FOREIGN KEY (idUbicacion) REFERENCES Ubicacion(idUbicacion),\n" +
                        "   FOREIGN KEY (idAdministrador) REFERENCES Administrador(idAdministrador)\n" +
                        ");");
                db.execSQL("CREATE TABLE OpcionCrud (\n" +
                        "   idOpcion INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   desOpcion VARCHAR(30),\n" +
                        "   numCrud INTEGER\n" +
                        ");");
                db.execSQL("CREATE TABLE Pedido (\n" +
                        "   idPedido INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   idUbicacion INTEGER,\n" +
                        "   idEstado INTEGER,\n" +
                        "   idFactura INTEGER,\n" +
                        "   idRepartidor INTEGER,\n" +
                        "   idCliente INTEGER,\n" +
                        "   totalApagar FLOAT,\n" +
                        "   tiempoEstimado VARCHAR(10),\n" +
                        "   fechaPedido DATE,\n" +
                        "   fechaEntregaP DATE,\n" +
                        "   descripcionOrden VARCHAR(30),\n" +
                        "   FOREIGN KEY (idUbicacion) REFERENCES Ubicacion(idUbicacion),\n" +
                        "   FOREIGN KEY (idRepartidor) REFERENCES Repartidor(idRepartidor),\n" +
                        "   FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente),\n" +
                        "   FOREIGN KEY (idEstado) REFERENCES EstadoOrden(idEstado),\n" +
                        "   FOREIGN KEY (idFactura) REFERENCES Factura(idFactura)\n" +
                        ");");
                db.execSQL("CREATE TABLE Producto (\n" +
                        "   idProducto INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   idNegocio INTEGER,\n" +
                        "   nombreProducto VARCHAR(10),\n" +
                        "   tipoProducto VARCHAR(10),\n" +
                        "   descripcionProducto VARCHAR(20),\n" +
                        "   precioProducto FLOAT(10),\n" +
                        "   existenciaProducto BOOLEAN,\n" +
                        "   FOREIGN KEY (idNegocio) REFERENCES Negocio(idNegocio)\n" +
                        ");");
                db.execSQL("CREATE TABLE Repartidor (\n" +
                        "   idRepartidor INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   nombreRepartidor VARCHAR(30),\n" +
                        "   apellidoRepartidor VARCHAR(30),\n" +
                        "   sexoRepartidor VARCHAR(30),\n" +
                        "   correoRepartidor VARCHAR(30),\n" +
                        "   contraRepartidor VARCHAR(30),\n" +
                        "   fechaNacimientoR DATE\n" +
                        ");");
                db.execSQL("CREATE TABLE Reservacion (\n" +
                        "   idReservacion INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   idCliente INTEGER,\n" +
                        "   descripcionReservacion VARCHAR(30),\n" +
                        "   anticipoReservacion FLOAT,\n" +
                        "   montoPendiente FLOAT,\n" +
                        "   fechaEntregar DATE,\n" +
                        "   horaEntrega TEXT,\n" +
                        "   totalReservacion FLOAT,\n" +
                        "   FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente)\n" +
                        ");");
                db.execSQL("CREATE TABLE Se_Encuentra (\n" +
                        "   idUbicacion INTEGER NOT NULL,\n" +
                        "   idCliente INTEGER NOT NULL,\n" +
                        "   PRIMARY KEY (idUbicacion, idCliente),\n" +
                        "   FOREIGN KEY (idUbicacion) REFERENCES Ubicacion(idUbicacion),\n" +
                        "   FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente)\n" +
                        ");");
                db.execSQL("CREATE TABLE Ubicacion (\n" +
                        "   idUbicacion INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   idDistrito INTEGER,\n" +
                        "   descripcionUbicacion VARCHAR(20),\n" +
                        "   FOREIGN KEY (idDistrito) REFERENCES Distrito(idDistrito)\n" +
                        ");");
                db.execSQL("CREATE TABLE Usuario (\n" +
                        "   idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "   nombreUsuario VARCHAR(30),\n" +
                        "   clave CHAR(5),\n" +
                        "   rol CHAR(14),\n" +
                        "   estado CHAR(7)\n" +
                        ");");

//                -- Trigger para ACCESOUSUARIO
                db.execSQL("CREATE TRIGGER fk_accesous_opcioncrud\n" +
                        "BEFORE INSERT ON AccesoUsuario\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idOpcion FROM OpcionCrud WHERE idOpcion = NEW.idOpcion) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDOPCION no existente en OPCIONCRUD')\n" +
                        "    END;\n" +
                        "END;\n");

                db.execSQL("CREATE TRIGGER fk_accesous_usuario\n" +
                        "BEFORE INSERT ON AccesoUsuario\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idUsuario FROM Usuario WHERE idUsuario = NEW.idUsuario) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDUSUARIO no existente en USUARIO')\n" +
                        "    END;\n" +
                        "END;");
                //Trigger para ADMINITRADOR
                db.execSQL("CREATE TRIGGER fk_adminit_administr_negocio\n" +
                        "BEFORE INSERT ON Negocio\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idAdministrador FROM Administrador WHERE idAdministrador = NEW.idAdministrador) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDNEGOCIO no existente en NEGOCIO')\n" +
                        "    END;\n" +
                        "END;");
                //Trigger para DISTRITO
                db.execSQL("CREATE TRIGGER fk_distrito_engloba_municipi\n" +
                        "BEFORE INSERT ON Distrito\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idMunicipio FROM Municipio WHERE idMunicipio = NEW.idMunicipio) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDMUNICIPIO no existente en MUNICIPIO')\n" +
                        "    END;\n" +
                        "END;\n");
                //Trigger para FACTURA
                db.execSQL("CREATE TRIGGER fk_factura_realiza_metodopa\n" +
                        "BEFORE INSERT ON Factura\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idPago FROM MetodoPago WHERE idPago = NEW.idPago) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDPAGO no existente en METODOPAGO')\n" +
                        "    END;\n" +
                        "END;");
                // Trigger para MUNICIPIO
                db.execSQL("CREATE TRIGGER fk_municipi_incluye_departam\n" +
                        "BEFORE INSERT ON Municipio\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idDepartamento FROM Departamento WHERE idDepartamento = NEW.idDepartamento) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDDEPARTAMENTO no existente en DEPARTAMENTO')\n" +
                        "    END;\n" +
                        "END;");
                //Trigger para NEGOCIO
                db.execSQL("CREATE TRIGGER fk_negocio_se_encuen_ubicacio\n" +
                        "BEFORE INSERT ON Negocio\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idUbicacion FROM Ubicacion WHERE idUbicacion = NEW.idUbicacion) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDUBICACION no existente en UBICACION')\n" +
                        "    END;\n" +
                        "END;");
                // Trigger para PEDIDO
                db.execSQL("CREATE TRIGGER fk_pedido_destino_ubicacio\n" +
                        "BEFORE INSERT ON Pedido\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idUbicacion FROM Ubicacion WHERE idUbicacion = NEW.idUbicacion) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDUBICACION no existente en UBICACION')\n" +
                        "    END;\n" +
                        "END;");
                db.execSQL("CREATE TRIGGER fk_pedido_genera_factura\n" +
                        "BEFORE INSERT ON Pedido\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idFactura FROM FACTURA WHERE idFactura = NEW.idFactura) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDFACTURA no existente en FACTURA')\n" +
                        "    END;\n" +
                        "END;");
                db.execSQL("CREATE TRIGGER fk_pedido_hace_cliente\n" +
                        "BEFORE INSERT ON Pedido\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idCliente FROM Cliente WHERE idCliente = NEW.idCliente) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDCLIENTE no existente en CLIENTE')\n" +
                        "    END;\n" +
                        "END;");
                db.execSQL("CREATE TRIGGER fk_pedido_tiene_estadode\n" +
                        "BEFORE INSERT ON Pedido\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idEstado FROM EstadoOrden WHERE idEstado = NEW.idEstado) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDESTADO no existente en ESTADODEORDEN')\n" +
                        "    END;\n" +
                        "END;");
                // Trigger para RESERVACION
                db.execSQL("CREATE TRIGGER fk_reservac_realiza_u_cliente\n" +
                        "BEFORE INSERT ON Reservacion\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idCliente FROM Cliente WHERE idCliente = NEW.idCliente) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDCLIENTE no existente en CLIENTE')\n" +
                        "    END;\n" +
                        "END;");
                //Trigger para SE_ENCUENTRA_
                db.execSQL("CREATE TRIGGER fk_se_encue_se_encuen_ubicacio\n" +
                        "BEFORE INSERT ON Se_Encuentra\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idUbicacion FROM Ubicacion WHERE idUbicacion = NEW.idUbicacion) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDUBICACION no existente en UBICACION')\n" +
                        "    END;\n" +
                        "END;\n");
                db.execSQL("CREATE TRIGGER fk_se_encue_se_encuen_cliente\n" +
                        "BEFORE INSERT ON Se_Encuentra\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idCliente FROM Cliente WHERE idCliente = NEW.idCliente) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDCLIENTE no existente en CLIENTE')\n" +
                        "    END;\n" +
                        "END;");
                //Trigger para ubicacion
                db.execSQL("CREATE TRIGGER fk_ubicacio_encierra_distrito\n" +
                        "BEFORE INSERT ON Ubicacion\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE WHEN ((SELECT idDistrito FROM Distrito WHERE idDistrito = NEW.idDistrito) IS NULL)\n" +
                        "    THEN RAISE(ABORT, 'Error: Intento de inserción con IDDISTRITO no existente en DISTRITO')\n" +
                        "    END;\n" +
                        "END;");
                // Trigger para asegurar que el ID de negocio en PRODUCTO existe en la tabla NEGOCIO
                db.execSQL("CREATE TRIGGER fk_producto_negocio\n" +
                        "BEFORE INSERT ON Producto\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE\n" +
                        "        WHEN ((SELECT idNegocio FROM Negocio WHERE idNegocio = NEW.idNegocio) IS NULL)\n" +
                        "        THEN RAISE(ABORT, 'El ID de negocio no existe en la tabla NEGOCIO')\n" +
                        "    END;\n" +
                        "END;");

                //Trigger para asegurar que el ID de negocio en PRODUCTO existe en la tabla NEGOCIO
                db.execSQL("CREATE TRIGGER fk_producto_negocio_update\n" +
                        "BEFORE UPDATE OF idNegocio ON Producto\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT CASE\n" +
                        "        WHEN ((SELECT idNegocio FROM Negocio WHERE idNegocio = NEW.idNegocio) IS NULL)\n" +
                        "        THEN RAISE(ABORT, 'El ID de negocio no existe en la tabla NEGOCIO')\n" +
                        "    END;\n" +
                        "END;");

            } catch (SQLException e) {
                e.printStackTrace();

            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public void abrir() throws SQLException {
        db = DBHelper.getWritableDatabase();
    }

    public void cerrar() {
        DBHelper.close();
    }



    public String insertar(Departamento departamento) {
//        String nombreDepartamentoCodificado = "",idDepartamentoCodificado="";
//        try {
//            idDepartamentoCodificado=URLEncoder.encode(String.valueOf( departamento.getIdDepartamento()), "UTF-8");
//            nombreDepartamentoCodificado = URLEncoder.encode(departamento.getNombreDepartamento(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return "Error al codificar el nombre del departamento";
//        }
//
//        String url = urlDepartamento +"?IDDEPARTAMENTO="+idDepartamentoCodificado+ "&NOMBREDEPARTAMENTO=" + nombreDepartamentoCodificado;
//        ControladorSevicio.insertarDepartamento(url, context);
//        JSONObject datosDepa = new JSONObject();

        String regInsertados = "Registros Insertados N°=";
        long contador = 0;
        ContentValues depa = new ContentValues();
        depa.put("idDepartamento", departamento.getIdDepartamento());
        depa.put("nombreDepartamento", departamento.getNombreDepartamento());
        contador = db.insert("Departamento", null, depa);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }


    public String insertar(Reservacion reservacion) {
        String regInsertados = "Registros insertados N°= ";
        long contador = 0;

        ContentValues reser = new ContentValues();
        reser.put("idCliente", reservacion.getIdCliente());
        reser.put("descripcionReservacion", reservacion.getDescripcionReservacion());
        reser.put("anticipoReservacion", reservacion.getAnticipoReservacion());
        reser.put("montoPendiente", reservacion.getMontoPediente());

        // Obtener la fecha del EditText
        String fechaTexto = reservacion.getFechaEntregaR();

        // Convertir la fecha de formato "dd/MM/yyyy" a "yyyy-MM-dd"
        SimpleDateFormat dateFormatEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatSalida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            java.util.Date fecha = dateFormatEntrada.parse(fechaTexto);
            String fechaEntrega = dateFormatSalida.format(fecha);
            reser.put("fechaEntregaR", fechaEntrega);
        } catch (ParseException e) {
            e.printStackTrace();
            // Manejar el error de análisis de fecha aquí
        }

        reser.put("horaEntrega", reservacion.getHoraEntrega());
        reser.put("totalReservacion", reservacion.getTotalRerservacion());

        contador = db.insert("Reservacion", null, reser);

        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar inserción";
        } else {
            regInsertados = String.valueOf( contador);
        }
        return regInsertados;
    }
    public String insertar(Municipio municipio) {

//        String nombreMunicipioCodificado = "",idMunicipioCodificado="", idDepartamentoCodificado="";
//        try {
//            idMunicipioCodificado=URLEncoder.encode(String.valueOf( municipio.getIdMunicipio()), "UTF-8");
//            idDepartamentoCodificado=URLEncoder.encode(String.valueOf( municipio.getIdDepartamento()), "UTF-8");
//            nombreMunicipioCodificado = URLEncoder.encode(municipio.getNombreMunicipio(), "UTF-8");
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return "Error al codificar el nombre del departamento";
//        }
//
//        String url = urlMunicipio +"?IDMUNICIPIO="+idMunicipioCodificado+"&IDDEPARTAMENTO="+idDepartamentoCodificado+ "&NOMBREMUNICIPIO=" + nombreMunicipioCodificado;
//        ControladorSevicio.insertarDepartamento(url, context);
//        JSONObject datosDepa = new JSONObject();

        String regInsertados = "Registro insetado N°= ";
        long contador = 0;
        ContentValues muni = new ContentValues();
        muni.put("idMunicipio", municipio.getIdMunicipio());
        muni.put("idDepartamento", municipio.getIdDepartamento());
        muni.put("nombreMunicipio", municipio.getNombreMunicipio());
        contador = db.insert("Municipio", null, muni);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }


    public String insertar(ArrayList<DetallePedidoR> detallePedidoR){
        String regInsertados="Registros insetados N°= ";
        long contador=0;


        for (DetallePedidoR detallePedido: detallePedidoR) {
            ContentValues detail = new ContentValues();
            detail.put("idProducto", detallePedido.getIdProducto());
            detail.put("idReservacion", detallePedido.getIdReservacion());
            detail.put("cantidadDetalle", detallePedido.getCantidadDetalle());
            detail.put("subTotal",detallePedido.getSubTotal());
            contador=db.insert("DetallePedido", null, detail);
            if (contador==-1 || contador==0){
                regInsertados= "Error al Insertar el registro, Registro Duplicado. Verificr inserción";
            }else{
                regInsertados=regInsertados+contador;
            }
        }

        return regInsertados;
    }

    public ArrayList<Reservacion> consultaReservacion(){
        Cursor cursor = db.rawQuery("SELECT DP.idReservacion, N.nombreNegocio, R.fechaEntregar, R.horaEntrega\n" +
                "FROM DetallePedido AS DP\n" +
                "JOIN Reservacion AS R ON DP.idReservacion = R.idReservacion \n" +
                "JOIN Producto AS P ON DP.idProducto = P.idProducto\n" +
                "JOIN Negocio AS N ON P.idNegocio = N.idNegocio;", null);
       // Cursor cursor = db.rawQuery("SELECT * FROM Reservacion", null);
        ArrayList<Reservacion> reservacion = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Reservacion reservaciones = new Reservacion();
                reservaciones.setIdReservacion(cursor.getInt(0));
                reservaciones.setNombreNegocion(cursor.getString(1));
                reservaciones.setFechaEntregaR(cursor.getString(2));
                reservaciones.setHoraEntrega(cursor.getString(3));

               reservacion.add(reservaciones);
            }while (cursor.moveToNext());
        }
        db.close();
        return reservacion;
    }
    public int consultaUsuario() {
        Cursor cursor = db.rawQuery("SELECT * FROM USUARIO", null);
        int idUsuario = -1;  // Valor predeterminado para indicar que no se encontró ningún usuario

        if (cursor.moveToFirst()) {
            // Asumiendo que la columna 0 es el idUsuario
            idUsuario = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return idUsuario;
    }

    public String actualizar(Reservacion reservacion){

        String [] id ={String.valueOf( reservacion.getIdReservacion())};
        ContentValues cv=  new ContentValues();
        cv.put("idCliente",reservacion.getIdCliente());
        cv.put("descripcionReservacion", reservacion.getDescripcionReservacion());
        cv.put("anticipoReservacion", reservacion.getAnticipoReservacion());
        cv.put("montoPendiente",reservacion.getMontoPediente());
        cv.put("fechaEntregaR",reservacion.getFechaEntregaR());
        cv.put("horaEntrega",reservacion.getHoraEntrega());
        cv.put("totalReservacion",reservacion.getTotalRerservacion());
        db.update("Reservacion", cv, "idReservacion=?", id);
        return null;

    }
    public String cancelarReservacion(Reservacion reservacion){

        String regAefectados= "Filas afectadas= ";
        int contador =0;
        contador+=db.delete("Reservacion","idReservacion= '"+reservacion.getIdReservacion()+"'",null);
        regAefectados+=contador;
        return regAefectados;

    }

    public String insertar (Distrito distrito){


//        String nombreDistritoCodificado = " ",idMunicipioCodificado= "", idDistritoCodificado="";
//        try {
//            idMunicipioCodificado=URLEncoder.encode(String.valueOf( distrito.getIdMunicipio()), "UTF-8");
//            idDistritoCodificado=URLEncoder.encode(String.valueOf( distrito.getIdDistrito()), "UTF-8");
//            nombreDistritoCodificado = URLEncoder.encode(distrito.getNombreDistrito(), "UTF-8");
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return "Error al codificar el nombre del departamento";
//        }
//
//        String url = urlDistrito +"?IDDISTRITO="+idDistritoCodificado+"&IDMUNICIPIO="+idMunicipioCodificado+ "&NOMBREDISTRITO=" + nombreDistritoCodificado;
//        ControladorSevicio.insertarDepartamento(url, context);
//        JSONObject datosDepa = new JSONObject();

        String regInsertados= "Registros insertados N°= ";
        long contador=0;
        ContentValues distri = new ContentValues();
        distri.put("idDistrito",distrito.getIdDistrito());
        distri.put("idMunicipio", distrito.getIdMunicipio());
        distri.put("nombreDistrito", distrito.getNombreDistrito());
        contador= db.insert("Distrito", null, distri);
        if(contador==-1 || contador==0){
            regInsertados="Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        }else{
            regInsertados=regInsertados+contador;
        }
        return regInsertados;
    }

    public ArrayList<Departamento> getDepartamentos(){
        Cursor cursor = db.rawQuery("SELECT * FROM Departamento", null);
        ArrayList<Departamento> departamentos = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(cursor.getInt(0));
                departamento.setNombreDepartamento(cursor.getString(1));

                departamentos.add(departamento);
            }while (cursor.moveToNext());
        }
        return departamentos;
    }


    public ArrayList<Municipio> getMunicipioPorDepto(int idDepartamento){
        Cursor cursor = db.rawQuery("SELECT * FROM Municipio WHERE idDepartamento = ?",
                new String[]{Integer.toString(idDepartamento)});
        ArrayList<Municipio> municipios = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Municipio municipio = new Municipio();
                municipio.setIdMunicipio(cursor.getInt(0));
                municipio.setNombreMunicipio(cursor.getString(2));
                municipio.setIdDepartamento(cursor.getInt(1));

                municipios.add(municipio);
            }while(cursor.moveToNext());
        }
        return municipios;
    }

    public ArrayList<Distrito> getDistritoPorMunicipio(int idMunicipio){
        Cursor cursor = db.rawQuery("SELECT * FROM Distrito WHERE idMunicipio = ?",
                new String[]{Integer.toString(idMunicipio)});
        ArrayList<Distrito> distritos = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Distrito distrito = new Distrito();
                distrito.setIdDistrito(cursor.getInt(0));
                distrito.setIdMunicipio(cursor.getInt(1));
                distrito.setNombreDistrito(cursor.getString(2));


                distritos.add(distrito);
            }while(cursor.moveToNext());
        }
        return distritos;
    }

    public ArrayList<Producto> getProductos() {
        Cursor cursor = db.rawQuery("SELECT * FROM Producto", null);
        ArrayList<Producto> productos = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Producto producto = new Producto();
                producto.setId(cursor.getInt(0));
                producto.setIdNegocio(cursor.getInt(1));
                producto.setNombre(cursor.getString(2));
                producto.setTipo(cursor.getString(3));
                producto.setDescripcion(cursor.getString(4));
                producto.setPrecio(cursor.getFloat(5));
                producto.setExistencia(cursor.getInt(6) > 0);

                productos.add(producto);
            } while (cursor.moveToNext());
        }
        // No cierres la base de datos aquí
        // db.close();
        return productos;
    }


    //Todos los pedidos del cliente proporcionado
    public ArrayList<Pedido> getPedidos(int idCliente) {
        String query = "SELECT Pedido.idPedido,Pedido.idEstado, Pedido.idRepartidor," +
                "Pedido.totalApagar, Pedido.fechaPedido, Pedido.fechaEntregaP, " +
                "Pedido.descripcionOrden, EstadoOrden.tipoEstado FROM Pedido INNER JOIN EstadoOrden"
                + " ON Pedido.idEstado = EstadoOrden.idEstado"
                + " WHERE idCliente =" + idCliente;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Pedido> pedidos = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = new Pedido();
                pedido.setId(cursor.getInt(0));

                EstadoOrden estado = new EstadoOrden();
                estado.setId(cursor.getInt(1));
                estado.setTipo(cursor.getString(7));
                pedido.setEstadoOrden(estado);

                Repartidor rep = new Repartidor();
                rep.setIdRepartidor(Integer.toString(cursor.getInt(2)));
                pedido.setRepartidor(rep);

                pedido.setTotalAPagar(cursor.getFloat(3));

                Long fechaPed = cursor.getLong(4);
                if (fechaPed != null)
                    pedido.setFechaPedido(new Date(fechaPed));
                else
                    pedido.setFechaPedido(null);

                Long fechaEn = cursor.getLong(5);
                if (fechaEn != null)
                    pedido.setFechaEntrega(new Date(fechaEn));
                else
                    pedido.setFechaEntrega(null);

                pedido.setDescripcionOrden(cursor.getString(6));

                pedidos.add(pedido);
            } while (cursor.moveToNext());
        }
        db.close();
        return pedidos;
    }

    public boolean actualizarEstadoPedido(int idPedido, int idEstado) {
        ContentValues cv = new ContentValues();
        cv.put("idEstado", idEstado);
        int filasAfect = db.update("Pedido", cv, "idPedido = ?", new String[]{Integer.toString(idPedido)});
        if (filasAfect > 0)
            return true;
        return false;
    }

    //Pedidos pendientes pero con un REPARTIDOR asignado previamente
    public ArrayList<Pedido> getPedidosPendientesConRepartidor() {
        String query = "SELECT Pedido.idPedido, Pedido.totalApagar, Pedido.fechaPedido, " +
                "Pedido.fechaEntregaP, Pedido.descripcionOrden,Pedido.idRepartidor, " +
                "Repartidor.nombreRepartidor, Repartidor.apellidoRepartidor," +
                "Pedido.idCliente" +
                " FROM Pedido INNER JOIN Repartidor"
                + " ON Pedido.idRepartidor = Repartidor.idRepartidor"
                + " WHERE idEstado = 1";//1 de PEDIDOS ACTIVOS
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Pedido> pedidos = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = new Pedido();
                pedido.setId(cursor.getInt(0));
                pedido.setTotalAPagar(cursor.getFloat(1));
                Long fechaPed = cursor.getLong(2);
                if (fechaPed != null)
                    pedido.setFechaPedido(new Date(fechaPed));
                else
                    pedido.setFechaPedido(null);

                Long fechaEn = cursor.getLong(3);
                if (fechaEn != null)
                    pedido.setFechaEntrega(new Date(fechaEn));
                else
                    pedido.setFechaEntrega(null);
                pedido.setDescripcionOrden(cursor.getString(4));
                Repartidor rep = new Repartidor();
                rep.setIdRepartidor(Integer.toString(cursor.getInt(5)));
                rep.setNombre(cursor.getString(6));
                rep.setApellido(cursor.getString(7));
                pedido.setRepartidor(rep);

                Cliente cliente = new Cliente();
                cliente.setIdCliente(Integer.toString(cursor.getInt(8)));
                pedido.setCliente(cliente);
                pedidos.add(pedido);
            } while (cursor.moveToNext());
        }
        return pedidos;
    }

    //Pedidos pendientes y sin REPARTIDOR ASIGNADO
    public ArrayList<Pedido> getPedidosPendientes() {
        String query = "SELECT * FROM PEDIDO WHERE idEstado = 1 AND idRepartidor IS NULL";//1 de PEDIDOS ACTIVOS
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Pedido> pedidos = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = new Pedido();
                pedido.setId(cursor.getInt(0));
                Cliente cliente = new Cliente();
                cliente.setIdCliente(Integer.toString(cursor.getInt(5)));
                pedido.setCliente(cliente);
                pedido.setTotalAPagar(cursor.getFloat(6));
                Long fechaPed = cursor.getLong(8);
                if (fechaPed != null)
                    pedido.setFechaPedido(new Date(fechaPed));
                else
                    pedido.setFechaPedido(null);

                Long fechaEn = cursor.getLong(9);
                if (fechaEn != null)
                    pedido.setFechaEntrega(new Date(fechaEn));
                else
                    pedido.setFechaEntrega(null);
                pedido.setDescripcionOrden(cursor.getString(10));

                pedidos.add(pedido);
            } while (cursor.moveToNext());
        }
        return pedidos;
    }

    public ArrayList<Repartidor> getRepartidores() {
        String query = "SELECT * FROM Repartidor";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Repartidor> repartidores = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Repartidor repartidor = new Repartidor();
                repartidor.setIdRepartidor(Integer.toString(cursor.getInt(0)));
                repartidor.setNombre(cursor.getString(1));
                repartidor.setApellido(cursor.getString(2));
                repartidor.setSexo(cursor.getString(3));

                repartidores.add(repartidor);
            } while (cursor.moveToNext());
        }
        return repartidores;
    }

    public ArrayList<Pedido> getPedidosActivosPorRepartidor(int idRepartidor) {
        String sql = "SELECT Pedido.idPedido, Pedido.totalApagar, Pedido.fechaPedido, " +
                "Pedido.descripcionOrden, " +
                "Pedido.idCliente, Ubicacion.descripcionUbicacion" +
                " FROM Pedido INNER JOIN Ubicacion"
                + " ON Pedido.idUbicacion = Ubicacion.idUbicacion"
                + " WHERE idEstado = 1 AND Pedido.idRepartidor = " + idRepartidor;
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Pedido> pedidos = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = new Pedido();
                pedido.setId(cursor.getInt(0));
                pedido.setTotalAPagar(cursor.getFloat(1));
                String fechaPed = cursor.getString(2);
                if (fechaPed != null)
                    pedido.setFechaPedido(Date.valueOf(fechaPed));
                else
                    pedido.setFechaPedido(null);
                pedido.setDescripcionOrden(cursor.getString(3));
                Cliente cl = new Cliente();
                cl.setIdCliente(Integer.toString(cursor.getInt(4)));
                pedido.setCliente(cl);

                Ubicacion ub = new Ubicacion();
                ub.setDescripcion(cursor.getString(5));
                pedido.setUbicacion(ub);

                pedidos.add(pedido);
            } while (cursor.moveToNext());
        }
        return pedidos;
    }

    //Modificar el REPARTIDOR asignado al pedido
    public boolean actualizarRepartidorPedido(int idRepartidor, int idPedido) {
        ContentValues cv = new ContentValues();
        cv.put("idRepartidor", idRepartidor);
        int result = db.update("Pedido", cv, "idPedido = ?", new String[]{Integer.toString(idPedido)});
        if (result > 0)
            return true;
        return false;
    }

    public int ultimaReservacion(){
        String query = "SELECT * FROM Reservacion ORDER BY idReservacion DESC  LIMIT 1";
        int idReservacion=0;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            idReservacion= cursor.getInt(0);
        }

        return idReservacion;
    }
    public boolean actualizarEstadoPedido(int idPedido, int idEstado, java.util.Date fechaEntrega) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ContentValues cv = new ContentValues();
        cv.put("fechaEntregaP", dateFormat.format(fechaEntrega));
        cv.put("idEstado", idEstado);
        int filasAfect = db.update("Pedido", cv, "idPedido = ?", new String[]{Integer.toString(idPedido)});
        if (filasAfect > 0)
            return true;
        return false;
    }


    //Modulo producto - Insertar  (Ya estuvo)
    public String insertar(Product producto) {
        String regInsertados = "Producto Registrado";
        long contador = 0;
        ContentValues product = new ContentValues();
        product.put("idNegocio", producto.getIdNegocio());//Nuevo
        product.put("nombreProducto", producto.getNombreProducto());
        product.put("tipoProducto", producto.getTipoProducto());
        product.put("descripcionProducto", producto.getDescripcionProducto());
        product.put("precioProducto", producto.getPrecioProducto());
        product.put("existenciaProducto", producto.isExistenciaProducto());

        contador = db.insert("Producto", null, product);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado.Verificar inserción ";
        } else {
            regInsertados = regInsertados;
        }
        return regInsertados;
    }

    //Modulo producto - Consultar todos los productos (Ya estuvo)
    public ArrayList<Product> obtenerProductosPorIdNegocio(int idNegocio) {
        ArrayList<Product> productos = new ArrayList<>();

        // Realizar la consulta a la base de datos para obtener los productos con el idNegocio especificado
        Cursor cursor = db.query("Producto", camposProducto, "idNegocio = ?",
                new String[]{String.valueOf(idNegocio)}, null, null, null);

        // Verificar si se encontraron productos
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product producto = new Product();
                producto.setIdProducto(cursor.getInt(0));
                producto.setIdNegocio(cursor.getInt(1));
                producto.setNombreProducto(cursor.getString(2));
                producto.setTipoProducto(cursor.getString(3));
                producto.setDescripcionProducto(cursor.getString(4));
                producto.setPrecioProducto(cursor.getFloat(5));
                producto.setExistenciaProducto(cursor.getInt(6) == 1); // Convertir de 0/1 a booleano

                // Agregar el producto a la lista
                productos.add(producto);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return productos;
    }

    //Modulo producto - Consultar un producto en especifico (Ya estuvo)
    public Product verProducto(int idProducto) {
        Product producto = null;

        Cursor cursor = db.query("Producto", camposProducto, "idProducto = ?",
                new String[]{String.valueOf(idProducto)}, null, null, null, "1");

        if (cursor != null) {
            cursor.moveToFirst();
            producto = new Product();
            producto.setIdProducto(cursor.getInt(0));
            producto.setIdNegocio(cursor.getInt(1));
            producto.setNombreProducto(cursor.getString(2));
            producto.setTipoProducto(cursor.getString(3));
            producto.setDescripcionProducto(cursor.getString(4));
            producto.setPrecioProducto(cursor.getFloat(5));
            producto.setExistenciaProducto(cursor.getInt(6) == 1); // Convertir de 0/1 a booleano
        }
        cursor.close();
        return producto;
    }

    //Modulo producto - Actualizar (Ya estuvo)
    public String actualizar(Product producto) {
        String[] id = {String.valueOf(producto.getIdProducto())};

        ContentValues product = new ContentValues();
        product.put("nombreProducto", producto.getNombreProducto());
        product.put("tipoProducto", producto.getTipoProducto());
        product.put("descripcionProducto", producto.getDescripcionProducto());
        product.put("precioProducto", producto.getPrecioProducto());
        product.put("existenciaProducto", producto.isExistenciaProducto());

        db.update("Producto", product, "idProducto = ?", id);
        return "Producto Actualizado";
    }

    //Modulo producto - Eliminar
    public String eliminarProducto(int idProducto) {
        int filasAfectadas = db.delete("Producto", "idProducto = ?", new String[]{String.valueOf(idProducto)});
        String regAfectados;
        if (filasAfectadas > 0) {
            regAfectados = "Producto eliminado";
        } else {
            regAfectados = "No se pudo eliminar el producto. No se encontró el producto  especificado";
        }
        return regAfectados;
    }



    //Modulo negocio - insertar ubicacion (Ya estuvo)
    public int insertarUbicacion1(Restaurant restaurant, String nombreDistrito) {
        //Consiguiendo el idDistrito asociado a nombreDistrito
        int idDistrito = 10;
        Cursor cursor = db.rawQuery("SELECT idDistrito FROM Distrito WHERE nombreDistrito = '" + nombreDistrito + "'", null);
        if (cursor.moveToFirst()) {
            idDistrito = cursor.getInt(0);
        }
        cursor.close();


        //Agregando registro a tabla Ubicacion
        ContentValues ubicacion = new ContentValues();
        ubicacion.put("idDistrito", idDistrito);
        ubicacion.put("descripcionUbicacion", restaurant.getDescripcionUbicacion());
        long idUbicacion;
        idUbicacion = db.insert("Ubicacion", null, ubicacion);
        int valorInt = (int) idUbicacion;

        return valorInt;
    }

    //Modulo negocio - insertar Negocio (Ya estuvo)
    public String insertarNegocio(Restaurant restaurant) {
        String regInsertados = "Producto Registrado Correctamente";
        long contador = 0;
        ContentValues negocio = new ContentValues();
        negocio.put("idUbicacion", restaurant.getIdUbicacion());
        negocio.put("idAdministrador", restaurant.getIdAdministrador());
        negocio.put("nombreNegocio", restaurant.getNombre());
        negocio.put("telefonoNegocio", restaurant.getTelefono());
        negocio.put("horarioApertura", restaurant.getHorarioApertura());
        negocio.put("horarioCierre", restaurant.getHorarioCierre());

        contador = db.insert("Negocio", null, negocio);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado.Verificar inserción ";
        } else {
            regInsertados = regInsertados;
        }
        return regInsertados;
    }

    //Modulo negocio - Consultar todos los negocios (Ya estuvo)
    public ArrayList<Restaurant> obtenernegociosPorAdministrador(int idAdministrador) {
        ArrayList<Restaurant> productos = new ArrayList<>();

        // Realizar la consulta a la base de datos para obtener los productos con el idAdministrador especificado
        Cursor cursor = db.query("Negocio", camposNegocio, "idAdministrador = ?",
                new String[]{String.valueOf(idAdministrador)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Restaurant negocio = new Restaurant();
                negocio.setIdNegocio(cursor.getInt(0));
                negocio.setIdUbicacion(cursor.getInt(1));
                negocio.setIdAdministrador(cursor.getInt(2));
                negocio.setNombre(cursor.getString(3));
                negocio.setTelefono(cursor.getString(4));
                negocio.setHorarioApertura(cursor.getString(5));
                negocio.setHorarioCierre(cursor.getString(6));

                // Consultar la ubicación del negocio
                Cursor ubicacionCursor = db.rawQuery("SELECT d.nombreDepartamento, m.nombreMunicipio, di.nombreDistrito, u.descripcionUbicacion " +
                        "FROM Ubicacion u " +
                        "INNER JOIN Distrito di ON u.idDistrito = di.idDistrito " +
                        "INNER JOIN Municipio m ON di.idMunicipio = m.idMunicipio " +
                        "INNER JOIN Departamento d ON m.idDepartamento = d.idDepartamento " +
                        "WHERE u.idUbicacion = ?", new String[]{String.valueOf(negocio.getIdUbicacion())});

                if (ubicacionCursor != null && ubicacionCursor.moveToFirst()) {
                    String nombreDepartamento = ubicacionCursor.getString(0);
                    String nombreMunicipio = ubicacionCursor.getString(1);
                    String nombreDistrito = ubicacionCursor.getString(2);
                    String descripcionUbicacion = ubicacionCursor.getString(3);

                    // Construir el string de ubicación
                    String ubicacion = nombreDepartamento + ", " + nombreMunicipio + ", " + nombreDistrito + ", " + descripcionUbicacion;
                    negocio.setDescripcionUbicacion(ubicacion);

                    ubicacionCursor.close();
                }

                // Agregar el negocio a la lista
                productos.add(negocio);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return productos;

    }

    //Modulo Negocio - Consultar un negocio en especifico (Ya estuvo)
    public Restaurant verNegocio(int idNegocio) {
        Restaurant negocio = null;

        Cursor cursor = db.query("Negocio", camposNegocio, "idNegocio = ?",
                new String[]{String.valueOf(idNegocio)}, null, null, null, "1");

        if (cursor != null) {
            cursor.moveToFirst();
            negocio = new Restaurant();
            negocio.setIdNegocio(cursor.getInt(0));
            negocio.setIdUbicacion(cursor.getInt(1));
            negocio.setIdAdministrador(cursor.getInt(2));
            negocio.setNombre(cursor.getString(3));
            negocio.setTelefono(cursor.getString(4));
            negocio.setHorarioApertura(cursor.getString(5));
            negocio.setHorarioCierre(cursor.getString(6));
        }
        cursor.close();
        return negocio;
    }

    //Modulo negocio - actualizar ubicacion (Ya estuvo)
    public String actualizarUbicacion(Restaurant restaurant, int idUbicacion, String nombreDistrito) {
        String[] id = {String.valueOf(idUbicacion)};
        //Consiguiendo el idDistrito asociado a nombreDistrito
        int idDistrito = 10;
        Cursor cursor = db.rawQuery("SELECT idDistrito FROM Distrito WHERE nombreDistrito = '" + nombreDistrito + "'", null);
        if (cursor.moveToFirst()) {
            idDistrito = cursor.getInt(0);
        }
        cursor.close();

        ContentValues ubicacion = new ContentValues();
        ubicacion.put("idDistrito", idDistrito);
        ubicacion.put("descripcionUbicacion", restaurant.getDescripcionUbicacion());

        db.update("Ubicacion", ubicacion, "idUbicacion = ?", id);
        return "Producto Actualizado Correctamente";
    }

    //Modulo negocio - actualizar Negocio (Ya estuvo)
    public String actualizarNegocio(Restaurant restaurant) {
        String[] idNegocio = {String.valueOf(restaurant.getIdNegocio())};

        ContentValues negocio = new ContentValues();
        negocio.put("nombreNegocio", restaurant.getNombre());
        negocio.put("telefonoNegocio", restaurant.getTelefono());
        negocio.put("horarioApertura", restaurant.getHorarioApertura());
        negocio.put("horarioCierre", restaurant.getHorarioCierre());
        db.update("Negocio", negocio, "idNegocio = ?", idNegocio);
        return "Datos del Negocio actualizados";
    }

    // (Ya estuvo)
    public String eliminarNegocio(int idNegocio) {
        String regAfectados = "Negocio Eliminado Correctamente";
        int contadorNegocio = 0;
        int contadorUbicacion = 0;
        int contadorProducto = 0;

        // Eliminar los registros de la tabla "Ubicacion" asociados al negocio
        String consultaUbicacion = "DELETE FROM Ubicacion WHERE idUbicacion = (SELECT idUbicacion FROM Negocio WHERE idNegocio = " + idNegocio + ")";
        db.execSQL(consultaUbicacion);
        Cursor cursorUbicacion = db.rawQuery(consultaUbicacion, null);
        cursorUbicacion.close();


        // Eliminar los registros de la tabla "Producto" asociados al negocio
        contadorProducto = db.delete("Producto", "idNegocio = " + idNegocio, null);

        // Eliminar el registro de la tabla "Negocio"
        contadorNegocio = db.delete("Negocio", "idNegocio = " + idNegocio, null);

        // Concatenar el número de registros afectados al mensaje de retorno
        //regAfectados += "\nRegistros de Negocio: " + contadorNegocio + ", Registros de Ubicacion: " + contadorUbicacion + ", Registros de Producto: " + contadorProducto;

        // Retornar el mensaje con la cantidad de registros afectados en cada tabla
        return regAfectados;
    }


    //Consultas para el modulo negocio
    //Ubicacion - Departamento
    public ArrayList<String> obtenerNombresDepartamentos() {
        ArrayList<String> nombresDepartamentos = new ArrayList<>();

        // Realizar la consulta a la base de datos para obtener los nombres de los departamentos
        Cursor cursor = db.query("Departamento", new String[]{"nombreDepartamento"}, null, null, null, null, null);

        // Verificar si se encontraron departamentos
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Obtener el nombre del departamento y agregarlo a la lista
                String nombreDepartamento = cursor.getString(0); // Índice 0 para el nombre del departamento
                nombresDepartamentos.add(nombreDepartamento);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return nombresDepartamentos;
    }

    //Ubicacion - Municipio
    public ArrayList<String> obtenerNombresMunicipios(String nombreDepartamento) {
        ArrayList<String> nombresMunicipios = new ArrayList<>();

        // Realizar la consulta para obtener el idDepartamento asociado al nombreDepartamento
        Cursor cursorIdDepartamento = db.query("Departamento", new String[]{"idDepartamento"}, "nombreDepartamento=?", new String[]{nombreDepartamento}, null, null, null);

        // Verificar si se encontró el idDepartamento
        if (cursorIdDepartamento != null && cursorIdDepartamento.moveToFirst()) {
            int idDepartamento = cursorIdDepartamento.getInt(0); // Obtener el idDepartamento
            cursorIdDepartamento.close();

            // Realizar la consulta para obtener los nombres de los municipios asociados al idDepartamento
            Cursor cursorMunicipios = db.query("Municipio", new String[]{"nombreMunicipio"}, "idDepartamento=?", new String[]{String.valueOf(idDepartamento)}, null, null, null);

            // Verificar si se encontraron municipios asociados al idDepartamento
            if (cursorMunicipios != null && cursorMunicipios.moveToFirst()) {
                do {
                    // Obtener el nombre del municipio y agregarlo a la lista
                    String nombreMunicipio = cursorMunicipios.getString(0);
                    nombresMunicipios.add(nombreMunicipio);
                } while (cursorMunicipios.moveToNext());
                cursorMunicipios.close();
            }
        }

        return nombresMunicipios;
    }

    //Ubicacion - Distrito
    public ArrayList<String> obtenerNombresDistritos(String nombreMunicipio) {
        ArrayList<String> nombresDistritos = new ArrayList<>();

        // Realizar la consulta para obtener el idMunicipio asociado al nombreMunicipio
        Cursor cursorIdMunicipio = db.query("Municipio", new String[]{"idMunicipio"}, "nombreMunicipio=?", new String[]{nombreMunicipio}, null, null, null);

        // Verificar si se encontró el idMunicipio
        if (cursorIdMunicipio != null && cursorIdMunicipio.moveToFirst()) {
            int idMunicipio = cursorIdMunicipio.getInt(0); // Obtener el idMunicipio
            cursorIdMunicipio.close();

            // Realizar la consulta para obtener los nombres de los distritos asociados al idMunicipio
            Cursor cursorDistritos = db.query("Distrito", new String[]{"nombreDistrito"}, "idMunicipio=?", new String[]{String.valueOf(idMunicipio)}, null, null, null);

            // Verificar si se encontraron distritos asociados al idMunicipio
            if (cursorDistritos != null && cursorDistritos.moveToFirst()) {
                do {
                    // Obtener el nombre del distrito y agregarlo a la lista
                    String nombreDistrito = cursorDistritos.getString(0);
                    nombresDistritos.add(nombreDistrito);
                } while (cursorDistritos.moveToNext());
                cursorDistritos.close();
            }
        }

        return nombresDistritos;
    }


    private boolean verificarIntegridad(Object dato, int relacion) throws SQLException {

        switch (relacion) {


        }
        return false;
    }

    public String llenarBD() {

        final int[] VDidDepartamento = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        final String[] VDnombreDepartamento = {"Ahuachapan", "Santa Ana", "Sonsonate", "Chalatenango", "La Libertad", "San Salvador"
                , "Cuscatlán", "La Paz", "Cabañas", " San Vicente", "Usulután", "San Miguel", "Morazon", "La Unión"};


        final int[] VMidMunicipio = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

        final int[] VMidDepartamento = {1, 1, 1,
                2, 2, 2, 2,
                3, 3, 3, 3,
                4, 4, 4,
                5, 5, 5, 5, 5, 5,
                6, 6, 6, 6, 6,
                7, 7,
                8, 8, 8,
                9, 9,
                10, 10,
                11, 11, 11,
                12, 12, 12,
                13, 13,
                14, 14};

        final String[] VMnombreMunicipio = {
                "Ahuachapán Norte", "Ahuachapán Centro", "Ahuachapán Sur",
                "Santa Ana Norte", "Santa Ana Centro", "Santa Ana Este", "Santa Ana Oeste",
                "Sonsonate Norte", "Sonsonate Centro", "Sonsonate Este", "Sonsonate Oeste",
                "Chalatenango Norte", "Chalatenango Centro", "Chalatenango Sur",
                "La Libertad Norte", "La Libertad Centro", "La Libertad Oeste", "La Libertad Este", "La Libertad Costa", "La Libertad Sur",
                "San Salvador Norte", "San Salvador Oeste", "San Salvador Este", "San Salvador Centro", "San Salvador Sur",
                "Cuscatlán Norte", "Cuscatlán Sur",
                "La Paz Oeste", "La Paz Centro", "La Paz Este",
                "Cabañas Este", "Cabañas Oeste",
                "San Vicente Norte", "San Vicente Sur",
                "Usulután Norte", "Usulután  Este", "Usulután Oeste",
                "San Miguel Norte", "San Miguel Centro", "San Miguel Oeste",
                "Morazán Norte", "Morazón Sur",
                "La Unión Norte", "La Unión Sur"
        };


        //final String[] VIidDistrito ={"1","2","3","4","5"};
        final int[] VIidDistrito = new int[262];
        for (int i = 0; i < 262; i++) {
            VIidDistrito[i] = i + 1;
        }
        final int[] VIidMunicipio = {
                1, 1, 1, 1,
                2, 2, 2, 2,
                3, 3, 3, 3,
                4, 4, 4, 4,
                5,
                6, 6,
                7, 7, 7, 7, 7, 7,
                8, 8, 8, 8,
                9, 9, 9, 9, 9,
                10, 10, 10, 10, 10, 10,
                11,
                12, 12, 12,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
                15, 15, 15,
                16, 16,
                17, 17, 17, 17, 17,
                18, 18, 18, 18, 18,
                19, 19, 19, 19, 19,
                20, 20,
                21, 21, 21,
                22, 22,
                23, 23, 23, 23,
                24, 24, 24, 24, 24,
                25, 25, 25, 25, 25,
                26, 26, 26, 26, 26,
                27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27,
                28, 28, 28, 28, 28, 28, 28,
                29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
                30, 30, 30,
                31, 31, 31, 31, 31,
                32, 32, 32, 32,
                33, 33, 33, 33, 33, 33, 33,
                34, 34, 34, 34, 34, 34,
                35, 35, 35, 35, 35, 35, 35, 35, 35,
                36, 36, 36, 36, 36, 36, 36, 36, 36, 36,
                37, 37, 37, 37,
                38, 38, 38, 38, 38, 38, 38, 38,
                39, 39, 39, 39, 39, 39,
                40, 40, 40, 40, 40, 40,
                41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41,
                42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42, 42,
                43, 43, 43, 43, 43, 43, 43, 43, 43, 43,
                44, 44, 44, 44, 44, 44, 44, 44
        };
        final String[] VInombreDistrito = {
                /*1 Ahuchapan Norte **/"Atiquizaya", "El Refugio", "San Lorenzo", "Turín",
                /*2 Ahuachaoan centro **/"Ahuachapan", "Apaneca", "Concepción de Ataco", "Tacuba",
                /*3 Ahuachapan Sur **/  "Guaymango", "Jujutla", "San Franciso Menéndez", "San Pedro Puxtla",
                /*4 Santa Ana Norte **/ "Masahuat", "Metapán", "Santa Rosa Guachipilín", "Texistepeque",
                /*5 Santa Ana Centro **/ "Santa Ana",
                /*6 Santa Ana Este **/"Coatepeque", "El Congo",
                /*7 Santa Ana Oeste **/ "Candelaria de la Frontera", "Chalchuapa", "El Porvenir", "San Antonio Pajonal", "San Sebastián Salitrillo", "Santiago de la Frontera",
                /*8 Sonsonate Norte **/"Juayúa", "Nahuizalgo", "Salcoatitán", "San Catarina Masahuat",
                /*9 Sonsonate Centro **/"Sonsonate", "Sonzacate", "Nahulingo", "San Antonio del Monte", "Santo Domingo de Guzmán",
                /*10 Sonsonate Este **/ "Izalco", "Armenia", "Caluco", "San Julián", "Cuisnahuat", "Santa Isabel Ishuatán",
                /*11 Sonsonate Oeste **/ "Acajutla",
                /*12 Chalatenango Norte **/"La Palma", "Citalá", "San Ignacio",
                /*13 Chalatenango Centro **/"Nueva Concepción", "Tejutla", "La Reina", "Agua Caliente", "Dulce Nombre de María", "El Paraíso", "San Franciso Morazán", "San Rafael", "Santa Rita", "San Fernando",
                /*14 Chalatenango Sur **/"Chalatenango", "Arcatao", "Azacualpa", "Comalapa", "Concepción Quezaltepeque", "El Carrizal", "La Laguna", "Las Vueltas", "Nombre de Jesús", "Nueva Trinidad", "Ojos de Agua", "Potonico", "San Antonio de la Cruz", "San Antonio Los Ranchos", "San Franciso Lempa", "San Isidro Labrador", "San José Cancasque", "San Miguel de Mercedes", "San José Las Flores", "San Luis del Carmen",
                /*15 La libertad Norte **/"Quezaltepeque", "San Matías", "San Pablo Tacachico",
                /*16 La libertad Centro **/"San Juan Opico", "Ciudad Arce",
                /*17 La libertad Oeste **/"Colón", "Jayaque", "Sacacoyo", "Tepecoyo", "Talnique",
                /*18 La libertad Este **/"Antiguo Cuscatlán", "Huizúcar", "Nuevo Cuscatlán", "San José Villanueva", "Zaragoza",
                /*19 La libertad Costa **/"Chiltiupán", "Jicalapa", "La libertad", "Tamanique", "Teotepeque",
                /*20 La libetad Sur **/"Comasagua", "Santa Tecla",
                /*21 San Salvador Norte **/"Aguilares", "El Paisnal", "Guazapa",
                /*22 San Salvador Oeste **/"Apopa", "Nejapa",
                /*23 San Salvador Este **/ "Ilopango", "San Martín", "Soyapango", "Tonacatepeque",
                /*24 San Salvador Centro **/"Ayutuxtepeque", "Mejicanos", "Cuscatancingo", "San Salvador", "Ciudad Delgado",
                /*25 San Salvador Sur **/"Panchimalco", "Rosario de Mora", "San Marcos", "Santo Tomas", "Santiago Texacuangos",
                /*26 Cuscatlan norte **/"Suchitoto", "San José Guayabal", "Oratorio de Concepción", "San Bartolomé Perulapán", "San Pedro Perulapán",
                /*27 Cuscatlan Sur **/"Cojutepeque", "San Rafael Cedros", "Candelaria", "Monte San Juan", "El Carmen", "San Cristóbal", "Santa Cruz Michapa", "San Ramón", "El Rosario", "Santa Cruz Analquito", "Tenancingo",
                /*28 La Paz Oeste **/"Cuyultitán", "Olocuilta", "San Juan Talpa", "San Luis Talpa", "San Pedro Masahuat", "Tapalhuaca", "San Francisco Chinameca",
                /*29 La Paz Centro **/"El Rosario", "Jerusalen", "Mercedes La Ceiba", "Paraíso de Osorio", "San Antonio Masahuat", "San Emigdio", "San Juan Tepezontes", "San Luis La Herradura", "San Miguel Tepezontes", "San Pedro Nonualco", "Santa Maria Ostuma", "Santiago Nonualco",
                /*30 La Paz Este **/"San Juan Nonualco", "San Rafael Obrajuelo", "Zacatecoluca",
                /*31 Cabañas Este **/"Sensuntepeque", "Victoria", "Dolores", "Guacotecti", "San Isidro",
                /*32 Cabañas Oeste **/"Ilobasco", "Tejutepeque", "Jutiapa", "Cinquera",
                /*33 San Vicente Norte **/"Apastequepe", "Santa Clara", "San Ildefonso", "San Esteban Catarina", "San Sebastian", "San Lorenzo", "Santo Domingo",
                /*34 San Vicente Sur */"San Vicente", "Guadalupe", "Verapaz", "Nuevo Tepetitán", "Tecoluca", "San Cayetano Istepeque",
                /*35 Usulután norte **/"Santiago de María", "Alegría", "Berlín", "Mercedes Umaña", "Jucuapa", "El Triunfo", "Estanzuelas", "San Buenaventura", "Nueva Guadalupe",
                /*36 Usulután Este **/"Usulután", "Jucuarán", "San Dionisio", "Concepción Batres", "Santa María", "Ozatlan", "Tecapán", "Santa Elena", "California", "Ereguayquín",
                /*37 Usulután Oeste **/"Jiquilisco", "Puerto El Triunfo", "San Agustín", "San Francisco Javier",
                /*38 San Miguel norte **/"Ciudad Barrios", "Sesori", "Nuevo Edén de San Juan", "San Gerardo", "San Luis La Reina", "Carolina", "San Antonio del Mosco", "Chapeltique",
                /*39 San Miguel Centro **/"San Miguel", "Comacarán", "Uluazapa", "Moncagua", "Quelepa", "Chirilagua",
                /*40 San Miguel Oeste **/"Chinameca", "Nueva Guadaluoe", "Lolotique", "San Jorge", "San Rael Oriente", "El Tránsito",
                /*41 Morazan norte **/"Arambala", "Cacaopera", "Corinto", "El Rosario", "Joacateca", "Jocoaitique", "Meanguera", "Perquin", "San Fernando", "San Isidro", "Torola",
                /*42 Morazan sur **/"Chilanga", "Delicias de Concepción", "El Divisadero", "Gualococti", "Guatajiagua", "Jocoro", "Lolotiquillo", "Osicala", "San Carlos", "San Francisco Gotera", "San Simón", "Sensembra", "Sociedad", "Yamabal", "Yoloaiquín",
                /*43 La union norte **/"Anamorós", "Bolívar", "Concepción de Oriente", "El Sauce", "Lislique", "Nueva Esparta", "Pasaquina", "Polorós", "San José La Fuente", "Santa Rosa de Lima",
                /*44 La union sur **/"Conchagua", "El Carmen", "Intipucá", "La Unión", "Meanguera del Golfo", "San Alejo", "Yayantique", "Yucuaquín"

        };
        final String [] VEstado={"Activo","Cancelado","Realizado"};
        abrir();
        db.execSQL("DELETE FROM distrito");
        db.execSQL("DELETE FROM municipio");
        db.execSQL("DELETE FROM departamento");

        Departamento departamento = new Departamento();
        for (int i = 0; i < 14; i++) {
            departamento.setIdDepartamento(VDidDepartamento[i]);
            departamento.setNombreDepartamento(VDnombreDepartamento[i]);
            insertar(departamento);

        }
        Municipio municipio = new Municipio();
        for (int i = 0; i < 44; i++) {
            municipio.setIdMunicipio(VMidMunicipio[i]);
            municipio.setIdDepartamento(VMidDepartamento[i]);
            municipio.setNombreMunicipio(VMnombreMunicipio[i]);
            insertar(municipio);
        }
        Distrito distrito = new Distrito();
        for (int i = 0; i < VInombreDistrito.length; i++) {
            distrito.setIdDistrito(VIidDistrito[i]);
            distrito.setIdMunicipio(VIidMunicipio[i]);
            distrito.setNombreDistrito(VInombreDistrito[i]);
            insertar(distrito);
        }
        EstadoOrden estadoOrden= new EstadoOrden();
        for (int i=0; i< VEstado.length; i++){
            estadoOrden.setTipo(VEstado[i]);
            insertar(estadoOrden);
        }

        cerrar();

        return "Guardado correctamente";
    }

    public String insertar(EstadoOrden estadoOrden){
    String regInsertados = "Registros insertados N° = ";
    long contador;
    ContentValues est= new ContentValues();
    est.put("tipoEstado", estadoOrden.getTipo());

    contador = db.insert("EstadoOrden", null, est);
    if (contador == -1 || contador == 0) {
        regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar insercion";
    } else {
        regInsertados = regInsertados + contador;
    }
    return regInsertados;

    }

    public String insertar(Cliente cliente) {
        String regInsertados = "Registros insertados N°= ";
        long contador;
        ContentValues clien = new ContentValues();
        clien.put("nombreCliente", cliente.getNombre());
        clien.put("apellidoCliente", cliente.getApellido());
        clien.put("sexoCliente", cliente.getSexo());
        clien.put("correoCliente", cliente.getCorreo());
        clien.put("contraCliente", cliente.getContra());
        clien.put("fechaNacimientoC", cliente.getNacimiento());

        contador = db.insert("Cliente", null, clien);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }

    //********************************************************************************************
    //***                        INSERSICION DE USUARIO EN SQLITE                              ***
    //********************************************************************************************
    public String insertar(Usuario usuario) {
        String regInsertados = "Registros insertados N°= ";
        long contador;
        ContentValues user = new ContentValues();
        user.put("idUsuario", usuario.getIdUsuario() );
        user.put("nombreUsuario", usuario.getNombreUsuario());
        user.put("clave", usuario.getClave());
        user.put("rol", usuario.getRol());
        user.put("estado", usuario.getEstado());

        contador = db.insert("Usuario", null, user);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }

    //********************************************************************************************

    public String insertar(Administrador administrador) {
        String regInsertados = "Registros insertados N°= ";
        long contador;
        ContentValues admi = new ContentValues();
        admi.put("nombreAdmin", administrador.getNombre());
        admi.put("apellidoAdmin", administrador.getApellido());
        admi.put("sexoAdministrador", administrador.getSexo());
        admi.put("correoAdministrador", administrador.getCorreo());
        admi.put("contraAdministrador", administrador.getContra());
        admi.put("fechaNacimientoAdmin", administrador.getNacimiento());

        contador = db.insert("Administrador", null, admi);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }

    public String insertar(Repartidor repartidor) {
        String regInsertados = "Registros insertados N°= ";
        long contador;
        ContentValues rep = new ContentValues();
        rep.put("nombreRepartidor", repartidor.getNombre());
        rep.put("apellidoRepartidor", repartidor.getApellido());
        rep.put("sexoRepartidor", repartidor.getSexo());
        rep.put("correoRepartidor", repartidor.getCorreo());
        rep.put("contraRepartidor", repartidor.getContra());
        rep.put("fechaNacimientoR", repartidor.getNacimiento());

        contador = db.insert("Repartidor", null, rep);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }

    public String insertar (MetodoPago metodoPago){
        String regInsertados= "Registros insertados N°= ";
        long contador;

        //Se recupera el último ID de pago utilizado
        Cursor cursor = db.rawQuery("SELECT MAX(idPago) FROM MetodoPago", null);
        int ultimoID = 0;
        if (cursor.moveToFirst()) {
            ultimoID = cursor.getInt(0);
        }
        cursor.close();

        //Se incrementa el último ID para asignar el nuevo ID
        metodoPago.setId(ultimoID + 1);

        //Para insertar el nuevo registro en la base de datos
        ContentValues user = new ContentValues();
        user.put("idPago", metodoPago.getId());
        user.put("tipoPago", metodoPago.getTipoPago());

        contador= db.insert("MetodoPago", null, user);
        if(contador==-1 || contador==0){
            regInsertados="Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        } else {
            regInsertados = regInsertados + contador;
        }
        return regInsertados;
    }

    public String insertar(Factura factura, Pedido pedido){
        String regInsertados = "Registros insertados N°= ";
        long contador;

        // Recuperar el último ID de factura utilizado
        Cursor cursorFactura = db.rawQuery("SELECT MAX(idFactura) FROM Factura", null);
        int ultimoIDFactura = 0;
        if (cursorFactura.moveToFirst()) {
            ultimoIDFactura = cursorFactura.getInt(0);
        }
        cursorFactura.close();

        // Incrementar el último ID para asignar el nuevo ID a la factura
        factura.setId(ultimoIDFactura + 1);

        // Insertar el pago realizado en la base de datos
        insertar(factura.getMetodoPago());

        // Llenar la factura con sus respectivos datos
        ContentValues valuesFactura = new ContentValues();
        valuesFactura.put("idFactura", factura.getId());
        valuesFactura.put("idPago", factura.getMetodoPago().getId());
        valuesFactura.put("totalPagado", factura.getTotalPagado());
        valuesFactura.put("fechaEmision", String.valueOf(factura.getFechaEmision()));

        // Insertar la nueva factura en la base de datos
        contador = db.insert("Factura", null, valuesFactura);
        if (contador == -1 || contador == 0) {
            regInsertados = "Error al Insertar el registro, Registro Duplicado. Verificar insercion";
        } else {
            regInsertados = regInsertados + contador;
        }

        //Insertar Ubicacion y Pedido
        long idUbicacion = insertar(pedido.getUbicacion());
        ContentValues pedidoValues = new ContentValues();
        pedidoValues.put("idUbicacion", idUbicacion);
        pedidoValues.put("idEstado", 1);
        pedidoValues.put("idCliente",Integer.parseInt(pedido.getCliente().getIdCliente()));
        pedidoValues.put("idFactura", factura.getId());
        pedidoValues.put("totalApagar", pedido.getTotalAPagar());
        pedidoValues.put("fechaPedido", pedido.getFechaPedido().toString());
        pedidoValues.put("descripcionOrden", pedido.getDescripcionOrden());
        long id =  db.insert("Pedido", null, pedidoValues);

//        String sql = "INSERT INTO Pedido (idUbicacion, idEstado, idCliente, idFactura, totalApagar, fechaPedido, descripcionOrden) \n" +
//                "VALUES (?,?,?,?,?,?,?);";
//        String[] valores = new String[]{Long.toString(idUbicacion), Integer.toString(1), pedido.getCliente().getIdCliente(),
//        null ,Integer.toString(factura.getId()), Float.toString(pedido.getTotalAPagar()), pedido.getFechaPedido().toString(),
//                pedido.getDescripcionOrden()};

//        db.rawQuery(sql, valores);

        // Actualizar el ID de la factura en el pedido correspondiente
//        ContentValues valuesPedido = new ContentValues();
//        valuesPedido.put("idFactura", factura.getId());
//        String whereClause = "idPedido = ?";
//        String[] whereArgs = {String.valueOf(pedido.getId())};
//        contador = db.update("Pedido", valuesPedido, whereClause, whereArgs);

        return regInsertados;
    }


    public Cliente consultarCliente (String correo)
    {
        String[] identificador = {correo};
        Cursor cursor = db.query("Cliente", camposCliente, "correoCliente = ?", identificador, null, null, null);
        if (cursor.moveToFirst()) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(cursor.getString(0));
            cliente.setCorreo(cursor.getString(4));
            cliente.setContra(cursor.getString(5));
            return cliente;
        } else {
            return null;
        }

    }

    //********************************************************************************************
    //***                        CONSULTAR USUARIO POR SU CORREO                               ***
    //********************************************************************************************
    public Usuario consultarUsuario(String correo) {
        String[] id = {correo};
        Cursor cursor = db.query("Usuario", camposUsuario, "nombreUsuario = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(cursor.getString(1));
            usuario.setClave(cursor.getString(2));
            return usuario;
        } else {
            return null;
        }
    }

    //********************************************************************************************
    //***                        CONSULTAR USUARIO POR SU ESTADO                               ***
    //********************************************************************************************
    public Usuario consultarUsuarioActivo(String estado) {
        String[] state = {estado};
        Cursor cursor = db.query("Usuario", camposUsuario, "estado = ?", state, null, null, null);
        if (cursor.moveToFirst()) {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(cursor.getString(0));
            usuario.setNombreUsuario(cursor.getString(1));
            usuario.setClave(cursor.getString(2));
            usuario.setRol(cursor.getString(3));
            return usuario;
        } else {
            return null;
        }
    }
    //**********************************************************************************************

    public Administrador consultarAdministrador(String correo) {
        String[] id = {correo};
        Cursor cursor = db.query("Administrador", camposAdministrador, "correoAdministrador = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Administrador admi = new Administrador();
            admi.setIdAdministrador(cursor.getString(0));
            admi.setCorreo(cursor.getString(4));
            admi.setContra(cursor.getString(5));
            return admi;
        } else {
            return null;
        }
    }

    public Repartidor consultarRepartidor(String correo) {
        String[] id = {correo};
        Cursor cursor = db.query("Repartidor", camposRepartidor, "correoRepartidor = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Repartidor rep = new Repartidor();
            rep.setIdRepartidor(cursor.getString(0));
            rep.setCorreo(cursor.getString(4));
            rep.setContra(cursor.getString(5));
            return rep;
        } else {
            return null;
        }
    }

    //Inserto en ubicacion
    public long insertar(Ubicacion ubicacion) {
        ContentValues ubi = new ContentValues();
        ubi.put("idDistrito", ubicacion.getDistrito().getIdDistrito());
        ubi.put("descripcionUbicacion", ubicacion.getDescripcion());
        long idNuevoUbicacion = db.insert("Ubicacion", null, ubi);
        return idNuevoUbicacion;
    }

    public Distrito consultarDistrito(String distritoNombre) {
        String[] id = {distritoNombre};
        Cursor cursor = db.query("Distrito", camposDistrito, "nombreDistrito = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Distrito dist = new Distrito();
            dist.setIdDistrito(cursor.getInt(0));
            return dist;
        } else {
            return null;
        }
    }

    //Consulta el distrito por medio del ID
    public Distrito consultarDistritoId(String idDistrito) {
        String[] id = {idDistrito};
        Cursor cursor = db.query("Distrito", camposDistrito, "idDistrito = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Distrito dist = new Distrito();
            dist.setIdMunicipio(cursor.getInt(1));
            dist.setNombreDistrito(cursor.getString(2));
            return dist;
        } else {
            return null;
        }
    }

    //Consulta el municipio por medio del ID
    public Municipio consultarMunicipioId(String idMunicipio) {
        String[] id = {idMunicipio};
        Cursor cursor = db.query("Municipio", camposMunicipios, "idMunicipio = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Municipio muni= new Municipio();
            muni.setIdDepartamento(cursor.getInt(1));
            muni.setNombreMunicipio(cursor.getString(2));
            return muni;
        } else {
            return null;
        }
    }

    //Consulta el departamento por medio del ID
    public Departamento consultarDepartamentooId(String idDepartamento) {
        String[] id = {idDepartamento};
        Cursor cursor = db.query("Departamento", camposDepartamento, "idDepartamento = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Departamento dep = new Departamento();
            dep.setNombreDepartamento(cursor.getString(1));
            return dep;
        } else {
            return null;
        }
    }

    //Consulta Ubicacion por medio del ID
    public Ubicacion consultarUbicacion(String idUbicacion) {
        String[] id = {idUbicacion};
        Cursor cursor = db.query("Ubicacion", camposUbicacion, "idUbicacion = ?", id, null, null, null);
        if (cursor.moveToFirst()) {
            Ubicacion ubi = new Ubicacion();
            Distrito distrito = new Distrito();
            distrito.setIdDistrito(cursor.getInt(1)); //Obtenemos el id del Distrito
            ubi.setDistrito(distrito);
            ubi.setDescripcion(cursor.getString(2));  // Obtenemos la descripcion
            return ubi;
        } else {
            return null;
        }
    }

    public void insertarSeEncuetra(int idCliente, int idUbicacion){
        ContentValues seEncuentra = new ContentValues();
        //ubi.put("idDistrito", ubicacion.getDistrito().getIdDistrito());
        seEncuentra.put("idCliente", idCliente);
        seEncuentra.put("idUbicacion",idUbicacion);
        long contador;
        contador = db.insert("Se_Encuentra", null, seEncuentra);
        if (contador == -1 || contador == 0) {
        } else {
        }
    }


    public void LimpiarUsuario() {
        db.execSQL("DELETE FROM Usuario");
    }
}

