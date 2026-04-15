package com.pao.laboratory06.exercise3;

import java.util.ArrayList;
import java.util.List;

public class PersoanaJuridica extends Persoana implements PlataOnlineSMS {
    private List<String> smsTrimise;
    private double soldCont;

    public PersoanaJuridica(String nume, String prenume, String telefon, double soldInitial) {
        super(nume, prenume, telefon);
        this.smsTrimise = new ArrayList<>();
        this.soldCont = soldInitial;
    }

    @Override
    public void autentificare(String user, String parola) {
        if (user == null || user.trim().isEmpty() || parola == null || parola.trim().isEmpty()) {
            throw new IllegalArgumentException("User sau parola invalide pentru PJ.");
        }
        System.out.println("Persoana Juridica " + nume + " autentificata.");
    }

    @Override
    public double consultareSold() {
        return soldCont;
    }

    @Override
    public boolean efectuarePlata(double suma) {
        if (suma > 0 && soldCont >= suma) {
            soldCont -= suma;
            trimiteSMS("Ati efectuat o plata de " + suma + " lei.");
            return true;
        }
        return false;
    }

    @Override
    public boolean trimiteSMS(String mesaj) {
        if (this.telefon == null || this.telefon.trim().isEmpty()) {
            System.out.println("Nu se poate trimite SMS. Numar de telefon invalid pentru " + nume);
            return false;
        }
        if (mesaj == null || mesaj.trim().isEmpty()) {
            System.out.println("Mesajul SMS este invalid (null sau gol).");
            return false;
        }

        smsTrimise.add(mesaj);
        System.out.println("[SMS catre " + telefon + "]: " + mesaj);
        return true;
    }

    public List<String> getSmsTrimise() {
        return smsTrimise;
    }
}