package com.pao.laboratory05.angajati;

/**
 * Exercise 3 — Angajați
 *
 * Cerințele complete se află în:
 *   src/com/pao/laboratory05/Readme.md  →  secțiunea "Exercise 3 — Angajați"
 *
 * Creează fișierele de la zero în acest pachet, apoi rulează Main.java
 * pentru a verifica output-ul așteptat din Readme.
 */
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        System.out.println("Cerințele se află în Readme.md — secțiunea Exercise 3.");
        Scanner scanner = new Scanner(System.in);
        AngajatService service = AngajatService.getInstance();

        while (true) {
            System.out.println("\n===== Gestionare Angajați =====");
            System.out.println("1. Adaugă angajat");
            System.out.println("2. Listare după salariu");
            System.out.println("3. Caută după departament");
            System.out.println("0. Ieșire");
            System.out.print("Opțiune: ");
            String optiuneStr = scanner.nextLine().trim();

            if (optiuneStr.isEmpty()) continue;

            int optiune;
            try {
                optiune = Integer.parseInt(optiuneStr);
            } catch (NumberFormatException e) {
                System.out.println("Optiune invalida. Introdu un numar.");
                continue;
            }

            if (optiune == 0) {
                System.out.println("Iesire din program...");
                break;
            }

            switch (optiune) {
                case 1:
                    System.out.print("Nume angajat: ");
                    String nume = scanner.nextLine();

                    System.out.print("Nume departament: ");
                    String numeDept = scanner.nextLine();

                    System.out.print("Locatie departament: ");
                    String locatieDept = scanner.nextLine();

                    System.out.print("Salariu: ");
                    double salariu;
                    try {
                        salariu = Double.parseDouble(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Salariu invalid! Angajatul nu a fost adaugat.");
                        break;
                    }

                    Departament dept = new Departament(numeDept, locatieDept);
                    Angajat angajat = new Angajat(nume, dept, salariu);
                    service.addAngajat(angajat);
                    break;

                case 2:
                    System.out.println("\n--- Listare dupa salariu (descrescator) ---");
                    service.listBySalary();
                    break;

                case 3:
                    System.out.print("Introdu numele departamentului cautat: ");
                    String deptCautat = scanner.nextLine();
                    System.out.println("\n--- Rezultate cautare ---");
                    service.findByDepartament(deptCautat);
                    break;

                default:
                    System.out.println("Optiune invalida. Alege o optiune din meniu.");
            }
        }

        scanner.close();
    }
}

