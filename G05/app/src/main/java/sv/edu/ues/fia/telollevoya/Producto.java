package sv.edu.ues.fia.telollevoya;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Producto implements Serializable {
    private int id;
    private Negocio negocio;
    private int idNegocio;
    private String nombre;
    private String tipo;
    private String descripcion;
    private float precio;
    private boolean existencia;

    public Producto(){}

    public Producto(int id, String nombre, String tipo, String descripcion, float precio, boolean existencia) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.existencia = existencia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public boolean isExistencia() {
        return existencia;
    }

    public void setExistencia(boolean existencia) {
        this.existencia = existencia;
    }

    public int getIdNegocio() {
        return idNegocio;
    }

    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }
}
