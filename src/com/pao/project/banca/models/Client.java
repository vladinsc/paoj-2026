package com.pao.project.banca.models;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public class Client extends Persoana{
    private LocalDate dataInregistrare;
    private final List<String> ibanConturi;
    public Client(String id, String nume, String prenume, String cnp, String email, String tel, Adresa adresa){
        super(id, nume, prenume, cnp, email, tel, adresa);
        this.ibanConturi = new ArrayList<>();
        this.dataInregistrare = LocalDate.now();
    }
    @Override
    public String getRol(){
        return "Client";
    }

    public List<String> getIbanConturi() {
        return Collections.unmodifiableList(this.ibanConturi);
    }
    public LocalDate getDataInregistrare() {
        return dataInregistrare;
    }

    public void adaugaCont(String iban){
        if(iban != null && !iban.isBlank() && !ibanConturi.contains(iban)){
            ibanConturi.add(iban);
        }
        else{
            throw  new IllegalArgumentException("Iban ne postoji!");
        }
    }
    public void elimnaCont(String iban){
        ibanConturi.remove(iban);
    }
    public int getNrConturi(){
        return ibanConturi.size();
    }

    @Override
    public String toString() {
        return String.format("[CLIENT] %s %s | CNP: %s | Email: %s | Conturi: %d | Inregistrat: %s",
                prenume, nume, cnp, email, ibanConturi.size(), dataInregistrare);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
