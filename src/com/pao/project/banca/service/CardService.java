package com.pao.project.banca.service;

import com.pao.project.banca.exceptions.ContNegasitException;
import com.pao.project.banca.models.Card;

import java.util.*;
import java.util.stream.Collectors;

import static com.pao.project.banca.utils.CreditCardNumberGenerator.generateCreditCardNumber;

public class CardService {
    private static CardService instance;
    private CardService() {}
    public static CardService getInstance() {
        if (instance == null) {
            instance = new CardService();
        }
        return instance;
    }
    private final Map<String, Card> carduriDupaNumar = new HashMap<>();
    private final Map<String, List<Card>>  carduriDupaIban  = new HashMap<>();

    public Card emiteCard(String iban,String PIN ,Card.TipCard tipCard, String numeDetinator)
            throws ContNegasitException {


        ContService.getInstance().getCont(iban);

        String numarCard = generateCreditCardNumber();
        Card card = new Card(numarCard, iban, PIN ,tipCard, numeDetinator);

        carduriDupaNumar.put(numarCard, card);
        carduriDupaIban.computeIfAbsent(iban, k -> new ArrayList<>()).add(card);

        System.out.println("Card emis: " + card.getNumarMascat()
                + " [" + tipCard + "] pentru contul " + iban);
        return card;
    }

    public Card getCard(String numarCard) {
        Card card = carduriDupaNumar.get(numarCard);
        if (card == null) {
            throw new NoSuchElementException("Cardul cu numarul " + numarCard + " nu a fost gasit.");
        }
        return card;
    }

    public void blocheazaCard(String numarCard) {
        Card card = getCard(numarCard);
        card.setStatus(Card.StatusCard.BLOCAT);
        System.out.println("Card blocat: " + card.getNumarMascat());
    }

    public void deblocheazaCard(String numarCard) {
        Card card = getCard(numarCard);
        if (card.getStatus() == Card.StatusCard.BLOCAT) {
            card.setStatus(Card.StatusCard.ACTIV);
            System.out.println("Card deblocat: " + card.getNumarMascat());
        } else {
            System.out.println("Cardul nu era blocat.");
        }
    }

    public List<Card> getCarduriPentruCont(String iban) {
        return carduriDupaIban.getOrDefault(iban, Collections.emptyList());
    }

    public List<Card> getCarduriActive() {
        return carduriDupaNumar.values().stream()
                .filter(Card::isActiv)
                .collect(Collectors.toList());
    }

    public int getNrCarduri() {
        return carduriDupaNumar.size();
    }
}
