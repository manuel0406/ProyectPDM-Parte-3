package sv.edu.ues.fia.telollevoya;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Ubicacion implements Serializable {
    private int id;
    private Distrito distrito;
    private String descripcion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @NonNull
    @Override
    public String toString() {
        return descripcion;
    }
}
