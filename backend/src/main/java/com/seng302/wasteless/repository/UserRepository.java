package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.ArrayList;

/**
 * UserRepository is a repository interface for User.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Integer> {

    User findFirstByEmail(String email);

    User findFirstById(Integer id);

    User findFirstByRole(UserRoles roles);

    ArrayList<User> findAllByFirstNameContainsOrLastNameContainsOrMiddleNameContainsOrNicknameContainsAllIgnoreCase(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nicknameQuery, Pageable pageable);

    @Query(value = "select * from User where id = (select administrators_id from Business_administrators where business_id = :businessId and administrators_id = :userId)", nativeQuery = true)
    User findUserContainBusinessIdAndContainAdminId(@Param("businessId") Integer businessId, @Param("userId") Integer userId);

}
