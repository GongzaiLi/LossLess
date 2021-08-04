package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Business;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * BusinessRepository is repository interface for Business.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    /**
     * Find the first business with the given id from the JPA.
     * Ids are unique so there can only ever be one of each type, findFirst allows it to return singular
     * and not a list.
     *
     * @param id        The id of the business to find
     * @return          The business if it was found, or null if no business with the given id
     */
    Business findFirstById(Integer id);

    /**
     * Return all the Businesses administrated by a user.
     * @param userId   The Id of the user
     * @return          A list of businesses administrated with the user
     */
    @Query(value = "Select * from Business where id in (SELECT distinct business_id FROM Business B inner join Business_administrators A where administrators_id = :user_id)", nativeQuery = true)
    List<Business> findBySpecificAdminId(@Param("user_id") Integer userId);


    List<Business> findAllByNameContainsAllIgnoreCase(String businessName, Pageable pageable);

}

