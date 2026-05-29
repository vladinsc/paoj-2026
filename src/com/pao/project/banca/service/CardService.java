package com.pao.project.banca.service;

import com.pao.project.banca.exceptions.ContNegasitException;
import com.pao.project.banca.models.Card;
import com.pao.project.banca.repository.CardRepository;

import java.util.*;
import java.util.stream.Collectors;

import static com.pao.project.banca.utils.CreditCardNumberGenerator.generateCreditCardNumber;

public class CardService {
    private static CardService instance;
    private final CardRepository cardRepository = new CardRepository();

    private CardService() {}
    public static CardService getInstance() {
        if (instance == null) {
            instance = new CardService();
        }
        return instance;
    }

    public Card emiteCard(String iban,String PIN ,Card.TipCard tipCard, String numeDetinator)
            throws ContNegasitException {
        AuditService.getInstance().logAction("emite_card");

        ContService.getInstance().getCont(iban);

        String numarCard = generateCreditCardNumber();
        Card card = new Card(numarCard, iban, PIN ,tipCard, numeDetinator);

        cardRepository.save(card);

        System.out.println("Card emis: " + card.getNumarMascat()
                + " [" + tipCard + "] pentru contul " + iban);
        return card;
    }

    public Card getCard(String numarCard) {
        return cardRepository.findById(numarCard).orElseThrow(() -> 
            new NoSuchElementException("Cardul cu numarul " + numarCard + " nu a fost gasit."));
    }

    public void blocheazaCard(String numarCard) {
        AuditService.getInstance().logAction("blocare_card");
        Card card = getCard(numarCard);
        card.setStatus(Card.StatusCard.BLOCAT);
        cardRepository.update(card);
        System.out.println("Card blocat: " + card.getNumarMascat());
    }

    public void deblocheazaCard(String numarCard) {
        AuditService.getInstance().logAction("deblocare_card");
        Card card = getCard(numarCard);
        if (card.getStatus() == Card.StatusCard.BLOCAT) {
            card.setStatus(Card.StatusCard.ACTIV);
            cardRepository.update(card);
            System.out.println("Card deblocat: " + card.getNumarMascat());
        } else {
            System.out.println("Cardul nu era blocat.");
        }
    }

    public List<Card> getCarduriPentruCont(String iban) {
        return cardRepository.findAll().stream()
                .filter(c -> c.getIban().equals(iban))
                .collect(Collectors.toList());
    }

    public List<Card> getCarduriActive() {
        return cardRepository.findAll().stream()
                .filter(Card::isActiv)
                .collect(Collectors.toList());
    }

    public int getNrCarduri() {
        return cardRepository.findAll().size();
    }
}
