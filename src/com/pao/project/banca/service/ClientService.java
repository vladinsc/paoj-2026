package com.pao.project.banca.service;

import com.pao.project.banca.exceptions.ClientNegasitException;
import com.pao.project.banca.models.Client;
import com.pao.project.banca.utils.UuidGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class ClientService {
    private static ClientService instance;
    private ClientService(){}
    public static ClientService getInstance(){
        if(instance==null){
            instance=new ClientService();
        }
        return instance;
    }

    private final Map<String, Client> clientiDupaCnp = new HashMap<>();
    private final Map<String, Client> clientiDupaId  = new HashMap<>();
    private final TreeSet<Client>     clientiSortati  = new TreeSet<>();

    public Client inregistreazaClient(String nume, String prenume, String cnp, String email, String telefon, com.pao.project.banca.models.Adresa adresa){
        if (clientiDupaCnp.containsKey(nume)){
            throw new IllegalArgumentException("Exista deja un client cu CNP-ul: "+ cnp);
        }
        String id  = UuidGenerator.generateIDClient();
        Client client = new Client(id, nume, prenume, cnp, email, telefon, adresa);
        clientiDupaCnp.put(cnp, client);
        clientiDupaId.put(id, client);
        clientiSortati.add(client);
        System.out.println("Client Inregistrat: " + client.getNumeComplet());
        return client;
    }

    public void stergeClientDupaCNP(String cnp){
        Client client = cautaDupaCnp(cnp);
    }
    public Client cautaDupaCnp(String cnp) throws ClientNegasitException {
        Client client = clientiDupaCnp.get(cnp);
        if (client == null) {
            throw new ClientNegasitException(cnp);
        }
        return client;
    }
    public Client cautaDupaId(String id) throws ClientNegasitException {
        Client client = clientiDupaId.get(id);
        if (client == null) {
            throw new ClientNegasitException(id);
        }
        return client;
    }
    public List<Client> cautaDupaNume(String fragment) {
        String lower = fragment.toLowerCase();
        return clientiSortati.stream()
                .filter(c -> c.getNume().toLowerCase().contains(lower)
                        || c.getPrenume().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
    public List<Client> listeazaToti() {
        return new ArrayList<>(clientiSortati);
    }

    public int getNrClienti() {
        return clientiDupaCnp.size();
    }

    public void asociazaCont(String idClient, String iban) throws ClientNegasitException {
        Client client = cautaDupaId(idClient);
        client.adaugaCont(iban);
    }

}
