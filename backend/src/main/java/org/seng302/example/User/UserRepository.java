package org.seng302.example.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Integer> {

    User findFirstByEmail(String email);

    User findFirstById(Integer id);

    List<User> findAllByFirstNameContainsOrLastNameContainsOrMiddleNameOrNicknameContains(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nickNameQuery);

    List<User> findAllByFirstNameOrLastNameOrMiddleNameOrNicknameOrderByFirstNameAscLastNameAscMiddleNameAscNicknameAsc(String firstNameQuery, String lastNameQuery, String middleNameQuery, String nickNameQuery);

    List<User> findAllByFirstNameContainsAndFirstNameNot(String firstNameQuery, String firstNameNot);

}
