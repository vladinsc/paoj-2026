package com.pao.laboratory05.angajati;

import java.util.Arrays;

public class AngajatService {
    private Angajat[] angajati;

    private AngajatService() {
        this.angajati = new Angajat[0];
    }

    private static class Holder {
        private static final AngajatService INSTANCE = new AngajatService();
    }

    public static AngajatService getInstance() {
        return Holder.INSTANCE;
    }

    public void addAngajat(Angajat a) {
        Angajat[] noiAngajati = new Angajat[this.angajati.length + 1];
        System.arraycopy(this.angajati, 0, noiAngajati, 0, this.angajati.length);
        noiAngajati[noiAngajati.length - 1] = a;
        this.angajati = noiAngajati;

        System.out.println("Confirmare: Angajatul '" + a.getNume() + "' a fost adaugat.");
    }

    public void printAll() {
        for (Angajat a : this.angajati) {
            System.out.println(a);
        }
    }

    public void listBySalary() {
        Angajat[] copie = this.angajati.clone();
        Arrays.sort(copie);

        for (Angajat a : copie) {
            System.out.println(a);
        }
    }

    public void findByDepartament(String numeDept) {
        boolean gasit = false;

        for (Angajat a : this.angajati) {
            if (a.getDepartament().nume().equalsIgnoreCase(numeDept)) {
                System.out.println(a);
                gasit = true;
            }
        }

        if (!gasit) {
            System.out.println("Niciun angajat in departamentul: " + numeDept);
        }
    }
}