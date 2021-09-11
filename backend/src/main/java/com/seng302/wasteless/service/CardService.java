package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.CardRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
     *
     * @param section The section the card belongs to.
     * @return A (possibly empty) list of all cards that belong to the given section
     */
    public Page<Card> findBySection(CardSections section, Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream().map(Sort.Order::ignoreCase).collect(Collectors.toList());
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders)
        );
        return cardRepository.findBySection(section, pageable);
    }


    /**
     * Returns all cards that belong to the current user.
     *
     * @param userId The id of the current user.
     * @return A (possibly empty) list of all cards that belong to the current user.
     */
    public List<Card> getAllUserCards(Integer userId) {
        return cardRepository.findAllByCreatorIdOrderByDisplayPeriodEnd(userId);
    }

    /**
     * Returns the first card found with the given ID. Will always be correct card as the id is unique
     *
     * @param cardId The id as an integer of the card being requested
     * @return Will return the card object that with the requested id. returns null if none found.
     */
    public Card findById(Integer cardId) {
        return cardRepository.findFirstById(cardId);
    }

    /**
     * Returns the total number of cards that belong to the given section.
     *
     * @param section The marketplace section in which to count cards
     * @return The total number of cards in the given section.
     */
    public Integer numberOfCardsInSection(CardSections section) {
        return cardRepository.countCardBySection(section);
    }

    /**
     * Deletes the database entry of the card that is passed to this method
     *
     * @param card The card to be deleted from the database
     */
    public void deleteCard(Card card) {
        cardRepository.delete(card);
    }


    /**
     * Checks if the string passed in corresponds to the name of a valid section.
     * Throws a BAD REQUEST (400) response error if the section is not valid.
     * All valid sections are values of the CardSections enum.
     * @throws ResponseStatusException if the given section is invalid
     * @param section The section to check.
     */
    public void checkValidSection(String section) {
        logger.info("Checking if '{}' is valid section", section);
        for (CardSections c : CardSections.values()) {
            if (c.toString().equals(section)) {
                return;
            }
        }
        logger.warn("Section '{}' is not a valid section", section);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The section specified is not one of 'ForSale', 'Wanted', or 'Exchange'");
    }

    /**
     * Checks whether a card is allowed to be extended.
     * Card must with 48 hours of expiring or after expiry.
     *
     * @param card  The card to check
     * @return      True if card allowed to be extended, false otherwise
     */
    public boolean checkCardWithinExtendDateRange(Card card) {
        return card.getDisplayPeriodEnd().isAfter(LocalDateTime.now().plusDays(2));
    }

    /**
     * Checks user is either cards creator or is global admin
     *
     * @param card  The card to check permission for
     * @param user  the user to check permission for
     * @return  True if allowed, false otherwise
     */
    public boolean checkUserHasPermissionForCard(Card card, User user) {
        return !card.getCreator().getId().equals(user.getId()) && !user.checkUserGlobalAdmin();
    }
}

