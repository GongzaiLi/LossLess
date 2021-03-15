package org.seng302.example.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Set;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Integer> {

    User findFirstByEmail(String email);

    User findFirstById(Integer id);

    List<User> findAllByFirstNameContainsOrLastNameContainsOrMiddleNameOrNicknameContains(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nickNameQuery);

    Set<User> findAllByFirstNameOrLastNameOrMiddleNameOrNicknameOrderByFirstNameAscLastNameAscMiddleNameAscNicknameAsc(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nickNameQuery);

    Set<User> findAllByFirstNameContainsAndFirstNameNot(String firstNameQuery, String firstNameNot);

    Set<User> findAllByLastNameContainsAndLastNameNot(String lastNameQuery, String firstNameNot);

}
