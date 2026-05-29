package com.pao.project.banca.service;

import com.pao.project.banca.exceptions.ClientNegasitException;
import com.pao.project.banca.exceptions.ContNegasitException;
import com.pao.project.banca.exceptions.FonduriInsuficienteException;
import com.pao.project.banca.models.*;
import com.pao.project.banca.repository.ContRepository;
import com.pao.project.banca.repository.TranzactieRepository;
import com.pao.project.banca.utils.IbanGenerator;
import com.pao.project.banca.utils.UuidGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContService {
    private static ContService instance;
    private final ContRepository contRepository = new ContRepository();
    private final TranzactieRepository tranzactieRepository = new TranzactieRepository();

    private ContService() {}
    public static ContService getInstance() {
        if (instance == null) {
            instance = new ContService();
        }
        return instance;
    }

    public Cont deschideCont(String idClient, String tipCont, Moneda moneda, String PREFIX_BANCA) throws ClientNegasitException {
        AuditService.getInstance().logAction("deschide_cont");
        ClientService clientService = ClientService.getInstance();
        clientService.cautaDupaId(idClient);

        String iban = IbanGenerator.genereazaIban(PREFIX_BANCA);
        String numeBanca = PREFIX_BANCA; // Using the prefix as the bank name for now

        Cont cont;
        if ("ECONOMII".equalsIgnoreCase(tipCont)) {
            cont = new ContEconomii(iban, idClient, moneda, numeBanca);
        }
        else {
            cont = new ContCurent(iban, idClient, moneda, numeBanca);
        }
        contRepository.save(cont);
        System.out.println("Cont deschis: " + cont.getTipCont() + " | IBAN: " + iban);
        return cont;
    }
    public Cont getCont(String iban) throws ContNegasitException {
        return contRepository.findById(iban).orElseThrow(() -> new ContNegasitException(iban));
    }
    public void stergeCont(String iban) throws ContNegasitException {
        getCont(iban);
        contRepository.delete(iban);
        System.out.println("Cont inchis: " + iban);
    }
    public List<Cont> listeazaToate() {
        return contRepository.findAll();
    }
    public List<Cont> listeazaConturiClient(String idClient) {
        return contRepository.findAll().stream()
                .filter(c -> c.getIdClient().equals(idClient))
                .collect(Collectors.toList());
    }

    /*
    * Operatii pe bani
    */

    public void depune(String iban, double suma) throws ContNegasitException {
        AuditService.getInstance().logAction("depunere_numerar");
        if (suma <= 0) throw new IllegalArgumentException("Suma de depus trebuie sa fie pozitiva");
        Cont cont = getCont(iban);
        if (!cont.isActiv()) throw new IllegalStateException("Contul "+iban+" nu este activ.");

        cont.setSold(cont.getSold() + suma);
        contRepository.update(cont);

        Tranzactie t = new Tranzactie(
                UuidGenerator.generateTranzactieID(),
                null,
                iban,
                suma,
                TipTranzactie.DEPUNERE,
                TipTranzactie.DEPUNERE.getDescriere()
        );
        tranzactieRepository.save(t);

        System.out.printf("Depunere %.2f %s in contul %s. Sold nou %.2f %s%n.", suma, cont.getMoneda(), iban, cont.getSold(), cont.getTipCont());
    }
    public void retrage(String iban, double suma) throws ContNegasitException, FonduriInsuficienteException {
        AuditService.getInstance().logAction("retragere_numerar");
        if (suma <=0 ) throw new IllegalArgumentException("Suma de retragere trebuie sa fie pozitiva");
        Cont cont = getCont(iban);
        if (!cont.isActiv()) throw new IllegalStateException("Contul "+iban+" nu este activ.");

        double disponibil = cont.getSold();
        if (cont instanceof ContCurent){
            disponibil = ((ContCurent) cont).getSoldDisponibil();
        }

        if(suma > disponibil) throw new FonduriInsuficienteException(disponibil, suma);

        cont.setSold(cont.getSold() - suma);
        contRepository.update(cont);

        Tranzactie t = new Tranzactie(
                UuidGenerator.generateTranzactieID(),
                iban,
                null,
                suma,
                TipTranzactie.RETRAGERE,
                TipTranzactie.RETRAGERE.getDescriere()
        );
        tranzactieRepository.save(t);
        System.out.printf("Retragere %.2f %s din contul %s. Sold nou %.2f %s %n", suma, cont.getMoneda(), iban, cont.getSold(), cont.getMoneda());
    }
    public void transfera(String ibanSursa, String ibanDestinatie, double suma) throws ContNegasitException, FonduriInsuficienteException {
        AuditService.getInstance().logAction("transfer_bancar");
        if (ibanSursa.equals(ibanDestinatie)) {
            throw new IllegalArgumentException("Sursa si destinatia transferului nu pot fi identice.");
        }
        if (suma <= 0) throw new IllegalArgumentException("Suma transferului trebuie sa fie pozitiva.");

        Cont sursa = getCont(ibanSursa);
        Cont dest = getCont(ibanDestinatie);

        if(!sursa.isActiv()) throw new IllegalStateException("Contul sursa nu este activ.");
        if(!dest.isActiv()) throw new IllegalStateException("Contul destinatie nu este activ.");
        double disponibil = sursa.getSold();
        if (sursa instanceof ContCurent){
            disponibil = ((ContCurent) sursa).getSoldDisponibil();
        }

        if(suma > disponibil) throw new FonduriInsuficienteException(disponibil, suma);

        // JDBC Transaction
        Connection conn = com.pao.project.banca.utils.DatabaseConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
            
            sursa.setSold(sursa.getSold() - suma);
            dest.setSold(dest.getSold() + suma);
            
            contRepository.update(sursa);
            contRepository.update(dest);

            String descriere = "Transfer intre conturi";

            tranzactieRepository.save(new Tranzactie(UuidGenerator.generateTranzactieID(), ibanSursa, ibanDestinatie,
                    suma, TipTranzactie.TRANSFER_TRIMIS, descriere));
            tranzactieRepository.save(new Tranzactie(UuidGenerator.generateTranzactieID(), ibanSursa, ibanDestinatie,
                    suma, TipTranzactie.TRANSFER_PRIMIT, descriere));

            conn.commit();
            System.out.printf("Transfer %.2f %s: %s -> %s [TRANZACTIE REUSITA]%n", suma, sursa.getMoneda(), ibanSursa, ibanDestinatie);
        } catch (SQLException e) {
            try {
                conn.rollback();
                System.err.println("Tranzactie esuata. Rollback executat: " + e.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public double getSold(String iban) throws ContNegasitException {
        AuditService.getInstance().logAction("interogare_sold");
        Cont cont = getCont(iban);
        System.out.printf("Sold cont %s: %.2f %s%n", iban, cont.getSold(), cont.getMoneda());
        return cont.getSold();
    }
    public List<Tranzactie> getExtrasDeCont(String iban) throws ContNegasitException {
        AuditService.getInstance().logAction("extras_cont");
        return tranzactieRepository.findByIban(iban);
    }
}
