package sv.edu.ues.fia.telollevoya;

import java.io.Serializable;

public class Reservacion implements Serializable {
    private int idReservacion;
    private int idCliente;
    private String descripcionReservacion;
    private String nombreNegocion;
    private double anticipoReservacion;
    private double montoPediente;
    private String fechaEntregaR;
    private String horaEntrega;
    private double totalRerservacion;

    public Reservacion(int i, String s, String space) {
        this.idReservacion = i;
        this.descripcionReservacion = s;
        this.fechaEntregaR = space;
    }
    public Reservacion() {

    }

    public Reservacion(int idReservacion, int idCliente, String descripcionReservacion, double anticipoReservacion, double montoPediente, String fechaEntregaR, double totalRerservacion) {
        this.idReservacion = idReservacion;
        this.idCliente = idCliente;
        this.descripcionReservacion = descripcionReservacion;
        this.anticipoReservacion = anticipoReservacion;
        this.montoPediente = montoPediente;
        this.fechaEntregaR = fechaEntregaR;
        this.totalRerservacion = totalRerservacion;
    }

    public String getNombreNegocion() {
        return nombreNegocion;
    }

    public void setNombreNegocion(String nombreNegocion) {
        this.nombreNegocion = nombreNegocion;
    }

    public int getIdReservacion() {
        return idReservacion;
    }

    public void setIdReservacion(int idReservacion) {
        this.idReservacion = idReservacion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDescripcionReservacion() {
        return descripcionReservacion;
    }

    public void setDescripcionReservacion(String descripcionReservacion) {
        this.descripcionReservacion = descripcionReservacion;
    }

    public double getAnticipoReservacion() {
        return anticipoReservacion;
    }

    public void setAnticipoReservacion(double anticipoReservacion) {
        this.anticipoReservacion = anticipoReservacion;
    }

    public double getMontoPediente() {
        return montoPediente;
    }

    public void setMontoPediente(double montoPediente) {
        this.montoPediente = montoPediente;
    }

    public String getFechaEntregaR() {
        return fechaEntregaR;
    }

    public void setFechaEntregaR(String fechaEntregaR) {
        this.fechaEntregaR = fechaEntregaR;
    }

    public String getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(String horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public double getTotalRerservacion() {
        return totalRerservacion;
    }

    public void setTotalRerservacion(double totalRerservacion) {
        this.totalRerservacion = totalRerservacion;
    }
}
