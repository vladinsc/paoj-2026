package com.pao.laboratory06.exercise2;

import com.pao.test.IOTest;

import java.util.Scanner;

public class CIMColaborator extends Colaborator implements PersoanaFizica{
    private boolean bonus;

    @Override
    public void citeste(Scanner in) {
        super.citeste(in);
        if (in.hasNext("DA") || in.hasNext("NU")) {
            String b = in.next();
            this.bonus = b.equals("DA");
        } else {
            this.bonus = false;
        }
    }

    @Override
    public boolean areBonus() {
        return bonus;
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double net = venitBrutLunar * 12 * 0.55;
        if (areBonus()) {
            net += net * 0.10;
        }
        return net;
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.CIM;
    }

    @Override
    public String tipContract() {
        return "CIM";
    }
}

