package sv.edu.ues.fia.telollevoya;

import java.io.Serializable;

public class Municipio implements Serializable {
    private int idMunicipio;
    private int idDepartamento;
    private  String nombreMunicipio;

    public Municipio() {
    }

    public Municipio(int idMunicipio, int idDepartamento, String nombreMunicipio) {
        this.idMunicipio = idMunicipio;
        this.idDepartamento = idDepartamento;
        this.nombreMunicipio = nombreMunicipio;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }
}
