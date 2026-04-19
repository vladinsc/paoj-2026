package com.pao.project.banca.exceptions;

public class ContNegasitException extends RuntimeException {
    private final String iban;
    public ContNegasitException(String iban) {
        super("Contul cu IBAN-ul " + iban + "nu a fost  gasit!");
        this.iban = iban;
    }
    public String getIban() { return iban; }
}
