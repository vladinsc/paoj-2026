package com.pao.project.banca.models;
import java.time.LocalDate;
import java.util.Objects;

public class Card {
    public enum StatusCard {ACTIV, BLOCAT, EXPIRAT}
    public enum TipCard {DEBIT, CREDIT};

    private final String numarCard;
    private final  String iban;
    private final TipCard tipCard;
    private String PIN;
    private StatusCard statusCard;
    private LocalDate dataExpirare;
    private String numeDetinator;

    public Card(String numarCard, String iban,String PIN, TipCard tipCard, String numeDetinator) {
        this.numarCard = numarCard;
        this.iban = iban;
        this.tipCard = tipCard;
        this.numeDetinator = numeDetinator;
        this.dataExpirare = LocalDate.now().plusMonths(48);
        this.PIN = PIN;
        this.statusCard = StatusCard.ACTIV;
    }

    public String getNumarCard()       { return numarCard; }
    public String getIban()            { return iban; }
    public TipCard getTipCard()        { return tipCard; }
    public StatusCard getStatus()      { return statusCard; }
    public LocalDate getDataExpirare() { return dataExpirare; }
    public String getNumeDetinator()   { return numeDetinator; }
    public String getPIN()             { return PIN; }
    public void setStatus(StatusCard status) { this.statusCard = status; }
    public void setNumeDetinator(String nume)   { this.numeDetinator = nume; }
    public void setPIN(String PIN) { this.PIN = PIN; }

    public boolean isActiv() {
        return statusCard == StatusCard.ACTIV && LocalDate.now().isBefore(dataExpirare);
    }

    /** Returneaza numarul mascat: ****-****-****-1234 */
    public String getNumarMascat() {
        if (numarCard == null || numarCard.length() < 4) return "****";
        return "****-****-****-" + numarCard.substring(numarCard.length() - 4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card c = (Card) o;
        return Objects.equals(numarCard, c.numarCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numarCard);
    }

    @Override
    public String toString() {
        return String.format("[CARD %s] %s | Cont: %s | Detinator: %s | Expira: %s | Status: %s",
                tipCard, getNumarMascat(), iban, numeDetinator, dataExpirare, statusCard);
    }
}
