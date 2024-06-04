package sv.edu.ues.fia.telollevoya;

import java.io.Serializable;

public class EstadoOrden implements Serializable {
    private int id;
    private String tipo;

    public EstadoOrden(){}
    public EstadoOrden(int id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
