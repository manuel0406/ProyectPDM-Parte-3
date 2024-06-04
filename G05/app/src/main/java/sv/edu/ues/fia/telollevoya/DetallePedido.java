package sv.edu.ues.fia.telollevoya;

import java.io.Serializable;

public class DetallePedido implements Serializable {

    private int id;
    private int cantidad;
    private int idReservacion;
    private float subTotal;


    private Producto producto;

    public DetallePedido() {
    }

    public DetallePedido(int cantidad, float subTotal) {
        this.cantidad = cantidad;
        this.subTotal = subTotal;
    }

    public DetallePedido(int id, int cantidad, float subTotal) {
        this.id = id;
        this.cantidad = cantidad;
        this.subTotal = subTotal;
    }

    public DetallePedido(int id, int cantidad, float subTotal, Producto producto) {
        this.id = id;
        this.cantidad = cantidad;
        this.subTotal = subTotal;
        this.producto = producto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getIdReservacion() {
        return idReservacion;
    }

    public void setIdReservacion(int idReservacion) {
        this.idReservacion = idReservacion;
    }
}
