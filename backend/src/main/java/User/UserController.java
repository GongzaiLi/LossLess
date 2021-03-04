package User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.example.MainApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User createUser(@RequestBody UserPostRequestBody requestBody){

        //Get body from request

        //Validate body
        requestBody.getEmail();



        //Other stuff here:
        //Save user object in h2 database

        user = userRepository.save(user);
        logger.info("saved new user {}", user);

        return user;
    }
}