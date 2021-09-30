package com.seng302.wasteless.service;

import com.seng302.wasteless.dto.PutUserDto;
import com.seng302.wasteless.model.*;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User service applies business logic over the User JPA repository.
 */
@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class.getName());

    private UserRepository userRepository;
    private ImageService imageService;
    private final AddressService addressService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       ImageService imageService,
                       AddressService addressService,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
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

        logger.debug("Validated token for user: {} with Email: {}.", user.getId(), currentPrincipalEmail);
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
    public List<User> searchForMatchingUsers(String searchQuery, Integer count, Integer offset, UserSearchSortTypes sortBy, String sortDirection) {

        Pageable pageable = PageRequest.of(
                offset,
                count,
                sortDirection.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.toString()
        );

        return userRepository.findAllByFirstNameContainsOrLastNameContainsOrMiddleNameContainsOrNicknameContainsAllIgnoreCase(searchQuery, searchQuery, searchQuery, searchQuery, pageable);
    }

    /**
     * Calculates the total count of users matching searchQuery.
     * @return the total count of users.
     */
    public Integer getTotalUsersCountMatchingQuery(String searchQuery) {
        return userRepository.countAllByFirstNameContainsOrLastNameContainsOrMiddleNameContainsOrNicknameContainsAllIgnoreCase(searchQuery, searchQuery, searchQuery, searchQuery);
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


    /**
     * Finds IDs of all users who have liked a given listing.
     *
     * We return a list of IDs instead of users as the number of users
     * who like a listing could be very large. As our users eagerly their
     * administered businesses, returning the IDs is far faster than trying to
     * return a list of User objects.
     *
     * @param listing   The listing to find users for
     * @return          List of IDs users who have liked the listing
     */
    public List<Integer> findUserIdsByLikedListing(Listing listing) {
        return userRepository.findAllByLikedListingId(listing.getId());
    }

    /**
     * Remove all likes on users that reference a given listing
     *
     * @param listing   The listing to remove likes for
     */
    public void unlikePurchasedListing(Listing listing) {
        userRepository.unlikePurchasedListingAllUsers(listing.getId());
    }

    /**
     * Add image to a user
     * Calling the method in this way allows for mocking during automated testing
     * @param user User that image is to be added to
     * @param image image that is to be added to user
     */
    public void addImageToUser(User user, Image image) {
        user.setProfileImage(image);
    }

    /**
     * Delete profile image of a user, leaving it null.
     * The actual image file will also get deleted from the filesystem.
     * Make sure that the user must have a profile image, otherwise this method will crash.
     * @param user User for whom image is to be deleted
     */
    public void deleteUserImage(User user) {
        Image oldUserImage = user.getProfileImage();

        // Have to do this before deleting image otherwise we violate a foreign key constraint
        user.setProfileImage(null);
        saveUserChanges(user);

        imageService.deleteImageRecordFromDB(oldUserImage);
        imageService.deleteImageFile(oldUserImage);
    }

    /**
     * Sets the user details to the modified user details
     * and updates the database.
     *
     * Does not handle password or email or DOB or Address.
     *
     * @param user User to be updated
     * @param modifiedUser Dto containing information needed to update a user
     */
    public void updateUserDetails(User user, PutUserDto modifiedUser) {
        user.setFirstName(modifiedUser.getFirstName());
        user.setLastName(modifiedUser.getLastName());
        user.setMiddleName(modifiedUser.getMiddleName());
        user.setNickname(modifiedUser.getNickname());
        user.setBio(modifiedUser.getBio());
        user.setPhoneNumber(modifiedUser.getPhoneNumber());
        updateUser(user);
    }


    /**
     * Given the ID of a user to modify, returns a User object with given ID.
     * Will also check if the user is allowed to modify the user, and if not, throws a
     * 403 FORBIDDEN exception.
     * This should really be in the User Controller but that file is wayyy too big so we
     * decided to put this here for now. Hopefully when the user controller gets refactored
     * we can chuck it back there.
     * @param userId ID of user to modify
     * @return Object of user with given ID
     * @throws ResponseStatusException 403 FORBIDDEN exception if the user is not allowed to modify the user with given id,
     * 406 NOT ACCEPTABLE if given user doesn't exist
     */
    public User getUserToModify(Integer userId) throws ResponseStatusException {
        User loggedInUser = getCurrentlyLoggedInUser();

        if (loggedInUser.getId().equals(userId)) {
            return loggedInUser;
        } else if (loggedInUser.checkUserGlobalAdmin()) {
            return findUserById(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to make change for this user");
        }
    }

    /**
     * Modify the users date of birth after validating that it is valid.
     *
     * Date of birth added to user before validating as the validation method is on the user class
     * The changes will not actually be saved if an error is thrown so no worries.
     *
     * @param userToModify  The user to modify
     * @param dateOfBirth   The new date of birth for the user
     * @throws ResponseStatusException  If invalid date of birth
     */
    public void modifyUserDateOfBirth(User userToModify, LocalDate dateOfBirth) throws ResponseStatusException {
        if (!userToModify.getDateOfBirth().equals(dateOfBirth)) {
            userToModify.setDateOfBirth(dateOfBirth);
            if (!userToModify.checkDateOfBirthValid()) {
                logger.warn("Invalid date of birth {} for user: {}", dateOfBirth, userToModify.getId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date out of expected range");
            }
        }
    }


    /**
     * Creates new home address entity for user and sets home address. After validating its not the same
     * address as current.
     *
     * @param userToModify  The user to modify
     * @param homeAddress   The new address for the user
     */
    public void modifyUserHomeAddress(User userToModify, Address homeAddress) {
        if (!userToModify.getHomeAddress().equals(homeAddress)) {
            logger.debug("Creating new Address Entity for user with ID {}", userToModify.getId());
            addressService.createAddress(homeAddress);
        }
        userToModify.setHomeAddress(homeAddress);
    }

    /**
     * Modify the users password, after validating it is not empty or null.
     *
     * @param userToModify  The user to modify
     * @param newPassword   The new password for the user
     */
    public void modifyUserPassword(User userToModify, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            userToModify.setPassword(passwordEncoder.encode(newPassword));
        }
    }

    /**
     * Updates a users email address, after validating email is valid and not already used.
     *
     * @param userToModify  The user to modify
     * @param newEmail      The new email for the user.
     * @throws ResponseStatusException  If email is already used or is invalid.
     */
    public void updateUserEmail(User userToModify, String newEmail) throws ResponseStatusException {
        if (!newEmail.equals(userToModify.getEmail())) {
            if (checkEmailAlreadyUsed(newEmail)) {
                logger.warn("Attempted to update user with already used email {}, dropping request", newEmail);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempted to update user with already used email");
            }

            if (!checkEmailValid(newEmail)) {
                logger.warn("Attempted to update user with invalid email {}, dropping request", newEmail);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address is invalid");
            }
            logger.debug("New email for user with ID {}, {}", userToModify.getId(), newEmail);

            userToModify.setEmail(newEmail);
        }
    }
}
