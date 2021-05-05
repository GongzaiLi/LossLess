package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.MainApplicationRunner;
import com.seng302.wasteless.dto.GetUserDto;
import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.repository.BusinessRepository;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.UserViews;
import com.seng302.wasteless.model.Login;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.security.CustomUserDetails;
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
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

/**
 * UserController is used for mapping all Restful API requests starting with the address "/users".
 */
@RestController
public class UserController {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final UserService userService;
    private final AddressService addressService;

    @Autowired
    public UserController(UserService userService,
                          AddressService addressService,
                          BCryptPasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.addressService = addressService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Attempted to create user with already used email");
        }
        logger.info("Email validated for user: {}", user);

        //check the email validation
        if (!userService.checkEmailValid(user.getEmail())) {
            logger.warn("Attempted to create user with invalid email, dropping request: {}", user);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email address is invalid");
        }

        if (!user.checkDateOfBirthValid()) {
            logger.warn("Invalid date for user: {}", user);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date out of expected range");
        }
        logger.debug("Validated date for user: {}", user);


        logger.debug("Setting created date");
        user.setCreated(LocalDate.now());
        logger.debug("Setting created date");
        user.setRole(UserRoles.USER);

        logger.debug("Logging in user: {}", user);
        Login login = new Login(user.getEmail(), user.getPassword());
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
                login.getEmail(), login.getPassword());
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);

        logger.info("Saved and authenticated new user {}", user);

        JSONObject responseBody = new JSONObject();
        responseBody.put("id", user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);



    }


    /**
     * Search for users by a search query.
     * Ordered by full matches then partial matches, and by firstname > lastname > nickname > middlename
     *
     * @param searchQuery   The query to search by
     * @return              A list of matching results
     */
    @GetMapping("/users/search")
    public ResponseEntity<Object> searchUsers (@RequestParam(value = "searchQuery") String searchQuery, HttpServletRequest request) {

        logger.debug("Request to search for users with query: {}", searchQuery);

        logger.debug("Getting users matching query: {}", searchQuery);
        LinkedHashSet<User> searchResults = userService.searchForMatchingUsers(searchQuery);

        List<GetUserDto> searchResultsDto = new ArrayList<>();
        //List<Object> searchResultsDto = new ArrayList <Object>();   //Use Map<> ?

        logger.debug("Adding all matched users to list");
        for (User user : searchResults) {
            searchResultsDto.add(GetUserDtoMapper.toGetUserDto(user));
        }

        logger.info("User matching the query: {} are: {}", searchQuery, searchResultsDto);

        return ResponseEntity.status(HttpStatus.OK).body(searchResultsDto);


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
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
//            logger.error(errorMessage); doesnt work. I am not sure why.
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     *  Uses a Get Request to grab the user with the specified ID
     *  Returns either an unacceptable response if ID doesnt exist,
     *  a body showing the details of the user if it does exist
     *  and unauthorized if a user hasn't logged in
     *
     * @param userId The userID integer
     * @return              200 okay with user, 401 unauthorised, 406 not acceptable
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer userId) {

        logger.debug("Request to get a user ith ID: {}", userId);

        User possibleUser = userService.findUserById(userId);
        logger.debug("possible User: {}", possibleUser);

        if (possibleUser == null) {
            logger.warn("User with ID: {} does not exist", userId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }

        // Not too sure what to do with Response 401 because it's possibly about security but do we need
        // to have U4 for that or is it possible to do without it

        logger.info("Account: {} retrieved successfully using ID: {}", possibleUser, userId);

        GetUserDto getUserDto = GetUserDtoMapper.toGetUserDto(possibleUser);

        return ResponseEntity.status(HttpStatus.OK).body(getUserDto);

    }


    /**
     * Endpoint to make a specified user an admin. Sets user role to GLOBAL_APPLICATION_ADMIN
     * if successful. Returns 406 NOT_ACCEPTABLE status if the user id does not exist.
     * Returns 403 FORBIDDEN if the user making the request is not an admin.
     * @param userId The id of the user to be made an admin
     * @param authentication Spring Security Authentication object, representing current user and token
     * @return 200 OK, 406 Not Acceptable, 403 Forbidden
     */
    @PutMapping("/users/{id}/makeAdmin")
    public ResponseEntity<Object> makeAdmin(@PathVariable("id") Integer userId, Authentication authentication) {

        logger.debug("Request to make user: {} an application admin", userId);

        logger.debug("Trying to find user with ID: {}", userId);
        User possibleUser = userService.findUserById(userId);

        // The Spring Security principal can only be retrieved as an Object and needs to be cast
        // to the correct UserDetails instance, see:
        // https://www.baeldung.com/get-user-in-spring-security
        CustomUserDetails loggedInUser = (CustomUserDetails) authentication.getPrincipal();

        if (possibleUser == null) {
            logger.warn("User with ID: {} does not exist", userId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        } else if (possibleUser.getRole() == UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            logger.warn("User {} tried to make User {} (who is already DGAA) admin.", loggedInUser.getId(), possibleUser.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot change role of a DGAA");
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
     * @param userId The id of the user to be made an admin
     * @param authentication Spring Security Authentication object, representing current user and token
     * @return 200 OK if successful. 406 NOT_ACCEPTABLE status if the user id does not exist.
     * 403 FORBIDDEN if the user making the request is not an admin. 409 CONFLICT if the admin
     * tries to revoke their own admin status.
     */
    @PutMapping("/users/{id}/revokeAdmin")
    public ResponseEntity<Object> revokeAdmin(@PathVariable("id") Integer userId, Authentication authentication) {

        logger.debug("Request to revoke admin status for  user: {}", userId);

        logger.debug("Trying to find user with ID: {}", userId);
        User possibleUser = userService.findUserById(userId);
        logger.info("User: {} found using Id : {}", possibleUser, userId);


        // The Spring Security principal can only be retrieved as an Object and needs to be cast
        // to the correct UserDetails instance, see:
        // https://www.baeldung.com/get-user-in-spring-security
        CustomUserDetails loggedInUser = (CustomUserDetails) authentication.getPrincipal();

        if (possibleUser == null) {
            logger.warn("Could not find user with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        } else if (possibleUser.getId().equals(loggedInUser.getId())) {
            logger.warn("User {} tried to revoke their own admin rights.", loggedInUser.getId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot revoke your own admin rights");
        } else if (possibleUser.getRole() == UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            logger.warn("User {} tried to make User {} (who is already DGAA) admin.", loggedInUser.getId(), possibleUser.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot revoke DGAA");
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
    @RequestMapping(value = "/login", method = {RequestMethod.POST} )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> verifyLogin(@Validated @RequestBody Login login) {

        logger.debug("Request to authenticate user login for user with data: {}" , login);


        User savedUser = userService.findUserByEmail(login.getEmail());
        if (savedUser == null) {
            logger.warn("Attempted to login to account that does not exist, dropping request: {}", login.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have tried to log into an account with an email " +
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
                logger.debug("Getting user ID for user: {}", savedUser);
                responseBody.put("id", savedUser.getId());


                logger.info("Successfully logged into user: {} with {}", savedUser, login);

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);

            } catch (AuthenticationException e) {
                logger.warn("Login unsuccessful. " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect email or password");
            }


        }

    }
}