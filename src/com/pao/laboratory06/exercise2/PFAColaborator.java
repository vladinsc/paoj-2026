package com.pao.laboratory06.exercise2;
import java.util.Scanner;

public class PFAColaborator extends Colaborator implements PersoanaFizica{
    private double cheltuieliLunare;

    @Override
    public void citeste(Scanner in) {
        super.citeste(in); // venitBrutLunar va stoca Venitul Lunar
        this.cheltuieliLunare = in.nextDouble();
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double venitNet = (venitBrutLunar - cheltuieliLunare) * 12;
        double impozit = 0.10 * venitNet;

        double salariuMinimAnual = 48600;
        double cass = 0;

        if (venitNet < 6 * salariuMinimAnual) {
            cass = 0.10 * (6 * salariuMinimAnual);
        } else if (venitNet <= 72 * salariuMinimAnual) {
            cass = 0.10 * venitNet;
        } else {
            cass = 0.10 * (72 * salariuMinimAnual);
        }

        double cas = 0;
        if (venitNet < 12 * salariuMinimAnual) {
            cas = 0;
        } else if (venitNet <= 24 * salariuMinimAnual) {
            cas = 0.25 * (12 * salariuMinimAnual);
        } else {
            cas = 0.25 * (24 * salariuMinimAnual);
        }

        return venitNet - impozit - cass - cas;
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.PFA;
    }

    @Override
    public String tipContract() {
        return "PFA";
    }
}
