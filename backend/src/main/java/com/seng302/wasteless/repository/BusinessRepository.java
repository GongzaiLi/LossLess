package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

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
     * @param user_id   The Id of the user
     * @return          A list of businesses administrated with the user
     */
    @Query(value = "Select * from business where id in (SELECT distinct business_id FROM Business B inner join BUSINESS_ADMINISTRATORS A where administrators_id = :user_id)", nativeQuery = true)
    List<Business> findBySpecificAdminId(@Param("user_id") Integer user_id);
}

