package com.pao.laboratory06.exercise3;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== 1. Demonstratie Constante Financiare ===");
        System.out.println("TVA Curent: " + ConstanteFinanciare.TVA.getValoare());
        System.out.println("Salariu Minim: " + ConstanteFinanciare.SALARIU_MINIM.getValoare() + "\n");

        System.out.println("=== 2. Sortare Ingineri ===");
        Inginer[] ingineri = {
                new Inginer("Zaharia", "Ion", "0711111111", 5000),
                new Inginer("Avram", "Vasile", "0722222222", 8000),
                new Inginer("Popescu", "Mihai", null, 6500)
        };

        System.out.println("Sortare naturala (dupa nume):");
        Arrays.sort(ingineri);
        for (Inginer i : ingineri) System.out.println(i);

        System.out.println("\nSortare folosind Comparator (dupa salariu descrescator):");
        Arrays.sort(ingineri, new ComparatorInginerSalariu());
        for (Inginer i : ingineri) System.out.println(i);


        System.out.println("\n=== 3. Acces prin referinta de tip PlataOnline ===");
        PlataOnline referintaInginer = ingineri[0];

        referintaInginer.autentificare("admin", "1234");
        System.out.println("Sold disponibil: " + referintaInginer.consultareSold());

        System.out.println("\n=== 4. Testare exceptii SMS si Autentificare ===");
        try {
            referintaInginer.trimiteSMS("Salut!");
        } catch (UnsupportedOperationException e) {
            System.out.println("Eroare prinsa (Corect): " + e.getMessage());
        }

        try {
            referintaInginer.autentificare("", null);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare prinsa (Corect): " + e.getMessage());
        }


        System.out.println("\n=== 5. Persoana Juridica (PlataOnlineSMS) ===");
        PersoanaJuridica srlValid = new PersoanaJuridica("TechSRL", "", "0799999999", 20000);
        PersoanaJuridica srlFaraTelefon = new PersoanaJuridica("NoPhoneSRL", "", null, 5000);

        PlataOnlineSMS refSrl = srlValid;
        refSrl.autentificare("company", "pass");

        refSrl.efectuarePlata(1500);

        boolean succesSms = refSrl.trimiteSMS(null);
        System.out.println("Succes trimitere SMS invalid? " + succesSms);

        PlataOnlineSMS refFaraTel = srlFaraTelefon;
        boolean succesSms2 = refFaraTel.trimiteSMS("Mesaj important");
        System.out.println("Succes trimitere SMS fara numar valid? " + succesSms2);

        System.out.println("\nIstoric SMS-uri trimise pentru TechSRL:");
        for (String sms : srlValid.getSmsTrimise()) {
            System.out.println("- " + sms);
        }
    }
}