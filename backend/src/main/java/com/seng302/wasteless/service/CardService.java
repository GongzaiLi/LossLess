package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CardService applies card logic over the card JPA repository.
 */
@Service
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) { this.cardRepository = cardRepository; }

    /**
     * Creates a Card by saving the card object and persisting it in the database
     * @param card The Card object to be created.
     * @return The created Card object.
     */
    public Card createCard(Card card) {return cardRepository.save(card); }
}

