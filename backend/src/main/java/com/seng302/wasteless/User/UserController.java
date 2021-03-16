package com.seng302.wasteless.User;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.MainApplicationRunner;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.seng302.wasteless.MainApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;


@RestController
public class UserController {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
    private UserService userService;
    private Encryption encryption;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (!user.checkDateOfBirthValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date out of expected range");
        }

        user.setCreated(LocalDate.now());


        /**
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails springUser = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(springUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
         */

        //Create the users salt
        String salt = encryption.getNextSalt().toString();
        user.setSalt(salt);
        //Encrypt the users password
        user.setPassword(encryption.generateHashedPassword(user.getPassword(), salt));
        //Save user object in h2 database
        user = userService.createUser(user);

        logger.info("saved new user {}", user);

        //Todo send back authenticated responses
        ResponseCookie responseCookie = ResponseCookie.from("JSESSIONID", user.getId().toString() + "_USER")
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, responseCookie.toString()).build();

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

        Cookie type = WebUtils.getCookie(request, "JSESSIONID");
        if (type == null || !type.getValue().contains("USER")) {
            logger.warn("Access token is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is missing or invalid");
        }

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
     * @param request
     * @return
     */
    @GetMapping("/users/{id}")
    @JsonView(UserViews.GetUserView.class)
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer userId, HttpServletRequest request) {
        User possibleUser = userService.findUserById(userId);
        logger.info("possible User{}", possibleUser);

        Cookie type = WebUtils.getCookie(request, "JSESSIONID");
        if (type == null || !type.getValue().contains("USER")) {
            logger.warn("Access token is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is missing or invalid");
        }

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
     * Takes an inputed username and password and checks the credentials against the database of saved users.
     * Returns either a bad request response or an authenticated ok response with a JSESSIONID cookie.
     *
     * @param login The login object parsed from the request body by spring
     * @return 200 ok for correct login, 400 bad request otherwise
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> verifyLogin(@Validated @RequestBody Login login) {

        User savedUser = userService.findUserByEmail(login.getEmail());
        if (savedUser == null) {
            logger.warn("Attempted to login to account that does not exist, dropping request: {}", login.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have tried to log into an account with an email " +
                    "that is not registered.");
        } else {

            String enteredPassword = login.getPassword();
            String savedPasswordHash = savedUser.getPassword();
            String savedSalt = savedUser.getSalt();

            boolean correctPassword = encryption.verifyUserPassword(enteredPassword, savedPasswordHash, savedSalt);

            if (!correctPassword) {
                logger.warn("Attempted to login to account with incorrect password, dropping request: {}", login.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have entered an incorrect password.");
            } else {
                logger.info("Account {}, logged into successfully", login.getEmail());
                JSONObject responseBody = new JSONObject();
                responseBody.put("id", savedUser.getId());

                ResponseCookie responseCookie = ResponseCookie.from("JSESSIONID", savedUser.getId().toString() + "_USER")
                        .httpOnly(true)
                        .path("/")
                        .build();

                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(responseBody);

            }


        }

    }
}