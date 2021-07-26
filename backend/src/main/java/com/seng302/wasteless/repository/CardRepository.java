package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CardRepository is a repository interface for the Card entity.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Integer> {

     /**
     * Returns the first card found with the given ID. Will always be correct card as the id is unique
     * @param cardId The id as an integer of the card being requested
     * @return Will return the card object that with the requested id. returns null if none found.
     */
    Card findFirstById(Integer cardId);

    /**
     * Returns all cards that belong to the given CardSections.
     * @param section The section the card belongs to.
     * @return A (possibly empty) list of all cards that belong to the given section
     */
    List<Card> findBySection(CardSections section);

    /**
     * Returns all cards that belong to the current user.
     *
     * @param userId The id of the current user.
     * @return A (possibly empty) list of all cards that belong to the current user.
     */
    List<Card> findAllByCreator_IdOrderByDisplayPeriodEnd(Integer userId);

    /**
     * Returns all cards whose display period end datetime is less than the given datetime.
     *
     * @param time The datetime to compare.
     * @return List of all cards whose display period end datetime is less than the given datetime.
     */
    List<Card> findAllByDisplayPeriodEndLessThan(LocalDateTime time);
}
