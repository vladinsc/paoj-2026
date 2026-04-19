package com.pao.project.banca.models;

import java.util.Objects;

public class Adresa {
    private String strada;
    private String numar;
    private String oras;
    private String judet;
    private String codPostal;
    private String tara;

    public Adresa(String strada,  String numar, String oras, String judet, String codPostal, String tara) {
        this.strada = strada;
        this.numar = numar;
        this.oras = oras;
        this.judet = judet;
        this.codPostal = codPostal;
        this.tara = tara;
    }
    public String getStrada() {return strada;}
    public String getNumar() {return numar;}
    public String getOras() {return oras;}
    public String getJudet() {return judet;}
    public String getCodPostal() {return codPostal;}
    public String getTara() {return tara;}
    public void setStrada(String strada) {this.strada = strada;}
    public void setNumar(String numar) {this.numar = numar;}
    public void setOras(String oras) {this.oras = oras;}
    public void setJudet(String judet) {this.judet = judet;}
    public void setCodPostal(String codPostal) {this.codPostal = codPostal;}
    public void setTara(String tara) {this.tara = tara;}
    @Override
    public String toString() {
        return "Str. " + strada + "nr. " + numar + ", " + oras + ", " + judet + ", " + codPostal + ", " + tara;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adresa a = (Adresa) o;
        return Objects.equals(strada, a.strada) && Objects.equals(numar, a.numar) && Objects.equals(oras, a.oras) && Objects.equals(codPostal, a.codPostal) && Objects.equals(tara, a.tara);

    }
    @Override
    public int hashCode() {
        return Objects.hash(strada, numar, oras, judet, codPostal, tara);
    }
}
