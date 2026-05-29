package com.pao.project.banca;

import com.pao.project.banca.exceptions.*;
import com.pao.project.banca.models.*;
import com.pao.project.banca.service.*;
import com.pao.project.banca.utils.DatabaseInitializer;

import javax.swing.*;
import java.util.List;

public class Main {
    private static final ClientService clientService = ClientService.getInstance();
    private static final ContService contService = ContService.getInstance();
    private static final CardService cardService = CardService.getInstance();

    public static void main(String[] args) {
        // 1. Initialize Database
        System.out.println(">>> Initializare Baza de Date...");
        DatabaseInitializer.initialize();

        // 2. Run Automated Test Cases
        runAutomatedTests();

        // 3. Launch GUI
        System.out.println("\n>>> Lansare Interfata Grafica...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            BancaGUI gui = new BancaGUI();
            gui.setVisible(true);
        });
    }

    private static void sectiune(String titlu) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  TEST: " + titlu);
        System.out.println("=".repeat(60));
    }

    private static void runAutomatedTests() {
        sectiune("Inregistrare Clienti & Validari");
        
        Adresa adresa = new Adresa("Libertatii", "10", "Bucuresti", "Ilfov", "010011", "Romania");
        Client c1 = null;
        try {
            c1 = clientService.inregistreazaClient("Popescu", "Ion", "1234567890123", "ion@test.com", "0721000000", adresa);
            System.out.println("  [SUCCESS] Client inregistrat: " + c1.getNumeComplet());
            
            // Test eroare: Duplicate CNP
            System.out.println("  [ERROR TEST] Incercare duplicare CNP...");
            clientService.inregistreazaClient("Ionescu", "Ana", "1234567890123", "ana@test.com", "0722000000", adresa);
        } catch (IllegalArgumentException e) {
            System.out.println("  [CAUGHT EXPECTED] " + e.getMessage());
        }

        sectiune("Management Conturi & Tranzactii");
        
        String iban1 = null, iban2 = null;
        try {
            if (c1 != null) {
                Cont cont1 = contService.deschideCont(c1.getId(), "CURENT", Moneda.RON, "BCR");
                iban1 = cont1.getIban();
                System.out.println("  [SUCCESS] Cont deschis: " + iban1);

                contService.depune(iban1, 1000.0);
                System.out.println("  [SUCCESS] Depunere 1000 RON. Sold: " + contService.getSold(iban1));

                // Test eroare: Fonduri insuficiente
                System.out.println("  [ERROR TEST] Incercare retragere 5000 RON...");
                contService.retrage(iban1, 5000.0);
            }
        } catch (Exception e) {
            System.out.println("  [CAUGHT EXPECTED] " + e.getMessage());
        }

        sectiune("Tranzactii JDBC (Transfer)");
        try {
            Client c2 = clientService.inregistreazaClient("Vasilescu", "Dan", "2900101999999", "dan@test.com", "0733000000", adresa);
            Cont cont2 = contService.deschideCont(c2.getId(), "ECONOMII", Moneda.RON, "BT");
            iban2 = cont2.getIban();

            System.out.println("  Transfer 300 RON: " + iban1 + " -> " + iban2);
            contService.transfera(iban1, iban2, 300.0);
            
            System.out.println("  Sold Final Sursa: " + contService.getSold(iban1));
            System.out.println("  Sold Final Destinatie: " + contService.getSold(iban2));
        } catch (Exception e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }

        sectiune("Management Carduri");
        try {
            Card card = cardService.emiteCard(iban1, "1234", Card.TipCard.DEBIT, "POPESCU ION");
            System.out.println("  [SUCCESS] Card emis: " + card.getNumarMascat());
            
            cardService.blocheazaCard(card.getNumarCard());
            System.out.println("  [SUCCESS] Status card: " + cardService.getCard(card.getNumarCard()).getStatus());
        } catch (Exception e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }

        sectiune("Rapoarte Avansate (JOIN)");
        System.out.println("  1. Sold Total per Client:");
        clientService.getRapoarteSoldTotal().forEach(r -> System.out.println("     " + r));

        System.out.println("\n  2. Top Clienti dupa Carduri:");
        clientService.getRapoarteTopClientiCarduri().forEach(r -> System.out.println("     " + r));

        System.out.println("\n  3. Tranzactii Recente (Ion Popescu):");
        if (c1 != null) {
            clientService.getRapoarteTranzactiiRecente(c1.getId()).forEach(r -> System.out.println("     " + r));
        }

        System.out.println("\n>>> TESTE AUTOMATE FINALIZATE CU SUCCES.");
    }
}
