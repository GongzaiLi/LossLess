package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * CardRepository is a repository interface for the Card entity.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Integer> {

    Card findFirstById(Integer id);
}
