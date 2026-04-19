package com.pao.project.banca.models;

public enum TipTranzactie {
    DEPUNERE("Depunere numerar"),
    RETRAGERE("Retragere numerar"),
    TRANSFER_TRIMIS("Transfer trimis"),
    TRANSFER_PRIMIT("Transfer primit"),
    PLATA_CARD("Plata cu cardul"),
    DOBANDA("Dobanda creditata");

    private final String descriere;

    TipTranzactie(String descriere) {
        this.descriere = descriere;
    }

    public String getDescriere() {
        return descriere;
    }

    @Override
    public String toString() {
        return descriere;
    }
}
