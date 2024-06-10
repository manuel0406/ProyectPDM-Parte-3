package sv.edu.ues.fia.telollevoya.pago;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


import sv.edu.ues.fia.telollevoya.Cliente;
import sv.edu.ues.fia.telollevoya.ControladorSevicio;
import sv.edu.ues.fia.telollevoya.DetallePedido;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.Reservacion;
import sv.edu.ues.fia.telollevoya.Reservaciones.DetallePedidoR;
import sv.edu.ues.fia.telollevoya.pedidos.cliente.MisPedidosActivity;
import sv.edu.ues.fia.telollevoya.pedidos.negocio.NegociosActivity;

public class PagoAprobadoRActivity extends AppCompatActivity implements ControladorSevicio.ReservacionInsertListener {

    static Reservacion reservacion;
    Button btnVerResumen;
    static ArrayList<DetallePedidoR> detallesPedidoR;
    TextView txAgradecimiento;
    static String correo;
    static String contra;
    static Session session;
    static String correoUsuario;
    static final String rutaPDF = "Comprobante_Reservación.pdf"; // Definir la ruta del PDF
    private String urlReservacion = "https://telollevoya.000webhostapp.com/Reservaciones/reservacion_insert.php";
    private String urldetalle = "https://telollevoya.000webhostapp.com/Reservaciones/reservacion_detalle_insertar.php";
    private static final String urlCliente = "https://telollevoya.000webhostapp.com/Pago/obtener_cliente_por_id.php?idCliente=";
    static String urlProducto = "https://telollevoya.000webhostapp.com/Pago/obtener_producto_por_id.php?idProducto=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservacion_aprobada);

        txAgradecimiento = findViewById(R.id.txtAgradecimiento);
        btnVerResumen = findViewById(R.id.btnVerResumen);

        reservacion = (Reservacion) getIntent().getSerializableExtra("reservacion");
        detallesPedidoR = getIntent().getParcelableArrayListExtra("listaDetalle");

        Intent intent = getIntent();
        String correoElectronico = intent.getStringExtra("CORREO_ELECTRONICO");
        correoUsuario = correoElectronico;

