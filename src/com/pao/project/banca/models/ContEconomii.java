package com.pao.project.banca.models;

public class ContEconomii extends Cont {
    private double rataDobandaAnuala;
    private double sumaMinimaObligatorie;

    public ContEconomii(String iban, String idClient, Moneda moneda) {
        super(iban, idClient, moneda);
        this.rataDobandaAnuala = 5.5;
        this.sumaMinimaObligatorie = 2500.0;
    }

    public ContEconomii(String iban, String idClient, Moneda moneda, double rataDobandaAnuala, double sumaMinimaObligatorie) {
        super(iban, idClient, moneda);
        this.rataDobandaAnuala = rataDobandaAnuala;
        this.sumaMinimaObligatorie = sumaMinimaObligatorie;
    }

    @Override
    public String getTipCont() {
        return "Cont Economii";
    }
    public double calculeazaDobanda(){
        if (sold < sumaMinimaObligatorie) {
            return 0.0;
        }
        return sold * rataDobandaAnuala / 100.0;
    }
    public double getRataDobandaAnuala()       { return rataDobandaAnuala; }
    public double getSumaMinimaObligatorie()   { return sumaMinimaObligatorie; }
    public void setRataDobandaAnuala(double rata)       { this.rataDobandaAnuala = rata; }
    public void setSumaMinimaObligatorie(double suma)   { this.sumaMinimaObligatorie = suma; }
    @Override
    public String toString() {
        return String.format("[CONT ECONOMII] IBAN: %s | Sold: %.2f RON | Dobanda: %.2f%% | Dobanda anuala estimata: %.2f RON | Activ: %s",
                iban, sold, rataDobandaAnuala, calculeazaDobanda(), activ ? "DA" : "NU");
    }
}
