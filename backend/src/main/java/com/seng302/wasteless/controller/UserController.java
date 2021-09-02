package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetUserDto;
import com.seng302.wasteless.dto.LoginDto;
import com.seng302.wasteless.dto.PutUserDto;
import com.seng302.wasteless.dto.UserSearchDto;
import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.dto.mapper.UserSearchDtoMapper;
import com.seng302.wasteless.model.*;
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
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

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
        logger.info("Email validated for user: {}", user);

        if (!user.checkDateOfBirthValid()) {
            logger.warn("Invalid date for user: {}", user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date out of expected range");
        }
        logger.debug("Validated date for user: {}", user);


        logger.debug("Setting created date");
        user.setCreated(LocalDate.now());
        logger.debug("Setting created date");
        user.setRole(UserRoles.USER);

        logger.debug("Logging in user: {}", user);
        String tempEmail = user.getEmail();
        String tempPassword = user.getPassword();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRoles.USER);


        //Save user object in h2 database
        logger.debug("Creating Address Entity for user: {}", user);
        addressService.createAddress(user.getHomeAddress());
        logger.debug("Creating user: {}", user);
        userService.createUser(user);

        logger.info("Successfully registered user: {}", user.getId());

        logger.debug("Authenticating user: {}", user.getId());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                tempEmail, tempPassword);
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);

        logger.info("Saved and authenticated new user {}", user);

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
     * Returns a json object of bad field found in the request
     *
     * @param exception The exception thrown by Spring when it detects invalid data
     * @return Map of field name that had the error and a message describing the error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
