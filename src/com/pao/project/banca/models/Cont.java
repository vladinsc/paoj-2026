package com.pao.project.banca.models;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public abstract class Cont {
    protected String iban;
    protected double sold;
    protected String idClient;
    protected LocalDate dataCreare;
    protected boolean activ;
    protected Moneda moneda;
    protected final List<Tranzactie> tranzactii;

    public Cont (String iban,String idClient,Moneda moneda) {
        this.iban = iban;
        this.idClient = idClient;
        this.moneda = moneda;
        this.dataCreare = LocalDate.now();
        this.activ = true;
        this.tranzactii = new ArrayList<>();
        this.sold = 0.0;

    }

    public abstract String getTipCont();
    //TODO
    //public abstract double getTotalInOtherMoneda(Moneda moneda);
    public void adaugaTranzactie(Tranzactie t) {
        tranzactii.add(t);
    }
    public List<Tranzactie> getTranzactii() {
        return Collections.unmodifiableList(tranzactii);
    }
    public int getNrTranzactii() {
        return tranzactii.size();
    }
    public String getIban()         { return iban; }
    public double getSold()         { return sold; }
    public String getIdClient()     { return idClient; }
    public LocalDate getDataCreare(){ return dataCreare; }
    public boolean isActiv()        { return activ; }
    public String getMoneda()       { return moneda.toString(); }
    public void setSold(double sold)   { this.sold = sold; }
    public void setActiv(boolean activ){ this.activ = activ; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cont)) return false;
        Cont c = (Cont) o;
        return Objects.equals(iban, c.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban);
    }

    @Override
    public String toString() {
        return String.format("[%s] IBAN: %s | Sold: %.2f %s | Activ: %s | Creat: %s",
                getTipCont(), iban, sold, moneda, activ ? "DA" : "NU", dataCreare);
    }
}
