package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetUserDto;
import com.seng302.wasteless.dto.LoginDto;
import com.seng302.wasteless.dto.PutUserDto;
import com.seng302.wasteless.dto.UserSearchDto;
import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.dto.mapper.UserSearchDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.UserViews;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * UserController is used for mapping all Restful API requests starting with the address "/users".
 */
@RestController
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class.getName());
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final UserService userService;
    private final AddressService addressService;
    private final NotificationService notificationService;

    @Autowired
    public UserController(UserService userService,
                          AddressService addressService,
                          BCryptPasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          NotificationService notificationService) {
        this.addressService = addressService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.notificationService = notificationService;
    }

    /**
     * Handle post request to /user endpoint for creating users
     * Request validated for create fields by Spring, if bad then returns 400 with map of errors
     * Checks email has not been previously used, if already used returns 409
     *
     * The @Valid annotation ensures the correct fields are present
     * The @JsonView prevents injection of readonly fields
     *
     * @param user The user object parsed from the request body by spring
     * @return 201 created, 400 bad request with json of errors, 409 email address already used
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Valid @RequestBody @JsonView(UserViews.PostUserRequestView.class) User user) {

        logger.debug("Request to create a new with data User: {}", user);

        if (userService.checkEmailAlreadyUsed(user.getEmail())) {
            logger.warn("Attempted to create user with already used email, dropping request: {}", user);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempted to create user with already used email");
        }

        //check the email validation
        if (!userService.checkEmailValid(user.getEmail())) {
            logger.warn("Attempted to create user with invalid email, dropping request: {}", user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address is invalid");
        }

        if (!user.checkDateOfBirthValid()) {
            logger.warn("Invalid date for user: {}", user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date out of expected range");
        }

        user.setCreated(LocalDate.now());
        user.setRole(UserRoles.USER);

        String tempEmail = user.getEmail();
        String tempPassword = user.getPassword();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRoles.USER);

        addressService.createAddress(user.getHomeAddress());
        userService.createUser(user);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                tempEmail, tempPassword);
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);

        JSONObject responseBody = new JSONObject();
        responseBody.put("id", user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }


    /**
     * Search for users by a search query.
     *
     * Searches first, last, middle, and nicknames for partial or full matches with the query, case insensitive.
     *
     * Takes a search query, offset, and count. Returns upto 'count' matching results, offset by the offset.
     * Sorted by sortBy, Sort direction by sortDirection
     *
     * Default values: offset is 0, count is 10, sortBy is ID, sortDirection is ASC
     *
     * @param searchQuery       The query string to search for in users
     * @param offset            The offset of the search (how many pages of results to 'skip')
     * @param count             The number of results to return
     * @param sortBy            The field (if any) to sort by
     * @return                  List of users who match the search query, maximum length of count, offset by offset.
     */
    @GetMapping("/users/search")
    public ResponseEntity<Object> searchUsers (@RequestParam(value = "searchQuery") String searchQuery,
                                               @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                               @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                               @RequestParam(value = "sortBy", required = false, defaultValue = "NONE") String sortBy,
                                               @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection
                                               ) {

        logger.debug("Request to search for users with query: {}", searchQuery);
        logger.info("Received count:{} offset:{}", count, offset);
        logger.info("Using count:{} offset:{} sortBy:{}", count, offset, sortBy);
        UserSearchSortTypes sortType;

        try {
            sortType = UserSearchSortTypes.valueOf(sortBy);
        } catch (IllegalArgumentException e) {
            logger.info("Invalid value for sortBy. Value was {}", sortBy);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid value for sortBy. Acceptable values are: NAME, NICKNAME, EMAIL, ROLE, NONE");
        }

        if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")) {
            logger.info("Invalid value for sortDirection. Value was {}", sortDirection);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid value for sortDirection. Acceptable values are: ASC, DESC");
        }

        if (count < 1) {
            logger.info("Count must be great than or equal to one. Value was {}", count);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Count must be great than or equal to one.");
        } else if (offset < 0) {
            logger.info("Offset must be great than or equal to one. Value was {}", offset);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Offset must be positive if provided.");
        }

        logger.debug("Getting users matching query: {}", searchQuery);

        UserSearchDto userSearchDto = UserSearchDtoMapper.toGetUserSearchDto(searchQuery, count, offset, sortType, sortDirection);

        return ResponseEntity.status(HttpStatus.OK).body(userSearchDto);
    }

    /**
     * Uses a Get Request to grab the user with the specified ID
     * Returns either an unacceptable response if ID doesnt exist,
     * a body showing the details of the user if it does exist
     * and unauthorized if a user hasn't logged in
     * <p>
     *
     * @param userId The userID integer
     * @return 200 okay with user, 401 unauthorised, 406 not acceptable
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer userId) {
        logger.debug("Request to get a user ith ID: {}", userId);

        User userToGet = userService.findUserById(userId);
        logger.info("Account: {} retrieved successfully using ID: {}", userToGet, userId);

        GetUserDto getUserDto = GetUserDtoMapper.toGetUserDto(userToGet);

        return ResponseEntity.status(HttpStatus.OK).body(getUserDto);

    }

    /**
     * Endpoint to GET all notifications of the logged-in user
     * @param tags list of Notification tags to match Notifications (can be null)
     * @param archived A boolean, true if the user wants archived notifications
     * @return 200 OK if successful request, With all notifications for logged in user
     */
    @GetMapping("/users/notifications")
    public ResponseEntity<Object> getNotifications(@RequestParam(value = "tags") Optional<List<String>> tags,
                                                   @RequestParam(value = "archived") Optional<Boolean> archived) {
        User user = userService.getCurrentlyLoggedInUser();
        logger.info("Request to get notifications for user: {}", user.getId());

        List<Notification> getNotifications = notificationService.filterNotifications(user.getId(), tags, archived);
        return ResponseEntity.status(HttpStatus.OK).body(getNotifications);
    }

    /**
     * Endpoint to make a specified user an admin. Sets user role to GLOBAL_APPLICATION_ADMIN
     * if successful. Returns 406 NOT_ACCEPTABLE status if the user id does not exist.
     * Returns 403 FORBIDDEN if the user making the request is not an admin.
     *
     * @param userId The id of the user to be made an admin
     * @return 200 OK, 406 Not Acceptable, 403 Forbidden
     */
    @PutMapping("/users/{id}/makeAdmin")
    public ResponseEntity<Object> makeAdmin(@PathVariable("id") Integer userId) {
        User possibleUser = userService.findUserById(userId);
        User loggedInUser = userService.getCurrentlyLoggedInUser();

         if (!loggedInUser.checkUserDefaultAdmin()) {
            logger.warn("User {} who is not default admin tried to make another user {} admin", loggedInUser.getId(), userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Must be default admin to make others admin");
        } else if (possibleUser.checkUserDefaultAdmin()) {
            logger.warn("User {} tried to make User {} (who is default application admin) admin.", loggedInUser.getId(), possibleUser.getId());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Cannot change role of default application admin");
        }

        possibleUser.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        userService.updateUser(possibleUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Endpoint to revoke a specified user's admin role. Sets user role to USER
     * if successful.
     *
     * @param userId The id of the user to be made an admin
     * @return 200 OK if successful. 406 NOT_ACCEPTABLE status if the user id does not exist.
     * 403 FORBIDDEN if the user making the request is not an admin. 409 CONFLICT if the admin
     * tries to revoke their own admin status.
     */
    @PutMapping("/users/{id}/revokeAdmin")
    public ResponseEntity<Object> revokeAdmin(@PathVariable("id") Integer userId) {
        User possibleUser = userService.findUserById(userId);
        User loggedInUser = userService.getCurrentlyLoggedInUser();

        if (!loggedInUser.checkUserDefaultAdmin()) {
            logger.warn("User {} who is not the default admin tried to revoke another user admin", loggedInUser.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Must be default admin to revoke others admin");
        } else if (possibleUser.checkUserDefaultAdmin()) {
            logger.warn("User {} tried to revoke their own admin rights.", loggedInUser.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot revoke your own admin rights");
        }

        possibleUser.setRole(UserRoles.USER);
        userService.updateUser(possibleUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Takes an inputed username and password and checks the credentials against the database of saved users.
     * Returns either a bad request response or an authenticated ok response with a JSESSIONID cookie.
     *
     * @param login The login object parsed from the request body by spring
     * @return 200 ok for correct login, 400 bad request otherwise
     */
    @PostMapping(value = "/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> verifyLogin(@Validated @RequestBody LoginDto login) {
        User savedUser = userService.findUserByEmail(login.getEmail());
        if (savedUser == null) {
            logger.warn("Attempted to login to account that does not exist, dropping request: {}", login.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have tried to log into an account with an email " +
                    "that is not registered.");
        } else {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    login.getEmail(), login.getPassword());
            try {
                Authentication auth = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(auth);

                JSONObject responseBody = new JSONObject();
                responseBody.put("userId", savedUser.getId());

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);
            } catch (AuthenticationException e) {
                logger.warn("Login unsuccessful. {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect email or password");
            }
        }
    }


    /**
     * Handle put request modify user
     *
     * If changing password or email checks if the old password matches current password
     * Allows (D)GAA to modify user without validation of current password.
     *
     * Validates inputted data using same validation as registration.
     *
     * Keeps user logged in if changing username or password
     *
     * Returns 200 on success
     * Returns 400 if password is incorrect, email is invalid or any invalid inputs e.g. date, address etc.
     * Returns 401 if unauthorised, handled by spring security
     * Returns 403 if forbidden, user tried to make request to another user and was not a DGAA/GAA
     * Returns 409 if email already exist
     *
     * @param modifiedUser Dto containing information needed to update a user
     * @return  Response code with message, see above for codes
     */
    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> modifyUser(@Valid @RequestBody PutUserDto modifiedUser, @PathVariable("id") Integer userId) {

        User loggedInUser = userService.getCurrentlyLoggedInUser();
        User userToModify = userService.getUserToModify(userId);

        boolean userModifyingThemselves = loggedInUser.getId().equals(userToModify.getId());
        boolean userCountryChanged = !modifiedUser.getHomeAddress().getCountry().equals(userToModify.getHomeAddress().getCountry());

        if (userModifyingThemselves && modifiedUser.getNewPassword() != null) {
            if (modifiedUser.getPassword() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password required when updating password");
            }
            if (!passwordEncoder.matches(modifiedUser.getPassword(), userToModify.getPassword())) {
                logger.warn("Attempted to update user but password is incorrect, dropping request: {}", modifiedUser);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
            }
        }

        userService.modifyUserDateOfBirth(userToModify, modifiedUser.getDateOfBirth());
        userService.modifyUserHomeAddress(userToModify, modifiedUser.getHomeAddress());
        userService.modifyUserPassword(userToModify, modifiedUser.getNewPassword());

        if (modifiedUser.getEmail() != null) {
            userService.updateUserEmail(userToModify, modifiedUser.getEmail());

            if (userModifyingThemselves) {
                // We need to change the 'username' (email) of the current authentication principal otherwise any requests after will result in 401
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                userDetails.setUsername(modifiedUser.getEmail());
            }
        }

        logger.debug("Updating user: {}", modifiedUser.getEmail());
        userService.updateUserDetails(userToModify, modifiedUser);

        if (userCountryChanged) {
            Notification notification = NotificationService.createNotification(userToModify.getId(),  userToModify.getId(), NotificationType.USER_CURRENCY_CHANGE,
            String.format("You have changed country from %s to %s therefore your currency may have changed. " +
                            "This will not affect the currency of products in your administered business unless you " +
                            "also modify the address of the business.",
                    userToModify.getHomeAddress().getCountry(), modifiedUser.getHomeAddress().getCountry()));
            notificationService.saveNotification(notification);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}