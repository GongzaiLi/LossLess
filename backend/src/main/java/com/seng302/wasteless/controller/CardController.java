package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;

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

    /**
     * Handle post request to /cards endpoint for creating card.
     * Request are validated for create fields by Spring, if bad then returns 400 with map of errors
     *
     * <p>
     * Returns:
     * 400 BAD_REQUEST If invalid section, invalid creatorId or invalid title.
     * 401 UNAUTHORIZED If no user is logged on.
     * 403 FORBIDDEN If user not allowed to make request (not global admin or not the Card Creator).
     * 201 Created If successfully created card.
     *
     * @param possibleCard Inventory Object
     * @return Status code dependent on success. 400, 401, 403 errors. 201 Created with card id if success.
     */
    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Valid @RequestBody Card possibleCard) {
        logger.info("Request to create a new card with data Card: {}", possibleCard);

        User user = userService.getCurrentlyLoggedInUser();
        logger.info("Got User {}", user);

        if (!user.checkUserGlobalAdmin() && possibleCard.checkUserIsCreator(user)) {
            logger.warn("Cannot create Card. User: {} is not global admin or user is not card creator", user);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application");
        }
        logger.info("User: {} validated as global admin.", user);

        logger.info("Setting created date");
        possibleCard.setCreated(LocalDate.now());

        Card card = cardService.createCard(possibleCard);

        logger.info("Successfully created card: {}", card.getId());

        JSONObject responseBody = new JSONObject();
        responseBody.put("cardId", card.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

}
