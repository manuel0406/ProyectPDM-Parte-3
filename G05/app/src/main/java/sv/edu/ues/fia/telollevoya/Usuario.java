package sv.edu.ues.fia.telollevoya;

public class Usuario {
    private String nombreUsuario;
    private String clave;
    private String rol;
    private String estado;
    private String idUsuario;

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String clave, String rol, String estado, String idUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.rol = rol;
        this.estado = estado;
        this.idUsuario = idUsuario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
