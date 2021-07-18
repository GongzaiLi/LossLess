package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.repository.CardRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
}

