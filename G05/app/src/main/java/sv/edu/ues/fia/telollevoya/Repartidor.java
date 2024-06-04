package sv.edu.ues.fia.telollevoya;
public class Repartidor {

    private String idRepartidor;
    private String nombre;
    private String apellido;
    private String correo;
    private String sexo;
    private String contra;
    private String nacimiento;



    public Repartidor() {
    }

    public Repartidor(String nombre, String apellido, String correo, String sexo, String contra, String nacimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.sexo = sexo;
        this.contra = contra;
        this.nacimiento = nacimiento;
    }

    public String getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(String idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(String nacimiento) {
        this.nacimiento = nacimiento;
    }

    @Override
    public String toString(){
        return getNombre() + " "+ getApellido();
    }
}
