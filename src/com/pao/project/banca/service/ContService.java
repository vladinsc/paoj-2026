package com.pao.project.banca.service;

import com.pao.project.banca.exceptions.ClientNegasitException;
import com.pao.project.banca.exceptions.ContNegasitException;
import com.pao.project.banca.exceptions.FonduriInsuficienteException;
import com.pao.project.banca.models.*;
import com.pao.project.banca.utils.IbanGenerator;
import com.pao.project.banca.utils.UuidGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContService {
    private static ContService instance;
    private ContService() {}
    public static ContService getInstance() {
        if (instance == null) {
            instance = new ContService();
        }
        return instance;
    }

    private final Map<String, Cont> conturiDupaIban= new HashMap<>();

    public Cont deschideCont(String idClient, String tipCont, Moneda moneda, String PREFIX_BANCA) throws ClientNegasitException {
        ClientService clientService = ClientService.getInstance();
        clientService.cautaDupaId(idClient);

        String iban = IbanGenerator.genereazaIban(PREFIX_BANCA);

        Cont cont;
        if ("ECONOMII".equalsIgnoreCase(tipCont)) {
            cont = new ContEconomii(iban, idClient, moneda);
        }
        else {
            cont = new ContCurent(iban, idClient, moneda);
        }
        conturiDupaIban.put(iban, cont);
        clientService.asociazaCont(idClient, iban);
        System.out.println("Cont deschis: " + cont.getTipCont() + " | IBAN: " + iban);
        return cont;
    }
    public Cont getCont(String iban) throws ContNegasitException {
        Cont cont = conturiDupaIban.get(iban);
        if (cont == null) {
            throw new ContNegasitException(iban);
        }
        return cont;
    }
    public void stergeCont(String iban) throws ContNegasitException {
        getCont(iban);
        conturiDupaIban.remove(iban);
        System.out.println("Cont inchis: " + iban);
    }
    public List<Cont> listeazaToate() {
        return new ArrayList<>(conturiDupaIban.values());
    }
    public List<Cont> listeazaConturiClient(String idClient) {
        return conturiDupaIban.values().stream()
                .filter(c -> c.getIdClient().equals(idClient))
                .collect(Collectors.toList());
    }

    /*
    * Operatii pe bani
    */

    public void depune(String iban, double suma) throws ContNegasitException {
        if (suma <= 0) throw new IllegalArgumentException("Suma de depus trebuie sa fie pozitiva");
        Cont cont = getCont(iban);
        if (!cont.isActiv()) throw new IllegalStateException("Contul "+iban+" nu este activ.");

        cont.setSold(cont.getSold() + suma);

        Tranzactie t = new Tranzactie(
                UuidGenerator.generateTranzactieID(),
                null,
                iban,
                suma,
                TipTranzactie.DEPUNERE,
                TipTranzactie.DEPUNERE.getDescriere()
        );
        cont.adaugaTranzactie(t);

        System.out.printf("Depunere %.2f %s in contul %s. Sold nou %.2f %s%n.", suma, cont.getMoneda(), iban, cont.getSold(), cont.getTipCont());
    }
    public void retragere(String iban, double suma) throws ContNegasitException, FonduriInsuficienteException {
        if (suma <=0 ) throw new IllegalArgumentException("Suma de retragere trebuie sa fie pozitiva");
        Cont cont = getCont(iban);
        if (!cont.isActiv()) throw new IllegalStateException("Contul "+iban+" nu este activ.");

        double disponibil = cont.getSold();
        if (cont instanceof ContCurent){
            disponibil = ((ContCurent) cont).getSoldDisponibil();
        }

        if(suma > disponibil) throw new FonduriInsuficienteException(disponibil, suma);

        cont.setSold(cont.getSold() - suma);
        Tranzactie t = new Tranzactie(
                UuidGenerator.generateTranzactieID(),
                iban,
                null,
                suma,
                TipTranzactie.RETRAGERE,
                TipTranzactie.RETRAGERE.getDescriere()
        );
        cont.adaugaTranzactie(t);
        System.out.printf("Retragere %.2f %s din contul %s. Sold nou %.2f %s %n", suma, cont.getMoneda(), iban, cont.getSold(), cont.getMoneda());
    }
    public void transfera(String ibanSursa, String ibanDestinatie, double suma) throws ContNegasitException, FonduriInsuficienteException {
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

        sursa.setSold(sursa.getSold() - suma);
        dest.setSold(dest.getSold() + suma);

        String idTrx = UuidGenerator.generateTranzactieID();
        String descriere = "Transfer intre conturi";

        sursa.adaugaTranzactie(new Tranzactie(idTrx, ibanSursa, ibanDestinatie,
                suma, TipTranzactie.TRANSFER_TRIMIS, descriere));
        dest.adaugaTranzactie(new Tranzactie(idTrx, ibanSursa, ibanDestinatie,
                suma, TipTranzactie.TRANSFER_PRIMIT, descriere));

        System.out.printf("Transfer %.2f %s: %s -> %s%n", suma,sursa.getMoneda(), ibanSursa, ibanDestinatie);
    }
    public double getSold(String iban) throws ContNegasitException {
        Cont cont = getCont(iban);
        System.out.printf("Sold cont %s: %.2f %s%n", iban, cont.getSold(), cont.getMoneda());
        return cont.getSold();
    }
    public List<Tranzactie> getExtrasDeCont(String iban) throws ContNegasitException {
        Cont cont = getCont(iban);
        return cont.getTranzactii();
    }

}
