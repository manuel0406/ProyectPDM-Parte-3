package sv.edu.ues.fia.telollevoya.pago;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
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
import sv.edu.ues.fia.telollevoya.Negocio;
import sv.edu.ues.fia.telollevoya.Pedido;
import sv.edu.ues.fia.telollevoya.R;
import sv.edu.ues.fia.telollevoya.negocio.negocio.EditarMiNegocioActivity;
import sv.edu.ues.fia.telollevoya.negocio.negocio.Restaurant;

public class ServiciosFactura {

    static String correo;
    static String contra;
    static Session session;
    public static Pedido pedido = new Pedido();
    public static Restaurant negocio;
    static String correoUsuario;
    private static Cliente cliente;
    static final String rutaPDF = "Factura_Te_lo_Llevo_Ya.pdf";
    private static final String urlCliente = "https://telollevoya.000webhostapp.com/Pago/obtener_cliente_por_id.php?idCliente=";
    private static final String urlNegocio = "https://telollevoya.000webhostapp.com/Pago/obtener_negocio_por_id.php?idNegocio=";


    public static void generarFactura(Context context){

        // Obtenemos el cliente desde el servicio web
        String urlC = urlCliente + pedido.getCliente().getIdCliente();
        cliente = ControladorSevicio.obtenerClientePorId(urlC, context);

        // Obtenemos el negocio desde el servicio web
        String idNegocio = String.valueOf(pedido.getDetallePedidoList().get(0).getProducto().getIdNegocio());
        String urlN = urlNegocio + idNegocio;
        negocio = ControladorSevicio.obtenerNegocioPorId(urlN, context);


        //Generando factura
        correo = "telollevoya.suporte@gmail.com";
        contra = "fzqy mmju xfbi maro"; //no se debe cambiar

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        crearPDF(context); //creando pdf con la factura a enviar al cliente

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

            if(session!=null){
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correo));
                message.setSubject(context.getString(R.string.titulo_correo));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoUsuario));

                //Cuerpo del mensaje
                MimeBodyPart textoPart = new MimeBodyPart();
                String mensaje = context.getString(R.string.mensaje_correo, cliente.getNombre());
                textoPart.setText(mensaje);


                //Archivo adjunto (Factura del pedido en PDF)
                MimeBodyPart adjuntoPart = new MimeBodyPart();
                adjuntoPart.attachFile(new File(context.getFilesDir(), rutaPDF));

                //Construcción del cuerpo del mensaje
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

            //Para obtener el directorio de archivos internos de la aplicación
            File directorio = context.getFilesDir();

            //crear la ruta completa del archivo PDF dentro del directorio de archivos internos
            File archivoPDF = new File(directorio, "Factura_Te_lo_Llevo_Ya.pdf");

            //Para crear el archivo PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(archivoPDF));
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            //agregando estilos al pdf
            document.setBackgroundColor(new DeviceRgb(230, 230, 230)); // Color de fondo
            document.setFontSize(12); // Tamaño de letra
            document.setTextAlignment(TextAlignment.LEFT); // Alineación del texto

            //obteniendo el logo de la empresa desde la carpeta drawable
            Drawable drawable = context.getResources().getDrawable(R.drawable.logo_general);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            // Convertir el logo a un array de bytes
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] logoBytes = stream.toByteArray();

            // Agregar el logo al PDF
            Image logo = new Image(ImageDataFactory.create(logoBytes));
            logo.setWidth(150); // Ancho del logo (ajustar según sea necesario)
            //logo.setAutoScale(true);
            document.add(logo);

            List<DetallePedido> detallesPedido = new ArrayList<>();
            detallesPedido = pedido.getDetallePedidoList();

            double sumaTotal = pedido.getTotalAPagar();
            double sumaSubtotal = pedido.getTotalAPagar() - pedido.getCostoEnvio();
            String metodoPago = pedido.getFactura().getMetodoPago().toString();

            String direccion = pedido.getUbicacion().toString();
            Date fechaEntrega = pedido.getFechaEntrega();
            String fechaE;

            if (fechaEntrega != null) {
                fechaE = pedido.getFechaEntrega().toString();
            } else {
                // Si la fecha de entrega es nula, establece la fecha actual
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                fechaE = dateFormat.format(new Date());
            }

            //Agregando mensaje al pdf
            document.add(new Paragraph("\n" + context.getString(R.string.factura_numero) + pedido.getFactura().getId()));
            document.add(new Paragraph(context.getString(R.string.nit_empresa)));
            document.add(new Paragraph( context.getString(R.string.cliente)+ " " + cliente.getNombre() + " " + cliente.getApellido()));
            document.add(new Paragraph("\n" + context.getString(R.string.datos_negocio) + " "+negocio.getNombre()).setFontSize(11).setBold());
            document.add(new Paragraph(context.getString(R.string.ubicacion_facturaP)+" " + negocio.getDescripcionUbicacion()));
            document.add(new Paragraph(context.getString(R.string.telefono_contacto) +" "+ negocio.getTelefono()));
            document.add(new Paragraph("\n" + context.getString(R.string.detalles_pedido)).setFontSize(11).setBold());
            document.add(new Paragraph(context.getString(R.string.numero_pedido) +" "+ pedido.getId()));
            document.add(new Paragraph(context.getString(R.string.repartidor_facturaP) + pedido.getRepartidor().getNombre() + " " + pedido.getRepartidor().getApellido()));
            document.add(new Paragraph(context.getString(R.string.fecha_entrega_facturaP) +" "+ fechaE));
            document.add(new Paragraph(context.getString(R.string.direccion_entrega) +" "+ direccion));
            document.add(new Paragraph("\n" + context.getString(R.string.lista_productos)).setFontSize(11).setBold());

            //crear la tabla
            float[] columnWidths = {200f, 100f, 100f, 100f};
            Table table = new Table(columnWidths);
            // Agregar encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph(context.getString(R.string.descripcion))));
            table.addHeaderCell(new Cell().add(new Paragraph(context.getString(R.string.cantidad))));
            table.addHeaderCell(new Cell().add(new Paragraph(context.getString(R.string.precio_unitario))));
            table.addHeaderCell(new Cell().add(new Paragraph(context.getString(R.string.total))));
            // Agregar detalles del pedido a la tabla
            for (DetallePedido detalle : detallesPedido) {
                table.addCell(new Cell().add(new Paragraph(detalle.getProducto().getNombre())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detalle.getCantidad()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", detalle.getProducto().getPrecio()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", detalle.getSubTotal()))));
            }

            Cell metodoPagoLabelCell = new Cell()
                    .add(new Paragraph(context.getString(R.string.metodo_pago)))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            Cell metodoPagoValueCell = new Cell()
                    .add(new Paragraph(metodoPago))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            Cell totalesLabelCell = new Cell()
                    .add(new Paragraph(context.getString(R.string.subtotal) + "\n" +
                            context.getString(R.string.costo_envio) + "\n" +
                            context.getString(R.string.pago_total)))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            Cell totalesValueCell = new Cell()
                    .add(new Paragraph(String.format("$%.2f\n$%.2f\n$%.2f", sumaSubtotal, pedido.getCostoEnvio(), sumaTotal)))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            //se agregan las celdas a la tabla en una nueva fila
            table.addCell(metodoPagoLabelCell);
            table.addCell(metodoPagoValueCell);
            table.addCell(totalesLabelCell);
            table.addCell(totalesValueCell);


            // Se agrega la tabla al documento
            document.add(table);

            document.add(new Paragraph("\n" + context.getString(R.string.comentario_factura)));

            // Se cierra el documento
            document.close();

            // Mostrar mensaje de éxito en consola
            System.out.println("PDF creado exitosamente en: " + archivoPDF.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}