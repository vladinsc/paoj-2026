package com.pao.laboratory06.exercise2;

import java.util.Locale;
import java.util.Scanner;

public abstract class Colaborator implements IOperatiiCitireScriere {
    protected String nume;
    protected String prenume;
    protected double venitBrutLunar;

    @Override
    public void citeste(Scanner in) {
        this.nume = in.next();
        this.prenume = in.next();
        this.venitBrutLunar = in.nextDouble();
    }

    public abstract double calculeazaVenitNetAnual();
    public abstract TipColaborator getTip();

    @Override
    public void afiseaza() {
        System.out.printf(Locale.US,"%s: %s %s, venit net anual: %.2f lei\n",
                tipContract(), nume, prenume, calculeazaVenitNetAnual());
    }
}

