package com.seng302.wasteless.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.LinkedHashSet;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Integer> {

    User findFirstByEmail(String email);

    User findFirstById(Integer id);

    LinkedHashSet<User> findAllByFirstNameOrLastNameOrMiddleNameOrNicknameOrderByFirstNameAscLastNameAscMiddleNameAscNicknameAsc(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nickNameQuery);

    LinkedHashSet<User> findAllByFirstNameContainsAndFirstNameNot(String firstNameQuery, String firstNameNot);

    LinkedHashSet<User> findAllByLastNameContainsAndLastNameNot(String lastNameQuery, String firstNameNot);

    LinkedHashSet<User> findAllByMiddleNameContainsAndMiddleNameNot(String middleNameQuery, String middleNameNot);

    LinkedHashSet<User> findAllByNicknameContainsAndNicknameNot(String nicknameQuery, String nicknameNot);

}