//            logger.error(errorMessage); doesnt work. I am not sure why.
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Returns a json object of bad field found in the request
     *
     * @param exception The exception thrown by Spring when it detects invalid data
     * @return Map of field name that had the error and a message describing the error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleValidationExceptions(
            ConstraintViolationException exception) {

        Map<String, String> errors = new HashMap<>();

        String constraintName = exception.getConstraintViolations().toString();
        String errorMsg = exception.getMessage();

        errors.put(constraintName, errorMsg);
        return errors;
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
     * Endpoint to GET all notifications of the logged in user
     *
     * @return  200 OK if succesful request, With all notifications for logged in user
     */
    @GetMapping("/users/notifications")
    public ResponseEntity<Object> getNotifications() {
        User user = userService.getCurrentlyLoggedInUser();
        logger.info("Request to get notifications for user: {}", user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.findAllNotificationsByUserId(user.getId()));
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

        logger.debug("Request to make user: {} an application admin", userId);

        logger.debug("Trying to find user with ID: {}", userId);
        User possibleUser = userService.findUserById(userId);
        User loggedInUser = userService.getCurrentlyLoggedInUser();

         if (!loggedInUser.checkUserDefaultAdmin()) {

            logger.warn("User {} who is not default admin tried to make another user admin", loggedInUser.getId());
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Must be default admin to make others admin");

        } else if (possibleUser.checkUserDefaultAdmin()) {

            logger.warn("User {} tried to make User {} (who is default application admin) admin.", loggedInUser.getId(), possibleUser.getId());
             throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Cannot change role of default application admin");

        }

        logger.info("User: {} found using Id : {}", possibleUser, userId);

        logger.debug("Setting role to admin for user: {}", possibleUser.getId());

        possibleUser.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);

        logger.debug("Updating user: {} ith new role", possibleUser.getId());

        userService.updateUser(possibleUser);

        logger.info("User: {} successfully made application administrator.", possibleUser.getId());

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

        logger.debug("Request to revoke admin status for  user: {}", userId);

        logger.debug("Trying to find user with ID: {}", userId);
        User possibleUser = userService.findUserById(userId);
        logger.info("User: {} found using Id : {}", possibleUser, userId);

        User loggedInUser = userService.getCurrentlyLoggedInUser();

        if (!loggedInUser.checkUserDefaultAdmin()) {

            logger.warn("User {} who is not the default admin tried to revoke another user admin", loggedInUser.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Must be default admin to revoke others admin");

        } else if (possibleUser.checkUserDefaultAdmin()) {

            logger.warn("User {} tried to revoke their own admin rights.", loggedInUser.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot revoke your own admin rights");

        }

        logger.debug("User: {} found using Id : {}", possibleUser, userId);

        logger.debug("Revoking admin status for user: {} ", possibleUser.getId());
        possibleUser.setRole(UserRoles.USER);
        logger.debug("Updating user: {} ith new role", possibleUser.getId());
        userService.updateUser(possibleUser);
        logger.info("Successfully revoked admin rights for user: {}", possibleUser.getId());

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

        logger.debug("Request to authenticate user login for user with data: {}" , login);


        User savedUser = userService.findUserByEmail(login.getEmail());
        if (savedUser == null) {
            logger.warn("Attempted to login to account that does not exist, dropping request: {}", login.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have tried to log into an account with an email " +
                    "that is not registered.");
        } else {
            logger.debug("User: {} found using data : {}", savedUser, login);
            logger.debug("Authenticating user: {} with {}", savedUser, login);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    login.getEmail(), login.getPassword());
            try {
                Authentication auth = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(auth);

                JSONObject responseBody = new JSONObject();
                responseBody.put("userId", savedUser.getId());
                logger.debug("Getting user ID for user: {}", savedUser);


                logger.info("Successfully logged into user: {} with {}", savedUser, login);

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);

            } catch (AuthenticationException e) {
                logger.warn("Login unsuccessful. {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect email or password");
            }


        }

    }


    /**
     * Handle put request to /users endpoint
     *
     * If changing password checks if the old password matches current password
     * Validates inputted data using same validation as registration.
     *
     * Returns 200 on success
     * Returns 400 if password is incorrect, email is invalid or any invalid inputs e.g. date, address etc.
     * Returns 401 if unauthorised, handled by spring security
     * Returns 409 if email already exist
     *
     * @param modifiedUser Dto containing information needed to update a user
     * @return  Response code with message, see above for codes
     */
    @PutMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> modifyUser(@Valid @RequestBody PutUserDto modifiedUser) {
        User currentUser = userService.getCurrentlyLoggedInUser();

        if (modifiedUser.getNewPassword() != null && !modifiedUser.getNewPassword().isEmpty()) {
            if (passwordEncoder.matches(modifiedUser.getPassword(), currentUser.getPassword())) {
                currentUser.setPassword(passwordEncoder.encode(modifiedUser.getNewPassword()));
            } else {
                logger.warn("Attempted to update password but current password is incorrect, dropping request: {}", modifiedUser);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
            }
        }

        if (!modifiedUser.getEmail().equals(currentUser.getEmail())) {
            if (userService.checkEmailAlreadyUsed(modifiedUser.getEmail())) {
                logger.warn("Attempted to update user with already used email, dropping request: {}", modifiedUser);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempted to update user with already used email");
            }

            if (!userService.checkEmailValid(modifiedUser.getEmail())) {
                logger.warn("Attempted to update user with invalid email, dropping request: {}", modifiedUser);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address is invalid");
            }
            logger.info("New email validated for user with ID {}", currentUser.getId());
        }

        if (!currentUser.getDateOfBirth().equals(modifiedUser.getDateOfBirth())) {
            currentUser.setDateOfBirth(modifiedUser.getDateOfBirth());
            if (!currentUser.checkDateOfBirthValid()) {
                logger.warn("Invalid date for user: {}", modifiedUser);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date out of expected range");
            }
        }

        if (!currentUser.getHomeAddress().equals(modifiedUser.getHomeAddress())) {
            logger.debug("Creating new Address Entity for user with ID", currentUser.getId());
            addressService.createAddress(modifiedUser.getHomeAddress());
        }

        logger.debug("Updating user: {}", modifiedUser);
        userService.updateUserDetails(currentUser, modifiedUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}