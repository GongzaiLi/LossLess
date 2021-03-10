package org.seng302.example.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.example.MainApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@RestController
public class UserController {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
    private UserRepository userRepository;

    //Todo validation of email, birthday, etc formats

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Handle post request to /user endpoint for creating users
     * Request validated for create fields by Spring, if bad then returns 400 with map of errors
     * Checks email has not been previously used, if already used returns 409
     *
     * @param user  The user object parsed from the request body by spring
     * @return  201 created, 400 bad request with json of errors, 409 email address already used
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user){

        //Invalid users are detected by Spring and are rejected as 400.

        //Check user with this email address does not already exist
        User possibleUser = userRepository.findFirstByEmail(user.getEmail());
        if (possibleUser != null) {
            logger.warn("Attempted to create user with already used email, dropping request: {}", user);
            return ResponseEntity.status(409).build();
        }

        //Save user object in h2 database
        user = userRepository.save(user);

        logger.info("saved new user {}", user);

        return ResponseEntity.status(HttpStatus.CREATED).build();

        //If response with url to where this new user is stored is required, then use
        //URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        //        .buildAndExpand(savedStudent.getId()).toUri();
        //return ResponseEntity.created(location).build();
    }

    /**
     * Returns a json object of bad field found in the request
     *
     * @param exception The exception thrown by Spring when it detects invalid data
     * @return          Map of field name that had the error and a message describing the error.
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
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer userId){
        User possibleUser = userRepository.findFirstById(userId);
        System.out.println(possibleUser);
        if(possibleUser == null){
            logger.warn("Not acceptable error");
            return ResponseEntity.status(406).build();
        }

        // Not too sure what to do with Response 401 because it's possibly about security but do we need
        // to have U4 for that or is it possible to do without it

        logger.info("Account: {} retrieved successfully", possibleUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}