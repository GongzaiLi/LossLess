package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import com.seng302.wasteless.repository.CardRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * CardService applies card logic over the card JPA repository.
 */
@Service
public class CardService {

    private final CardRepository cardRepository;

    private static final Logger logger = LogManager.getLogger(CardService.class.getName());

    @Autowired
    public CardService(CardRepository cardRepository) { this.cardRepository = cardRepository; }

    /**
     * Creates a Card by saving the card object and persisting it in the database
     * @param card The Card object to be created.
     * @return The created Card object.
     */
    public Card createCard(Card card) {return cardRepository.save(card); }


    /**
     * Find card by id
     * @param id        The id of the card to find
     * @return          The found card, if any, or throw ResponseStatusException
     */
    public Card findCardById(Integer id) {
        Card possibleCard = cardRepository.findFirstById(id);
        if (possibleCard == null) {
            logger.warn("Card with id: {} does not exist.", id);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Card with given ID does not exist");
        }
        return possibleCard;
    }

    /**
     * Returns all cards that belong to the given CardSections.
     * @param section The section the card belongs to.
     * @return A (possibly empty) list of all cards that belong to the given section
     */
    public List<Card> findBySection(CardSections section) {
        return cardRepository.findBySection(section);
    }

    /**
     * Returns the first card found with the given ID. Will always be correct card as the id is unique
     * @param cardId The id as an integer of the card being requested
     * @return Will return the card object that with the requested id. returns null if none found.
     */
    public Card findById(Integer cardId) {return cardRepository.findFirstById(cardId);}

    /**
     * Deletes the database entry of the card that is passed to this method
     * @param card The card to be deleted from the database
     */
    public void deleteCard(Card card) {cardRepository.delete(card);}


    /**
     * Returns all cards that belong to the given CardSections.
     * @param section The section the card belongs to.
     * @return A (possibly empty) list of all cards that belong to the given section
     */
    public void checkValidSection(String section) {
        logger.info("Checking if valid section");
        for (CardSections c : CardSections.values()) {
            if (c.toString().equals(section)) {
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The section specified is not one of 'ForSale', 'Wanted', or 'Exchange'");
    }
}

