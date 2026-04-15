package com.pao.laboratory07.exercise3;

import java.util.Locale;

public abstract sealed class Comanda permits ComandaStandard, ComandaRedusa, ComandaGratuita {
    protected String nume;
    protected String client;
    protected OrderState stare;

    public Comanda(String nume, String client) {
        this.nume = nume;
        this.client = client;
        this.stare = OrderState.PLACED;
    }

    public String getClient() {
        return client;
    }

    public abstract double pretFinal();


    public String descriere() {
        return switch (this) {
            case ComandaStandard cs ->
                    String.format(Locale.US, "STANDARD: %s, pret: %.2f lei [%s] - client: %s", cs.nume, cs.pretFinal(), cs.stare, cs.client);
            case ComandaRedusa cr ->
                    String.format(Locale.US, "DISCOUNTED: %s, pret: %.2f lei (-%d%%) [%s] - client: %s", cr.nume, cr.pretFinal(), cr.getDiscountProcent(), cr.stare, cr.client);
            case ComandaGratuita cg ->
                    String.format("GIFT: %s, gratuit [%s] - client: %s", cg.nume, cg.stare, cg.client);
        };
    }


    public String descriereFaraStare() {
        return switch (this) {
            case ComandaStandard cs ->
                    String.format(Locale.US, "STANDARD: %s, pret: %.2f lei - client: %s", cs.nume, cs.pretFinal(), cs.client);
            case ComandaRedusa cr ->
                    String.format(Locale.US, "DISCOUNTED: %s, pret: %.2f lei (-%d%%) - client: %s", cr.nume, cr.pretFinal(), cr.getDiscountProcent(), cr.client);
            case ComandaGratuita cg ->
                    String.format("GIFT: %s, gratuit - client: %s", cg.nume, cg.client);
        };
    }
}