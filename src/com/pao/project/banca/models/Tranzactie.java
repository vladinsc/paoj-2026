package com.pao.project.banca.models;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
public final class Tranzactie {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String id;
    private final String ibanSursa;
    private final String ibanDestinatie;
    private final double suma;
    private final TipTranzactie tipTranzactie;
    private final LocalDateTime dataOra;
    private final String descriere;


    public Tranzactie(String id, String ibanSursa, String ibanDestinatie, double suma,  TipTranzactie tipTranzactie, String descriere) {
        this.id = id;
        this.ibanSursa = ibanSursa;
        this.ibanDestinatie = ibanDestinatie;
        this.suma = suma;
        this.tipTranzactie = tipTranzactie;
        this.descriere = descriere;
        this.dataOra = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getDescriere() {
        return descriere;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public TipTranzactie getTipTranzactie() {
        return tipTranzactie;
    }

    public double getSuma() {
        return suma;
    }

    public String getIbanDestinatie() {
        return ibanDestinatie;
    }

    public String getIbanSursa() {
        return ibanSursa;
    }

    public String toString() {
        return String.format("[%s] %s | %.2f RON | %s -> %s | \"%s\"",
                dataOra.format(FORMATTER),
                tipTranzactie.getDescriere(),
                suma,
                ibanSursa      != null ? ibanSursa      : "EXTERN",
                ibanDestinatie != null ? ibanDestinatie : "EXTERN",
                descriere);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tranzactie)) return false;
        Tranzactie t = (Tranzactie) o;
        return Objects.equals(id, t.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
