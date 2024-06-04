package sv.edu.ues.fia.telollevoya.pago;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class MetodoPago implements Serializable {

    private int id;
    private String tipoPago;

    public MetodoPago(int id, String tipoPago) {
        this.id = id;
        this.tipoPago = tipoPago;
    }

    public MetodoPago(int id){
        this.id = id;
    }
    public MetodoPago(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    @NonNull
    @Override
    public String toString() {
        return tipoPago;
    }
}
