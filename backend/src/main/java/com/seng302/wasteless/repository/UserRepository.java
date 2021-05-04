package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.LinkedHashSet;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Integer> {

    User findFirstByEmail(String email);

    User findFirstById(Integer id);

    User findFirstByRole(UserRoles roles);

    LinkedHashSet<User> findAllByFirstNameOrLastNameOrMiddleNameOrNicknameOrderByFirstNameAscLastNameAscMiddleNameAscNicknameAsc(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nickNameQuery);

    LinkedHashSet<User> findAllByFirstNameContainsAndFirstNameNot(String firstNameQuery, String firstNameNot);

    LinkedHashSet<User> findAllByLastNameContainsAndLastNameNot(String lastNameQuery, String firstNameNot);

    LinkedHashSet<User> findAllByMiddleNameContainsAndMiddleNameNot(String middleNameQuery, String middleNameNot);

    LinkedHashSet<User> findAllByNicknameContainsAndNicknameNot(String nicknameQuery, String nicknameNot);


    @Query(value = "select * from user where id = (select administrators_id from business_administrators where business_id = :businessId and administrator_id = :userId)", nativeQuery = true)
    User findUserContainBusinessIdAndContainAdminId(@Param("businessId") Integer businessId, @Param("userId") Integer userId);

}
