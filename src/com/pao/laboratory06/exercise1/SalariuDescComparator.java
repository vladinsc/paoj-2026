package com.pao.laboratory06.exercise1;

import java.util.Comparator;

public class SalariuDescComparator implements Comparator<Angajat> {
    @Override
    public int compare(Angajat o1, Angajat o2) {
        return Double.compare(o2.getSalariu(), o1.getSalariu());
    }
}
