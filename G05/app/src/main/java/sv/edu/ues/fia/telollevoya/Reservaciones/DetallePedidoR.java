package sv.edu.ues.fia.telollevoya.Reservaciones;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DetallePedidoR implements Parcelable {
    private int idDetallePedido;
    private int idProducto;
    private int idReservacion;
    private int cantidadDetalle;
    private double subTotal;


    public DetallePedidoR() {
    }

    public DetallePedidoR(int idDetallePedido, int idProducto, int idReservacion, int cantidadDetalle, double subTotal) {
        this.idDetallePedido = idDetallePedido;
        this.idProducto = idProducto;
        this.idReservacion = idReservacion;
        this.cantidadDetalle = cantidadDetalle;
        this.subTotal = subTotal;
    }

    protected DetallePedidoR(Parcel in) {
        idDetallePedido = in.readInt();
        idProducto = in.readInt();
        idReservacion = in.readInt();
        cantidadDetalle = in.readInt();
        subTotal = in.readDouble();
    }

    public static final Creator<DetallePedidoR> CREATOR = new Creator<DetallePedidoR>() {
        @Override
        public DetallePedidoR createFromParcel(Parcel in) {
            return new DetallePedidoR(in);
        }

        @Override
        public DetallePedidoR[] newArray(int size) {
            return new DetallePedidoR[size];
        }
    };

    public int getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(int idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdReservacion() {
        return idReservacion;
    }

    public void setIdReservacion(int idReservacion) {
        this.idReservacion = idReservacion;
    }

    public int getCantidadDetalle() {
        return cantidadDetalle;
    }

    public void setCantidadDetalle(int cantidadDetalle) {
        this.cantidadDetalle = cantidadDetalle;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(idDetallePedido);
        dest.writeInt(idProducto);
        dest.writeInt(idReservacion);
        dest.writeInt(cantidadDetalle);
        dest.writeDouble(subTotal);
    }
}
