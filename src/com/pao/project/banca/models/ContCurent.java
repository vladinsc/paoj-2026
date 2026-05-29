package com.pao.project.banca.models;

import static com.pao.project.banca.models.Moneda.RON;

public class ContCurent extends Cont {
    private double limitaOverdraft;
    private double comisionAdministrare;

    public ContCurent(String iban, String idClient, Moneda moneda, String numeBanca, double limitaOverdraft, double comisionAdministrare) {
        super(iban, idClient, moneda, numeBanca);
        this.limitaOverdraft = limitaOverdraft;
        this.comisionAdministrare = comisionAdministrare;
    }
    public ContCurent(String iban, String idClient, Moneda moneda, String numeBanca) {
        super(iban, idClient, moneda, numeBanca);
        this.limitaOverdraft = 0.0;
        this.comisionAdministrare = 50.0;
    }
    @Override
    public String getTipCont() {
        return "Cont Curent";
    }

    public double getLimitaOverdraft() {
        return limitaOverdraft;
    }

    public void setLimitaOverdraft(double limitaOverdraft) {
        this.limitaOverdraft = limitaOverdraft;
    }

    public double getComisionAdministrare() {
        return comisionAdministrare;
    }

    public void setComisionAdministrare(double comisionAdministrare) {
        this.comisionAdministrare = comisionAdministrare;
    }

    public double getSoldDisponibil() {
        return sold + limitaOverdraft;
    }
    
    @Override
    public String toString() {
        return String.format("[CONT CURENT] IBAN: %s | Sold: %.2f %s | Overdraft: %.2f %s | Disponibil: %.2f %s | Activ: %s | Banca: %s",
                iban, sold, moneda, limitaOverdraft, moneda, getSoldDisponibil(), moneda, activ ? "DA" : "NU", numeBanca);
    }
}
