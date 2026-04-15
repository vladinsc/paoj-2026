package com.pao.laboratory07.exercise2;

import com.pao.laboratory07.exercise1.OrderState;
import java.util.Locale;

public abstract sealed class Comanda permits ComandaStandard, ComandaRedusa, ComandaGratuita {
    protected String nume;
    protected OrderState stare;

    public Comanda(String nume) {
        this.nume = nume;
        this.stare = OrderState.PLACED;
    }

    public abstract double pretFinal();

    public String descriere() {
        return switch (this) {
            case ComandaStandard cs ->
                    String.format(Locale.US, "STANDARD: %s, pret: %.2f lei [%s]", cs.nume, cs.pretFinal(), cs.stare);
            case ComandaRedusa cr ->
                    String.format(Locale.US, "DISCOUNTED: %s, pret: %.2f lei (-%d%%) [%s]", cr.nume, cr.pretFinal(), cr.getDiscountProcent(), cr.stare);
            case ComandaGratuita cg ->
                    String.format("GIFT: %s, gratuit [%s]", cg.nume, cg.stare);
        };
    }
}