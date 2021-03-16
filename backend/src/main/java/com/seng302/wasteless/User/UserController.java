package com.seng302.wasteless.User;

import com.fasterxml.jackson.annotation.JsonView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


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
     * @param user The user object parsed from the request body by spring
     * @return 201 created, 400 bad request with json of errors, 409 email address already used
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {

        //Invalid users are detected by Spring and are rejected as 400.

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
        ResponseCookie responseCookie = ResponseCookie.from("JSESSIONID", "Example")
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
        if (type != null && !type.getValue().contains("USER")) {
            logger.warn("Access token is missing or invalid: " + type.getValue());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is missing or invalid");
        }

        Set<User> searchResults = userService.searchForMatchingUsers(searchQuery);

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



    @GetMapping("/users/{id}")
    @JsonView(UserViews.GetUserView.class)
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer userId, HttpServletRequest request) {
        User possibleUser = userService.findUserById(userId);
        logger.info("possible User{}", possibleUser);

        Cookie type = WebUtils.getCookie(request, "JSESSIONID");
        if (type != null && !type.getValue().contains("USER")) {
            logger.warn("Access token is missing or invalid: " + type.getValue());
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


    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Object> verifyLogin(@Validated @RequestBody Login login) {

        User savedUser = userService.findUserByEmail(login.getEmail());
        if (savedUser == null) {
            logger.warn("Attempted to login to account that does not exist, dropping request: {}", login.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {

            String enteredPassword = login.getPassword();
            String savedPasswordHash = savedUser.getPassword();
            String savedSalt = savedUser.getSalt();

            boolean correctPassword = encryption.verifyUserPassword(enteredPassword, savedPasswordHash, savedSalt);

            if (!correctPassword) {
                logger.warn("Attempted to login to account with incorrect password, dropping request: {}", login.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else {
                logger.info("Account {}, logged into successfully", login.getEmail());
                JSONObject responseBody = new JSONObject();
                responseBody.put("id", savedUser.getId());

                ResponseCookie responseCookie = ResponseCookie.from("JSESSIONID", savedUser.getId().toString() + "_USER")
                        .httpOnly(true)
                        .path("/")
                        .build();

                return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(responseBody);



                //   AuthenticatedResponse:
                //      description: >
                //        Response returned to client when they have performed an action to gain authentication (registering or logging in).
                //        This response includes a session token that the client can use in future API requests to authenticate itself.
                //        This session token is set as a cookie with name 'JSESSIONID', and will need to be included in subsequent requests to the server.
                //      content:
                //        application/json:
                //          schema:
                //            type: object
                //            properties:
                //              userId:
                //                type: integer
                //                description: The ID of the user that has just been authenticated
                //                example: 100
                //
            }


        }

    }
}