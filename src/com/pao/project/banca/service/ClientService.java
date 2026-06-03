package com.pao.project.banca.service;

import com.pao.project.banca.exceptions.ClientNegasitException;
import com.pao.project.banca.models.Client;
import com.pao.project.banca.repository.ClientRepository;
import com.pao.project.banca.utils.UuidGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class ClientService {
    private static ClientService instance;
    private final ClientRepository clientRepository = new ClientRepository();

    private ClientService(){}
    public static ClientService getInstance(){
        if(instance==null){
            instance=new ClientService();
        }
        return instance;
    }

    public Client inregistreazaClient(String nume, String prenume, String cnp, String email, String telefon, com.pao.project.banca.models.Adresa adresa){
        AuditService.getInstance().logAction("inregistrare_client");
        Optional<Client> existent = clientRepository.findByCnp(cnp);
        if (existent.isPresent()){
            throw new IllegalArgumentException("Exista deja un client cu CNP-ul: "+ cnp);
        }
        String id  = UuidGenerator.generateIDClient();
        Client client = new Client(id, nume, prenume, cnp, email, telefon, adresa);
        clientRepository.save(client);
        System.out.println("Client Inregistrat: " + client.getNumeComplet());
        return client;
    }

    public Client cautaDupaCnp(String cnp) throws ClientNegasitException {
        AuditService.getInstance().logAction("cautare_client_cnp");
        return clientRepository.findByCnp(cnp).orElseThrow(() -> new ClientNegasitException(cnp));
    }
    public Client cautaDupaId(String id) throws ClientNegasitException {
        return clientRepository.findById(id).orElseThrow(() -> new ClientNegasitException(id));
    }
    public List<Client> cautaDupaNume(String fragment) {
        AuditService.getInstance().logAction("cautare_client_nume");
        String lower = fragment.toLowerCase();
        return clientRepository.findAll().stream()
                .filter(c -> c.getNume().toLowerCase().contains(lower)
                        || c.getPrenume().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
    public List<Client> listeazaToti() {
        return clientRepository.findAll();
    }

    public int getNrClienti() {
        return clientRepository.findAll().size();
    }

    public void asociazaCont(String idClient, String iban) throws ClientNegasitException {

    }

    public List<String> getRapoarteSoldTotal() {
        return clientRepository.getClientiCuSoldTotal();
    }

    public List<String> getRapoarteTranzactiiRecente(String clientId) {
        return clientRepository.getTranzactiiRecenteClient(clientId);
    }

    public List<String> getRapoarteTopClientiCarduri() {
        return clientRepository.getTopClientiDupaNrCarduri();
    }
}

