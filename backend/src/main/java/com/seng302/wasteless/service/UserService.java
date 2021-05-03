package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;

/**
 * User service applies business logic over the User JPA repository.
 */

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
     * Saves the given user object with updated fields in Database
     *
     * @param user      The updated user object to save in the DB
     */
    public void updateUser(User user) {
        userRepository.save(user);
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
     * Checks if a user with a specified role already exists
     *
     * @param roles     The role using UserRoles
     * @return          boolean whether a user is found or not
     */
    public boolean checkRoleAlreadyExists(UserRoles roles) { return userRepository.findFirstByRole(roles) != null; }

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
     * Checks whether the user is an admin in the business
     *
     * @param businessId    The business ID
     * @param userId        The user ID
     * @return              Returns true if user is admin to business else false
     */
    public boolean checkUserAdminsBusiness(Integer businessId, Integer userId) { return userRepository.findUserContainBusinessIdAndContainAdminId(businessId, userId) != null; }


    /**
     * Search for users by a search query.
     *      * Ordered by full matches then partial matches, and by firstname > lastname > nickname > middlename
     *
     * @param searchQuery       The search query
     * @return                  A set of all matching users
     */
    public LinkedHashSet<User> searchForMatchingUsers(String searchQuery) {
        // full matches
        LinkedHashSet<User> fullMatches = userRepository.findAllByFirstNameOrLastNameOrMiddleNameOrNicknameOrderByFirstNameAscLastNameAscMiddleNameAscNicknameAsc(searchQuery, searchQuery, searchQuery, searchQuery);

        // partial matches
        LinkedHashSet<User> firstNamePartialMatches = userRepository.findAllByFirstNameContainsAndFirstNameNot(searchQuery, searchQuery);
        LinkedHashSet<User> lastNamePartialMatches = userRepository.findAllByLastNameContainsAndLastNameNot(searchQuery, searchQuery);
        LinkedHashSet<User> nicknamePartialMatches = userRepository.findAllByNicknameContainsAndNicknameNot(searchQuery, searchQuery);
        LinkedHashSet<User> middleNamePartialMatches = userRepository.findAllByMiddleNameContainsAndMiddleNameNot(searchQuery, searchQuery);

        // combine matches

        LinkedHashSet<User> combinedResults = new LinkedHashSet<>();
        combinedResults.addAll(fullMatches);
        combinedResults.addAll(firstNamePartialMatches);
        combinedResults.addAll(lastNamePartialMatches);
        combinedResults.addAll(nicknamePartialMatches);
        combinedResults.addAll(middleNamePartialMatches);

        return combinedResults;
    }

    /**
     * Add a primarily administered business to a user/
     *
     * Calling the method in this way allows for mocking during automated testing
     *
     * @param user          The user to add to
     * @param business      The business the user is a primary admin of
     */
    public void addBusinessPrimarilyAdministered(User user, Business business) {
        user.addPrimaryBusiness(business);

    }

    /**
     * Save changes to a user
     *
     * @param user  The user to save changes to
     */
    public void saveUserChanges(User user) {
        userRepository.save(user);
    }

}
