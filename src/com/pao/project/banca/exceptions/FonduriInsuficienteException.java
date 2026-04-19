package com.pao.project.banca.exceptions;

public class FonduriInsuficienteException extends RuntimeException {
    private final double soldDisponibil;
    private final double sumaSolicitata;
    public FonduriInsuficienteException(double soldDisponibil, double sumaSolicitata) {
        super(String.format("Fonduri insuficiente! Sold disponibil: %.2f, Suma solicitata: %.2f.", soldDisponibil,  sumaSolicitata));
        this.soldDisponibil = soldDisponibil;
        this.sumaSolicitata = sumaSolicitata;
    }
    public double getSoldDisponibil() { return soldDisponibil; }
    public double getSumaSolicitata() { return sumaSolicitata; }
}
