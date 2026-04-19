package com.pao.project.banca.exceptions;

public class ClientNegasitException extends RuntimeException {
    private final String id;
    public ClientNegasitException(String identificativ) {
        super("Clientul cu identificatorul " + identificativ + " nu a fost gasit:{");
        id = identificativ;
    }
    public String getIdentificator() { return id; }
}
