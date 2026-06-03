package com.pao.project.banca;

import com.pao.project.banca.models.Client;
import com.pao.project.banca.models.Cont;
import com.pao.project.banca.models.Adresa;
import com.pao.project.banca.models.Moneda;
import com.pao.project.banca.service.ClientService;
import com.pao.project.banca.service.ContService;

import java.util.List;
import java.util.Scanner;

public class BancaCLI {
    private final ClientService clientService = ClientService.getInstance();
    private final ContService contService = ContService.getInstance();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    listClienti();
                    break;
                case "2":
                    addClient();
                    break;
                case "3":
                    listConturi();
                    break;
                case "4":
                    openCont();
                    break;
                case "5":
                    depunere();
                    break;
                case "6":
                    transfer();
                    break;
                case "0":
                    running = false;
                    System.out.println("La revedere!");
                    break;
                default:
                    System.out.println("Optiune invalida!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Sistem Management Bancar ===");
        System.out.println("1. Listeaza Clienti");
        System.out.println("2. Adauga Client");
        System.out.println("3. Listeaza Conturi");
        System.out.println("4. Deschide Cont");
        System.out.println("5. Depunere");
        System.out.println("6. Transfer");
        System.out.println("0. Iesire");
        System.out.print("Alegeti o optiune: ");
    }

    private void listClienti() {
        List<Client> clienti = clientService.listeazaToti();
        System.out.println("\n--- Lista Clienti ---");
        for (Client c : clienti) {
            System.out.printf("ID: %s | Nume: %s %s | CNP: %s | Email: %s\n",
                    c.getId(), c.getNume(), c.getPrenume(), c.getCnp(), c.getEmail());
        }
    }

    private void addClient() {
        System.out.println("\n--- Adauga Client Nou ---");
        System.out.print("Nume: ");
        String nume = scanner.nextLine();
        System.out.print("Prenume: ");
        String prenume = scanner.nextLine();
        System.out.print("CNP: ");
        String cnp = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefon: ");
        String telefon = scanner.nextLine();

        try {
            Adresa adresa = new Adresa("Strada Generica", "1", "Oras", "Judet", "000000", "Romania");
            clientService.inregistreazaClient(nume, prenume, cnp, email, telefon, adresa);
            System.out.println("Client adaugat cu succes!");
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void listConturi() {
        List<Cont> conturi = contService.listeazaToate();
        System.out.println("\n--- Lista Conturi ---");
        for (Cont cont : conturi) {
            System.out.printf("IBAN: %s | Client ID: %s | Tip: %s | Moneda: %s | Sold: %.2f | Banca: %s\n",
                    cont.getIban(), cont.getIdClient(), cont.getTipCont(), cont.getMoneda(), cont.getSold(), cont.getNumeBanca());
        }
    }

    private void openCont() {
        System.out.println("\n--- Deschide Cont ---");
        System.out.print("ID Client: ");
        String clientId = scanner.nextLine();
        System.out.print("Tip Cont (CURENT/ECONOMII): ");
        String tip = scanner.nextLine().toUpperCase();
        System.out.print("Prefix Banca (ex: BCR, BT, ING): ");
        String banca = scanner.nextLine();

        try {
            contService.deschideCont(clientId, tip, Moneda.RON, banca);
            System.out.println("Cont deschis cu succes!");
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void depunere() {
        System.out.println("\n--- Depunere ---");
        System.out.print("IBAN: ");
        String iban = scanner.nextLine();
        System.out.print("Suma: ");
        try {
            double suma = Double.parseDouble(scanner.nextLine());
            contService.depune(iban, suma);
            System.out.println("Depunere reusita!");
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void transfer() {
        System.out.println("\n--- Transfer ---");
        System.out.print("IBAN Sursa: ");
        String ibanSursa = scanner.nextLine();
        System.out.print("IBAN Destinatie: ");
        String ibanDest = scanner.nextLine();
        System.out.print("Suma: ");
        try {
            double suma = Double.parseDouble(scanner.nextLine());
            contService.transfera(ibanSursa, ibanDest, suma);
            System.out.println("Transfer realizat cu succes!");
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }
}