        // Inicia la inserción de la reservación
        insertarReservacion();
    }

    public void verResumenDeReservacion(View view) {
        Intent intent = new Intent(this, FacturaReservacionActivity.class);
        intent.putExtra("reservacion", reservacion);
        intent.putParcelableArrayListExtra("listaDetalle", detallesPedidoR);
        startActivity(intent);
    }

    public void regresarAListaDeNegocios(View view) {
        Intent intent = new Intent(this, NegociosActivity.class);
        startActivity(intent);
        finish();
    }

    public void insertarReservacion() {
        String fechaEntregar = "";
        SimpleDateFormat dateFormatEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatSalida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            java.util.Date fecha = dateFormatEntrada.parse(reservacion.getFechaEntregaR());
            fechaEntregar = dateFormatSalida.format(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Codificación de los datos de la reservación
        String idClienteCodificado = "", descripcionCodificado = "", anticipoCodificado = "", montoPendienteCodificado = "",
                fechaEntregaCodificado = "", horaEntegaCodificado = "", totalReservacionCodificado = "";
        try {
            idClienteCodificado = URLEncoder.encode(String.valueOf(reservacion.getIdCliente()), "UTF-8");
            descripcionCodificado = URLEncoder.encode(reservacion.getDescripcionReservacion(), "UTF-8");
            anticipoCodificado = URLEncoder.encode(String.valueOf(reservacion.getAnticipoReservacion()), "UTF-8");
            montoPendienteCodificado = URLEncoder.encode(String.valueOf(reservacion.getMontoPediente()), "UTF-8");
            fechaEntregaCodificado = fechaEntregar;
            horaEntegaCodificado = URLEncoder.encode(reservacion.getHoraEntrega(), "UTF-8");
            totalReservacionCodificado = URLEncoder.encode(String.valueOf(reservacion.getTotalRerservacion()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = urlReservacion + "?IDCLIENTE=" + idClienteCodificado + "&DESCRIPCIONRESERVACION=" +
                descripcionCodificado + "&ANTICIPORESERVACION=" + anticipoCodificado + "&MONTOPENDIENTE=" +
                montoPendienteCodificado + "&FECHAENTREGAR=" + fechaEntregaCodificado + "&HORAENTREGAR=" + horaEntegaCodificado +
                "&TOTALRESERVACION=" + totalReservacionCodificado;

        Log.v("URL completa", url); // Añadir esta línea para verificar la URL completa
        ControladorSevicio.insertarReservacion(url, this, this);
    }

    @Override
    public void onReservacionInserted(int idReservacion) {
        Log.v("idReservacionInt", String.valueOf(idReservacion));
        reservacion.setIdReservacion(idReservacion);
        Log.v("DetallesPedido size", String.valueOf(detallesPedidoR.size()));
        insertarDetallePedido(idReservacion);

        // Generar la factura una vez que se ha insertado la reservación y se tiene el ID correcto
        generarFactura(this);
    }

    public void insertarDetallePedido(int idReservacion){
        for (DetallePedidoR detallePedidoR : detallesPedidoR) {
            String idReservacionCodificado = "", idProductoCodificado = "", cantidadDetalleCodificado = "", subTotalCodificado = "";
            try {
                idReservacionCodificado = URLEncoder.encode(String.valueOf(idReservacion), "UTF-8");
                idProductoCodificado = URLEncoder.encode(String.valueOf(detallePedidoR.getIdProducto()), "UTF-8");
                cantidadDetalleCodificado = URLEncoder.encode(String.valueOf(detallePedidoR.getCantidadDetalle()), "UTF-8");
                subTotalCodificado = URLEncoder.encode(String.valueOf(detallePedidoR.getSubTotal()), "UTF-8");

                String url = urldetalle + "?IDRESERVACION=" + idReservacionCodificado + "&IDPRODUCTO=" +
                        idProductoCodificado + "&CANTIDADDETALLE=" + cantidadDetalleCodificado + "&SUBTOTAL=" + subTotalCodificado;

                Log.v("DetallePedidoR", detallePedidoR.toString());
                ControladorSevicio.insertarDetalle(url, this);
                Log.v("Url Detalle", url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    //****************** Envío de correo al aprobar pago de una reservación ******************//
    public static void generarFactura(Context context){
        //Obtenemos el cliente desde el servicio web
        String urlC = urlCliente + reservacion.getIdCliente();
        Cliente cliente = ControladorSevicio.obtenerClientePorId(urlC, context);

        //Generando Comprobante
        correo = "telollevoya.suporte@gmail.com";
        contra = "fzqy mmju xfbi maro"; //no se debe cambiar
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        crearPDF(context); // Creando PDF con la factura a enviar al cliente

        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","465");

        try{
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correo, contra);
                }
            });

            if(session != null){
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correo));
                message.setSubject(context.getString(R.string.titulo_correo_reservacion));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoUsuario));

                //Cuerpo del mensaje
                MimeBodyPart textoPart = new MimeBodyPart();
                textoPart.setText(context.getString(R.string.mensaje_correo_reservacion, cliente.getNombre()));

                //Archivo adjunto (PDF de Reservación)
                MimeBodyPart adjuntoPart = new MimeBodyPart();
                adjuntoPart.attachFile(new File(context.getFilesDir(), rutaPDF));

                //Construyendo el cuerpo del mensaje
                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(textoPart);
                multipart.addBodyPart(adjuntoPart);

                //Establecer el contenido del mensaje
                message.setContent(multipart);

                //Enviar el mensaje
                Transport.send(message);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void crearPDF(Context context) {
        try {
            File directorio = context.getFilesDir();

            File archivoPDF = new File(directorio, "Comprobante_Reservación.pdf");

            PdfWriter writer = new PdfWriter(new FileOutputStream(archivoPDF));
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            document.setBackgroundColor(new DeviceRgb(230, 230, 230)); // Color de fondo
            document.setFontSize(12); // Tamaño de letra
            document.setTextAlignment(TextAlignment.LEFT); // Alineación del texto

            //para obtener el logo de la empresa
            Drawable drawable = context.getResources().getDrawable(R.drawable.logo_general);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            //Convertir el logo a un array de bytes
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] logoBytes = stream.toByteArray();

            Image logo = new Image(ImageDataFactory.create(logoBytes));
            logo.setWidth(150); // Ancho del logo (ajustar según sea necesario)
            //logo.setAutoScale(true);
            document.add(logo);

            document.add(new Paragraph(context.getString(R.string.comprobante_reservacion)).setFontSize(15).setBold());
            document.add(new Paragraph(context.getString(R.string.numero_reservacion) + " " + reservacion.getIdReservacion()));
            document.add(new Paragraph(context.getString(R.string.descripcion) + " " + reservacion.getDescripcionReservacion()));
            document.add(new Paragraph(context.getString(R.string.fecha_entrega) + " " + reservacion.getFechaEntregaR() + " "+ context.getString(R.string.a_las) + " "+ reservacion.getHoraEntrega() + " hrs."));

            document.add(new Paragraph("\n" + context.getString(R.string.lista_productos)).setFontSize(11).setBold());

            //creando la tabla
            float[] columnWidths = {200f, 100f, 100f, 100f};
            Table table = new Table(columnWidths);

            //Para agregar encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph(context.getString(R.string.descripcion))));
            table.addHeaderCell(new Cell().add(new Paragraph(context.getString(R.string.cantidad))));
            table.addHeaderCell(new Cell().add(new Paragraph(context.getString(R.string.precio_unitario))));
            table.addHeaderCell(new Cell().add(new Paragraph(context.getString(R.string.total_factura))));

            ArrayList<DetallePedido> listaAdaptada = new ArrayList<>(); //la lista de productos que se mostrará
            for(int i = 0; i < detallesPedidoR.size(); i++){
                DetallePedido dp = new DetallePedido();
                dp.setId(detallesPedidoR.get(i).getIdDetallePedido());
                dp.setCantidad(detallesPedidoR.get(i).getCantidadDetalle());
                dp.setIdReservacion(detallesPedidoR.get(i).getIdReservacion());
                dp.setSubTotal((float)detallesPedidoR.get(i).getSubTotal());
                dp.setProducto(ControladorSevicio.obtenerProductoPorId(urlProducto + detallesPedidoR.get(i).getIdProducto(), context));
                listaAdaptada.add(dp);
            }

            //Agregar detalles del pedido a la tabla
            for (DetallePedido detalle : listaAdaptada) {
                table.addCell(new Cell().add(new Paragraph(detalle.getProducto().getNombre())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detalle.getCantidad()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", detalle.getProducto().getPrecio()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", detalle.getSubTotal()))));
            }

            Cell cell1 = new Cell()
                    .add(new Paragraph("   "))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            Cell cell2 = new Cell(1, 2)
                    .add(new Paragraph(context.getString(R.string.pago_total) + "\n" +
                            context.getString(R.string.monto_anticipacion) + "\n" +
                            context.getString(R.string.monto_pendiente)))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            Cell totalesValueCell = new Cell()
                    .add(new Paragraph(String.format("$%.2f\n$%.2f\n$%.2f", reservacion.getTotalRerservacion(), reservacion.getAnticipoReservacion(), reservacion.getMontoPediente())))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(totalesValueCell);

            //agregar la tabla al documento
            document.add(table);

            document.add(new Paragraph("\n" + context.getString(R.string.comentario_factura)));

            //cerrar el documento
            document.close();

            //mostrar mensaje de éxito
            System.out.println("PDF creado exitosamente en: " + archivoPDF.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


