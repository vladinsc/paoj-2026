package com.pao.laboratory06.exercise3;

public class Inginer extends Angajat implements PlataOnline, Comparable<Inginer> {
    private double soldCont;

    public Inginer(String nume, String prenume, String telefon, double salariu) {
        super(nume, prenume, telefon, salariu);
        this.soldCont = salariu;
    }

    @Override
    public void autentificare(String user, String parola) {
        if (user == null || user.trim().isEmpty() || parola == null || parola.trim().isEmpty()) {
            throw new IllegalArgumentException("User sau parola nu pot fi null sau goale.");
        }
        System.out.println("Inginerul " + nume + " s-a autentificat cu succes.");
    }

    @Override
    public double consultareSold() {
        return soldCont;
    }

    @Override
    public boolean efectuarePlata(double suma) {
        if (suma <= 0) return false;
        if (soldCont >= suma) {
            soldCont -= suma;
            System.out.println("Plata efectuata cu succes de " + nume);
            return true;
        }
        return false;
    }


    @Override
    public int compareTo(Inginer altInginer) {
        return this.nume.compareTo(altInginer.getNume());
    }

    @Override
    public String toString() {
        return "Inginer{" + "nume='" + nume + '\'' + ", salariu=" + salariu + '}';
    }
}