package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserRepository is a repository interface for User.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Integer> {

    User findFirstByEmail(String email);

    User findFirstById(Integer id);

    User findFirstByRole(UserRoles roles);

    Integer countAllByFirstNameContainsOrLastNameContainsOrMiddleNameContainsOrNicknameContainsAllIgnoreCase(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nicknameQuery);

    List<User> findAllByFirstNameContainsOrLastNameContainsOrMiddleNameContainsOrNicknameContainsAllIgnoreCase(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nicknameQuery, Pageable pageable);

    @Query(value = "select * from User where id = (select administrators_id from Business_administrators where business_id = :businessId and administrators_id = :userId)", nativeQuery = true)
    User findUserContainBusinessIdAndContainAdminId(@Param("businessId") Integer businessId, @Param("userId") Integer userId);

    @Query(value = "select * from User where id in (select User_id from User_listingsLiked where listingsLiked_id = :listingId)", nativeQuery = true)
    List<User> findAllByLikedListingId(@Param("listingId") Integer likedListingId);

    @Transactional //Describes a transaction attribute on an individual method or on a class.
    @Modifying //Indicates a query method should be considered as modifying query
    @Query(value = "delete from User_listingsLiked where listingsLiked_id = :listingId", nativeQuery = true)
    void unlikePurchasedListingAllUsers(@Param("listingId") Integer likedListingId);
}
