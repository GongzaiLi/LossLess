package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import com.seng302.wasteless.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * Returns all cards that belong to the given CardSections.
     * @param section The section the card belongs to.
     * @return A (possibly empty) list of all cards that belong to the given section
     */
    public List<Card> findBySection(CardSections section) {
        return cardRepository.findBySection(section);
    }


    /**
     * Returns all cards that belong to the current user.
     * @param userId The id of the current user.
     * @return A (possibly empty) list of all cards that belong to the current user.
     */
    public List<Card> getAllUserCards(Integer userId) { return cardRepository.findAllByCreator_Id(userId); }
}

