package com.pao.laboratory07.exercise3;

public final class ComandaRedusa extends Comanda {
    private double pret;
    private int discountProcent;

    public ComandaRedusa(String nume, double pret, int discountProcent, String client) {
        super(nume, client);
        this.pret = pret;
        this.discountProcent = discountProcent;
    }

    @Override
    public double pretFinal() {
        return pret * (1 - discountProcent / 100.0);
    }

    public int getDiscountProcent() {
        return discountProcent;
    }
}