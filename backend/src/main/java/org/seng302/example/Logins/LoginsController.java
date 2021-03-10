package org.seng302.example.Logins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.example.MainApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LoginsController {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
    private LoginsRepository loginsRepository;
    private Encryption encryption;

    @Autowired
    public LoginsController(LoginsRepository userRepository) {
        this.loginsRepository = userRepository;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> verifyLogin(@Valid @RequestBody Logins login) {

        Logins savedLogin = loginsRepository.findByEmail(login.getEmail());
        if (savedLogin == null) {
            logger.warn("Attempted to login to account that does not exist, dropping request: {}", login);
            return ResponseEntity.status(400).build();
        } else {

            String enteredPassword = login.getPassword();
            String savedPasswordHash = savedLogin.getPassword();
            String savedSalt = savedLogin.getSalt();

            boolean correctPassword = encryption.verifyUserPassword(enteredPassword, savedPasswordHash, savedSalt);

            if (!correctPassword) {
                logger.warn("Attempted to login to account with incorrect password, dropping request: {}", login);
                return ResponseEntity.status(400).build();
            } else {
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
        return ResponseEntity.status(200).build();
    }
}