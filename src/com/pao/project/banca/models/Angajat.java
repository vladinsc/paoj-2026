package com.pao.project.banca.models;

import java.time.LocalDate;

public class Angajat extends Persoana {

    private String codAngajat;
    private String departament;
    private double salariu;
    private LocalDate dataAngajare;

    public Angajat(String id, String nume, String prenume, String cnp, String email, String tel, Adresa adresa, String codAngajat, String departament,  double salariu) {
        super(id, nume, prenume, cnp, email, tel, adresa);
        this.codAngajat = codAngajat;
        this.departament = departament;
        this.salariu = salariu;
        this.dataAngajare = LocalDate.now();
    }
    @Override
    public String getRol() {
        return "Angajat";
    }

    public String getCodAngajat() {
        return codAngajat;
    }
    public String getDepartament() {
        return departament;
    }
    public void setDepartament(String departament) {
        this.departament = departament;
    }
    public double getSalariu() {
        return salariu;
    }
    public void setSalariu(double salariu) {
        this.salariu = salariu;
    }
    public LocalDate getDataAngajare() {
        return dataAngajare;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s | Cod: %s | Dept: %s | Salariu: %.2f RON", getRol(), prenume, nume, codAngajat, departament, salariu);
    }

}
