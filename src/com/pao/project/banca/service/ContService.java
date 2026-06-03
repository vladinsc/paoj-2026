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
import java.sql.PreparedStatement;
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
        String sqlUpdateCont = "UPDATE conturi SET sold = ? WHERE iban = ?";
        String sqlInsertTranzactie = "INSERT INTO tranzactii (id, iban_sursa, iban_destinatie, suma, tip_tranzactie, descriere) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = com.pao.project.banca.utils.DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateCont);
                 PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertTranzactie)) {
                
                // 1. Debiteaza sursa
                pstmtUpdate.setDouble(1, sursa.getSold() - suma);
                pstmtUpdate.setString(2, ibanSursa);
                pstmtUpdate.executeUpdate();

                // 2. Crediteaza destinatia
                pstmtUpdate.setDouble(1, dest.getSold() + suma);
                pstmtUpdate.setString(2, ibanDestinatie);
                pstmtUpdate.executeUpdate();

                String descriere = "Transfer intre conturi";

                // 3. Inregistreaza tranzactia sursa
                pstmtInsert.setString(1, UuidGenerator.generateTranzactieID());
                pstmtInsert.setString(2, ibanSursa);
                pstmtInsert.setString(3, ibanDestinatie);
                pstmtInsert.setDouble(4, suma);
                pstmtInsert.setString(5, TipTranzactie.TRANSFER_TRIMIS.name());
                pstmtInsert.setString(6, descriere);
                pstmtInsert.executeUpdate();

                // 4. Inregistreaza tranzactia destinatie
                pstmtInsert.setString(1, UuidGenerator.generateTranzactieID());
                pstmtInsert.setString(2, ibanSursa);
                pstmtInsert.setString(3, ibanDestinatie);
                pstmtInsert.setDouble(4, suma);
                pstmtInsert.setString(5, TipTranzactie.TRANSFER_PRIMIT.name());
                pstmtInsert.setString(6, descriere);
                pstmtInsert.executeUpdate();

                conn.commit();
                System.out.printf("Transfer %.2f %s: %s -> %s [TRANZACTIE REUSITA]%n", suma, sursa.getMoneda(), ibanSursa, ibanDestinatie);
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Tranzactie esuata. Rollback executat: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
