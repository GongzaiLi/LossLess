package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.model.UserSearchSortTypes;
import com.seng302.wasteless.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User service applies business logic over the User JPA repository.
 */
@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class.getName());

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
     * Checks if a email address contains an @ symbol at has something preceding and after the @ symbol
     *
     * @param email     The email to check
     * @return          Whether the email is valid or not
     */
    public boolean checkEmailValid(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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

        User possibleUser = userRepository.findFirstById(id);
        if (possibleUser == null) {
            logger.warn("User with ID: {} does not exist", id);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User does not exist");
        }
        return possibleUser;
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
     * Get Currently Logged in user by getting the email and searching by email
     * @return Logged in user or Throw Response Status Exception
     */
    public User getCurrentlyLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.debug("Validating user with Email: {}", currentPrincipalEmail);
        User user = findUserByEmail(currentPrincipalEmail);
        if (user == null) {
            logger.info("Access token invalid for user with Email: {}", currentPrincipalEmail);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session token is invalid");
        }

        logger.info("Validated token for user: {} with Email: {}.", user, currentPrincipalEmail);
        return user;
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
     *  Search for users by a search query. Returns upto count results. Starts at 'page' offset. Sorts by sortBy.
     *  Sort direction sortDirection
     *
     * @param searchQuery   The query to search for
     * @param count         The number of results to return
     * @param offset        The offset to start from
     * @param sortBy        The column to sort by
     * @param sortDirection The direction to sort by (ASC, DESC)
     * @return  An array of users matching the search params
     */
    public ArrayList<User> searchForMatchingUsers(String searchQuery, Integer count, Integer offset, UserSearchSortTypes sortBy, String sortDirection) {

        Pageable pageable = PageRequest.of(
                offset,
                count,
                sortDirection.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.toString()
        );

        return userRepository.findAllByFirstNameContainsOrLastNameContainsOrMiddleNameContainsOrNicknameContainsAllIgnoreCase(searchQuery, searchQuery, searchQuery, searchQuery, pageable);
    }

    /**
     * Calculates the total count of users.
     * @return the total count of users.
     */
    public Long getTotalUsersCount() {
        return userRepository.count();
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
