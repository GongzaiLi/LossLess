package com.seng302.wasteless.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user in Database
     *
     * @param user      The user object to create
     * @return          The created user
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Checks if a user already has given email
     *
     * @param email     The email to check
     * @return          Whether the email is take or not
     */
    public boolean checkEmailAlreadyUsed(String email) {
        return userRepository.findFirstByEmail(email) != null;
    }

    /**
     * Find user by id
     *
     * @param id        The id of the user to find
     * @return          The found user, if any, or wise null
     */
    public User findUserById(Integer id) {
        return userRepository.findFirstById(id);
    }

    /**
     * Find user by email
     *
     * @param email     The email address to find the user for
     * @return          The found user, if any
     */
    public User findUserByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    /**
     * Search for users by a search query.
     *      * Ordered by full matches then partial matches, and by firstname > lastname > nickname > middlename
     *
     * @param searchQuery       The search query
     * @return                  A set of all matching users
     */
    public Set<User> searchForMatchingUsers(String searchQuery) {
        // full matches
        Set<User> fullMatches = userRepository.findAllByFirstNameOrLastNameOrMiddleNameOrNicknameOrderByFirstNameAscLastNameAscMiddleNameAscNicknameAsc(searchQuery, searchQuery, searchQuery, searchQuery);

        // partial matches
        Set<User> firstNamePartialMatches = userRepository.findAllByFirstNameContainsAndFirstNameNot(searchQuery, searchQuery);
        Set<User> lastNamePartialMatches = userRepository.findAllByLastNameContainsAndLastNameNot(searchQuery, searchQuery);
        Set<User> nicknamePartialMatches = userRepository.findAllByNicknameContainsAndNicknameNot(searchQuery, searchQuery);
        Set<User> middleNamePartialMatches = userRepository.findAllByMiddleNameContainsAndMiddleNameNot(searchQuery, searchQuery);

        // combine matches

        Set<User> combinedResults = new HashSet<>();
        combinedResults.addAll(fullMatches);
        combinedResults.addAll(firstNamePartialMatches);
        combinedResults.addAll(lastNamePartialMatches);
        combinedResults.addAll(nicknamePartialMatches);
        combinedResults.addAll(middleNamePartialMatches);

        return combinedResults;
    }
}
