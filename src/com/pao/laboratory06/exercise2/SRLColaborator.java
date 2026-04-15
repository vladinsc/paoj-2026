package com.pao.laboratory06.exercise2;
import java.util.Scanner;
public class SRLColaborator extends Colaborator implements PersoanaJuridica{
    private double cheltuieliLunare;

    @Override
    public void citeste(Scanner in) {
        super.citeste(in);
        this.cheltuieliLunare = in.nextDouble();
    }

    @Override
    public double calculeazaVenitNetAnual() {
        return (venitBrutLunar - cheltuieliLunare) * 12 * 0.84;
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.SRL;
    }

    @Override
    public String tipContract() {
        return "SRL";
    }
}
