package com.pao.project.banca.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Banca {
    private String denumire;
    private String codBic; // Bank Identifier Code
    private String prefixIban;
    private Adresa sediuCentral;
    private final List<Angajat> angajati;

    public Banca(String denumire, String codBic, String ibanPrefix) {
        this.denumire = denumire;
        this.codBic = codBic;
        this.prefixIban = ibanPrefix;
        this.angajati = new ArrayList<>();
    }
    public void angajeazaPersoana(Angajat angajat) {
        if (angajat != null && !angajati.contains(angajat)) {
            angajati.add(angajat);
        }
    }
    public  void concediazaAnagajat(String codAngajat){
        angajati.removeIf(angajat -> angajat.getCodAngajat().equals(codAngajat));
    }
    public List<Angajat> getAngajati() {
        return Collections.unmodifiableList(angajati);
    }
    public int getNrAngajati() {
        return angajati.size();
    }
    public String getDenumire()       { return denumire; }
    public String getCodBic()         { return codBic; }
    public String getPrefixIban()     { return prefixIban; }
    public Adresa getSediuCentral()   { return sediuCentral; }

    public void setDenumire(String denumire)        { this.denumire = denumire; }
    public void setSediuCentral(Adresa adresa)      { this.sediuCentral = adresa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Banca)) return false;
        Banca b = (Banca) o;
        return Objects.equals(codBic, b.codBic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codBic);
    }

    @Override
    public String toString() {
        return String.format("=== %s (BIC: %s) ===\n  Sediu: %s\n  Angajati: %d",
                denumire, codBic, sediuCentral, angajati.size());
    }
}
