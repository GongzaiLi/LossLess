package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.CardSections;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * CardController is used for mapping all Restful API requests starting with the address "/cards".
 */
@RestController
public class CardController {
    private static final Logger logger = LogManager.getLogger(CardController.class.getName());


    private final UserService userService;
    private final CardService cardService;

    @Autowired
    public CardController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    /**
     * Handles requests for GET card endpoint. Returns a list of cards in the section given.
     * Throws a 400 error if the section given is not one of the predefined ones ('ForSale', 'Wanted', or 'Exchange').
     * @param section The section for which cards will be retrieved from. This should be in the query parameter named 'section
     * @return A 200 response with the list of cards in the given section
     */
    @GetMapping("/cards")
    public ResponseEntity<Object> getCards(@RequestParam String section) {
        logger.info("GET /cards, section={}", section);

        // Get CardSections enum value with text equal to request param
        CardSections cardSection = null;
        for (CardSections cardSectionValue : CardSections.values()) {
            if (cardSectionValue.toString().equals(section)) {
                cardSection = cardSectionValue;
            }
        }

        if (cardSection == null) {
            logger.warn("Tried to get cards with bad section '{}'", section);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The section specified is not one of 'ForSale', 'Wanted', or 'Exchange'");
        }
        return ResponseEntity.status(HttpStatus.OK).body(cardService.findBySection(cardSection));
    }
}
