package org.seng302.example.Logins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.example.MainApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LoginsController {

    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());
    private LoginsRepository loginsRepository;

    @Autowired
    public LoginsController(LoginsRepository userRepository) {
        this.loginsRepository = userRepository;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public Logins verifyLogin(@Valid @RequestBody Logins login) {
        return login;
    }
}