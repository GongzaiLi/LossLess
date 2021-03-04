package org.seng302.example.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.example.MainApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
public class UserController {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user){

        //Validate body
        List<String> missingFields = new ArrayList<>();

        if (user.getEmail() == null) {
            missingFields.add("email");
        }

        if (user.getFirstName() == null) {
            missingFields.add("firstName");
        }

        if (user.getLastName() == null) {
            missingFields.add("lastName");
        }

        if (user.getDateOfBirth() == null) {
            missingFields.add("dateOfBirth");
        }

        if (user.getHomeAddress() == null) {
            missingFields.add("homeAddress");
        }

        if (!missingFields.isEmpty()) {

            String errorMessage = "Missing Required Fields: " + missingFields;

            logger.error(errorMessage);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        //Save user object in h2 database
        user = userRepository.save(user);

        logger.info("saved new user {}", user);

        return user;
    }
}