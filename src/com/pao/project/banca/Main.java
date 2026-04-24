package com.pao.project.banca;

import com.pao.project.banca.exceptions.ClientNegasitException;
import com.pao.project.banca.exceptions.ContNegasitException;
import com.pao.project.banca.exceptions.FonduriInsuficienteException;
import com.pao.project.banca.models.*;
import com.pao.project.banca.service.CardService;
import com.pao.project.banca.service.ClientService;
import com.pao.project.banca.service.ContService;

import java.util.List;

public class Main {

    private static void sectiune(String titlu) {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("  " + titlu);
        System.out.println("-".repeat(60));
    }

    public static void main(String[] args) {

        ClientService clientService = ClientService.getInstance();
        ContService   contService   = ContService.getInstance();
        CardService   cardService   = CardService.getInstance();

        sectiune("ACTIUNEA 1 — Inregistrare clienti");

        Adresa adresa1 = new Adresa("Victoriei",  "10", "Bucuresti",   "Ilfov", "010011", "Romania");
        Adresa adresa2 = new Adresa("Florilor",    "5", "Cluj-Napoca", "Cluj",  "400001", "Romania");
        Adresa adresa3 = new Adresa("Libertatii", "22", "Timisoara",   "Timis", "300001", "Romania");

        Client ana, mihai, elena;
        try {
            ana   = clientService.inregistreazaClient("Popescu",    "Ana",   "1900101123456", "ana@mail.ro",   "0721000001", adresa1);
            mihai = clientService.inregistreazaClient("Ionescu",    "Mihai", "1850315654321", "mihai@mail.ro", "0722000002", adresa2);
            elena = clientService.inregistreazaClient("Dumitrescu", "Elena", "2950622987654", "elena@mail.ro", "0733000003", adresa3);
            System.out.println("\n  Total clienti inregistrati: " + clientService.getNrClienti());
        } catch (IllegalArgumentException e) {
            System.err.println("  EROARE: " + e.getMessage());
            return;
        }

        sectiune("ACTIUNEA 2 — Deschidere conturi bancare");

        String ibanCurentAna = null, ibanEconomiiAna = null, ibanCurentMihai = null;
        try {
            Cont c1 = contService.deschideCont(ana.getId(),   "CURENT", Moneda.RON, "BCR");
            Cont c2 = contService.deschideCont(ana.getId(),   "ECONOMII",  Moneda.RON, "BCR");
            Cont c3 = contService.deschideCont(mihai.getId(), "CURENT",  Moneda.RON, "BCR");
            contService.deschideCont(elena.getId(), "ECONOMII",  Moneda.RON, "BCR");

            ibanCurentAna   = c1.getIban();
            ibanEconomiiAna = c2.getIban();
            ibanCurentMihai = c3.getIban();

            System.out.println("\n  Conturi Ana: " + ana.getIbanConturi());
        } catch (ClientNegasitException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }


        sectiune("ACTIUNEA 3 — Emitere carduri");

        Card cardAna = null;
        try {
            cardAna = cardService.emiteCard(ibanCurentAna, "4528",   Card.TipCard.DEBIT,  "POPESCU ANA");
            cardService.emiteCard(ibanCurentMihai,"9330" ,Card.TipCard.DEBIT,  "IONESCU MIHAI");
            cardService.emiteCard(ibanEconomiiAna, "8321",Card.TipCard.CREDIT, "POPESCU ANA");

            System.out.println("  Total carduri emise: " + cardService.getNrCarduri());
            System.out.println("  Carduri active:      " + cardService.getCarduriActive().size());
        } catch (ContNegasitException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }


        sectiune("ACTIUNEA 4 — Depuneri numerar");

        try {
            contService.depune(ibanCurentAna,   5000.00);
            contService.depune(ibanEconomiiAna, 2000.00);
            contService.depune(ibanCurentMihai, 3000.00);
        } catch (ContNegasitException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }

        sectiune("ACTIUNEA 5 — Retrageri numerar");

        try {
            contService.retrage(ibanCurentAna, 1200.00);
        } catch (ContNegasitException | FonduriInsuficienteException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }

        try {
            contService.retrage(ibanCurentAna, 99999.00);
        } catch (FonduriInsuficienteException e) {
            System.err.println("    FonduriInsuficienteException: " + e.getMessage());
        } catch (ContNegasitException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }


        sectiune("ACTIUNEA 6 — Transfer intre conturi");

        try {
            contService.transfera(ibanCurentAna, ibanCurentMihai, 750.00);
            System.out.println("  Sold Ana dupa transfer:   " + contService.getCont(ibanCurentAna).getSold()   + " RON");
            System.out.println("  Sold Mihai dupa transfer: " + contService.getCont(ibanCurentMihai).getSold() + " RON");
        } catch (ContNegasitException | FonduriInsuficienteException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }


        sectiune("ACTIUNEA 7 — Interogare solduri");

        try {
            contService.getSold(ibanCurentAna);
            contService.getSold(ibanEconomiiAna);
            contService.getSold(ibanCurentMihai);
        } catch (ContNegasitException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }


        try {
            contService.getSold("RO00FAKE0000000000000000");
        } catch (ContNegasitException e) {
            System.err.println("ContNegasitException: " + e.getMessage());
        }

        sectiune("ACTIUNEA 8 — Extras de cont (Ana - cont curent)");

        try {
            List<Tranzactie> istoricAna = contService.getExtrasDeCont(ibanCurentAna);
            System.out.println("  Numar tranzactii: " + istoricAna.size());
            istoricAna.forEach(t -> System.out.println("  " + t));
        } catch (ContNegasitException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }


        sectiune("ACTIUNEA 9 — Blocare / deblocare card");

        if (cardAna != null) {
            System.out.println("  Status inainte:        " + cardAna.getStatus());
            cardService.blocheazaCard(cardAna.getNumarCard());
            System.out.println("  Status dupa blocare:   " + cardAna.getStatus());
            System.out.println("  Card activ?            " + cardAna.isActiv());
            cardService.deblocheazaCard(cardAna.getNumarCard());
            System.out.println("  Status dupa deblocare: " + cardAna.getStatus());
        }


        sectiune("ACTIUNEA 10 — Cautare clienti");

        try {
            Client gasit = clientService.cautaDupaCnp("1900101123456");
            System.out.println("  Gasit dupa CNP: " + gasit);
        } catch (ClientNegasitException e) {
            System.err.println("  EROARE: " + e.getMessage());
        }

        System.out.println("\n  Cautare dupa fragment 'escu':");
        List<Client> rezultate = clientService.cautaDupaNume("escu");
        rezultate.forEach(c -> System.out.println("    -> " + c.getNumeComplet() + " (" + c.getId() + ")"));

        System.out.println("\n  [Scenariu eroare] Cautare CNP inexistent:");
        try {
            clientService.cautaDupaCnp("9999999999999");
        } catch (ClientNegasitException e) {
            System.err.println("ClientNegasitException: " + e.getMessage());
        }


    }
}
