package com.pao.laboratory06.exercise1;

import java.util.Comparator;

public class NumeComparator implements Comparator<Angajat> {
    @Override
    public int compare(Angajat o1, Angajat o2) {
        return o1.getNume().compareTo(o2.getNume());
    }
}
