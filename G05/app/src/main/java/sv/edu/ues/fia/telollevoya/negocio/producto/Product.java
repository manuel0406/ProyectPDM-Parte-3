package sv.edu.ues.fia.telollevoya.negocio.producto;

public class Product {

    private int idNegocio;
    private int idProducto;
    private String nombreProducto, tipoProducto, descripcionProducto;
    private float precioProducto;
    private boolean existenciaProducto;

    public Product() {

    }

    public int getIdNegocio() {
        return idNegocio;
    }

    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public float getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(float precioProducto) {
        this.precioProducto = precioProducto;
    }

    public boolean isExistenciaProducto() {
        return existenciaProducto;
    }

    public void setExistenciaProducto(boolean existenciaProducto) {
        this.existenciaProducto = existenciaProducto;
    }

}
