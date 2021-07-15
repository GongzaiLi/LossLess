package com.seng302.wasteless.controller;

import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * CardController is used for mapping all Restful API requests starting with the address "/cards".
 */
@RestController
public class CardController {
    private static final Logger logger = LogManager.getLogger(CardController.class.getName());


    private final UserService userService;
    private final BusinessService businessService;
    private final CardService cardService;

    @Autowired
    public CardController(UserService userService, BusinessService businessService, CardService cardService) {
        this.userService = userService;
        this.businessService = businessService;
        this.cardService = cardService;
    }
}
