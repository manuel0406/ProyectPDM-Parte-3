package sv.edu.ues.fia.telollevoya;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pedido implements Serializable {
    private int id;
    private EstadoOrden estadoOrden;
    private Factura factura;
    private Cliente cliente;
    private Repartidor repartidor;
    private Ubicacion ubicacion;
    private ArrayList<DetallePedido> detallePedidoList;
    private float totalAPagar;
    private String tiempoEstimado;
    private Date fechaPedido;
    private Date fechaEntrega;
    private String descripcionOrden;
    private float costoEnvio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getTotalAPagar() {
        return totalAPagar;
    }

    public void setTotalAPagar(float totalAPagar) {
        this.totalAPagar = totalAPagar;
    }

    public String getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(String tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getDescripcionOrden() {
        return descripcionOrden;
    }

    public void setDescripcionOrden(String descripcionOrden) {
        this.descripcionOrden = descripcionOrden;
    }

    public EstadoOrden getEstadoOrden() {
        return estadoOrden;
    }

    public void setEstadoOrden(EstadoOrden estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public ArrayList<DetallePedido> getDetallePedidoList() {
        return detallePedidoList;
    }

    public void setDetallePedidoList(ArrayList<DetallePedido> detallePedidoList) {
        this.detallePedidoList = detallePedidoList;
    }

    public float getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(float costoEnvio) {
        this.costoEnvio = costoEnvio;
    }
}