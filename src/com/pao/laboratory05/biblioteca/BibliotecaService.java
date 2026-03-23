package com.pao.laboratory05.biblioteca;

import java.util.Arrays;
import java.util.Comparator;

public class BibliotecaService {
    private Carte[] carti;
    private BibliotecaService() {
        this.carti = new Carte[0];
    }
    private static class Holder {
        private static final BibliotecaService INSTANCE = new BibliotecaService();
    }

    public static BibliotecaService getInstance() {
        return Holder.INSTANCE;
    }

    public void addCarte(Carte carte) {
        Carte[] noiCarti = new Carte[this.carti.length + 1];
        System.arraycopy(this.carti, 0, noiCarti, 0, this.carti.length);
        noiCarti[noiCarti.length - 1] = carte;
        this.carti = noiCarti;

        System.out.println("Confirmare: A fost adaugata cartea '" + carte.getTitlu() + "'.");
    }

    public void listSortedByRating() {
        Carte[] copy = this.carti.clone();
        Arrays.sort(copy);

        for (Carte c : copy) {
            System.out.println(c);
        }
    }

    public void listSortedBy(Comparator<Carte> comparator) {
        Carte[] copy = this.carti.clone();
        Arrays.sort(copy, comparator);
        for (Carte c : copy) {
            System.out.println(c);
        }
    }
}