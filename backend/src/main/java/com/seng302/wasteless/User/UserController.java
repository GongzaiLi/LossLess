package com.seng302.wasteless.User;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.MainApplicationRunner;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;


@RestController
public class UserController {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService,
                          BCryptPasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
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

        //Check user with this email address does not already exist
        if (userService.checkEmailAlreadyUsed(user.getEmail())) {
            logger.warn("Attempted to create user with already used email, dropping request: {}", user);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Attempted to create user with already used email");
        }

        if (!user.checkDateOfBirthValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date out of expected range");
        }

        user.setCreated(LocalDate.now());

        user.setRole(UserRoles.USER);


        Login login = new Login(user.getEmail(), user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRoles.USER);
        //Save user object in h2 database
        userService.createUser(user);
        logger.info(String.format("Successful registration of user %d", user.getId()));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                login.getEmail(), login.getPassword());
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);



        logger.info("saved new user {}", user);

        JSONObject responseBody = new JSONObject();
        responseBody.put("id", user.getId());


        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);

    }


    /**
     * Search for users by a search query.
     * Ordered by full matches then partial matches, and by firstname > lastname > nickname > middlename
     *
     * @param searchQuery   The query to search by
     * @return              A set of matching results
     */
    @GetMapping("/users/search")
    @JsonView(UserViews.SearchUserView.class) //Only return appropriate fields
    public ResponseEntity<Object> searchUsers (@RequestParam(value = "searchQuery") String searchQuery, HttpServletRequest request) {

        LinkedHashSet<User> searchResults = userService.searchForMatchingUsers(searchQuery);

        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
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
     * @param request       The get request for getting the user
     * @return              200 okay with user, 401 unauthorised, 406 not acceptable
     */
    @GetMapping("/users/{id}")
    @JsonView(UserViews.GetUserView.class)
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer userId, HttpServletRequest request) {
        User possibleUser = userService.findUserById(userId);
        logger.info("possible User{}", possibleUser);

        if (possibleUser == null) {
            logger.warn("ID does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }

        // Not too sure what to do with Response 401 because it's possibly about security but do we need
        // to have U4 for that or is it possible to do without it

        logger.info("Account: {} retrieved successfully", possibleUser);
        return ResponseEntity.status(HttpStatus.OK).body(possibleUser);
    }


    /**
     * Endpoint to make a specified user an admin. Sets user role to GLOBAL_APPLICATION_ADMIN
     * if successful. Returns 406 NOT_ACCEPTABLE status if the user id does not exist.
     * Returns 403 FORBIDDEN if the user making the request is not an admin.
     * @param userId The id of the user to be made an admin
     * @return 200 OK, 406 Not Acceptable, 403 Forbidden
     */
    @PutMapping("/users/{id}/makeAdmin")
    public ResponseEntity<Object> makeAdmin(@PathVariable("id") Integer userId) {

        //Todo authentication

        User possibleUser = userService.findUserById(userId);
        logger.info("possible User{}", possibleUser);

        if (possibleUser == null) {
            logger.warn("ID does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        }

        possibleUser.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN);
        userService.updateUser(possibleUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Endpoint to revoke a specified user's admin role. Sets user role to USER
     * if successful.
     * @param userId The id of the user to be made an admin
     * @return 200 OK if successful. 406 NOT_ACCEPTABLE status if the user id does not exist.
     * 403 FORBIDDEN if the user making the request is not an admin. 409 CONFLICT if the admin
     * tries to revoke their own admin status.
     */
    @PutMapping("/users/{id}/revokeAdmin")
    public ResponseEntity<Object> revokeAdmin(@PathVariable("id") Integer userId) {

        //Todo authentication

        User possibleUser = userService.findUserById(userId);
        logger.info("possible User{}", possibleUser);

        if (possibleUser == null) {
            logger.warn("ID does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("ID does not exist");
        } else if (possibleUser.getRole() == UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot revoke DGAA");
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
    @RequestMapping(value = "/login", method = {RequestMethod.POST} )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> verifyLogin(@Validated @RequestBody Login login) {

        User savedUser = userService.findUserByEmail(login.getEmail());
        if (savedUser == null) {
            logger.warn("Attempted to login to account that does not exist, dropping request: {}", login.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have tried to log into an account with an email " +
                    "that is not registered.");
        } else {

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    login.getEmail(), login.getPassword());
            try {
                Authentication auth = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(auth);

                logger.info("Account {}, logged into successfully", login.getEmail());
                JSONObject responseBody = new JSONObject();
                responseBody.put("id", savedUser.getId());


                return ResponseEntity.status(HttpStatus.OK).body(responseBody);

            } catch (AuthenticationException e) {
                logger.error("Incorrect email or password.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect email or password");
            }


        }

    }
}