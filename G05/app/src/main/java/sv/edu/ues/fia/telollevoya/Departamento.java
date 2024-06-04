package sv.edu.ues.fia.telollevoya;

import java.io.Serializable;

public class Departamento implements Serializable {
    private int idDepartamento;
    private String nombreDepartamento;

    public Departamento() {
    }

    public Departamento(int idDepartamento, String nombreDepartamento) {
        this.idDepartamento = idDepartamento;
        this.nombreDepartamento = nombreDepartamento;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }
}
