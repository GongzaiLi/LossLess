package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * CardRepository is a repository interface for the Card entity.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Integer> {

    Card findFirstById(Integer id);

    /**
     * Returns all cards that belong to the given CardSections.
     * @param section The section the card belongs to.
     * @return A (possibly empty) list of all cards that belong to the given section
     */
    List<Card> findBySection(CardSections section);
}
