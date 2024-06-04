package sv.edu.ues.fia.telollevoya;

import java.io.Serializable;
import java.util.Date;

import sv.edu.ues.fia.telollevoya.pago.MetodoPago;

public class Factura implements Serializable {

    private int id;
    private float totalPagado;
    private Date fechaEmision;
    private MetodoPago metodoPago;

    public Factura(){}

    public Factura(int id, float totalPagado, Date fechaEmision) {
        this.id = id;
        this.totalPagado = totalPagado;
        this.fechaEmision = fechaEmision;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(float totalPagado) {
        this.totalPagado = totalPagado;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public MetodoPago getMetodoPago() { return metodoPago; }

    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
}
