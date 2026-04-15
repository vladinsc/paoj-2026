package com.pao.laboratory07.exercise3;

public final class ComandaGratuita extends Comanda {
    public ComandaGratuita(String nume, String client) {
        super(nume, client);
    }

    @Override
    public double pretFinal() {
        return 0.0;
    }
}