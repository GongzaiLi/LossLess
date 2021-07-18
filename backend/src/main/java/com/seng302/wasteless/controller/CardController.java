package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.PostCardDto;
import com.seng302.wasteless.dto.mapper.PostCardDtoMapper;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

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
     * Handle post request to /cards endpoint for creating card.
     * Request are validated for create fields by Spring, if bad then returns 400 with map of errors
     *
     * <p>
     * Returns:
     * 400 BAD_REQUEST If invalid section, invalid creatorId or invalid title.
     * 401 UNAUTHORIZED If no user is logged on.
     * 201 Created If successfully created card.
     *
     * @param cardDtoRequest Dto containing information needed to create a Card
     * @return Status code dependent on success. 400, 401, 403 errors. 201 Created with card id if success.
     */
    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Valid @RequestBody PostCardDto cardDtoRequest) {
        logger.info("Request to create a new card with data Card: {}", cardDtoRequest);

        User user = userService.getCurrentlyLoggedInUser();
        logger.info("Got User {}", user);

        Card card = PostCardDtoMapper.postCardDtoToEntityMapper(cardDtoRequest);

        logger.info("Setting created date");
        card.setCreated(LocalDate.now());

        logger.info("Setting card creator");
        card.setCreator(user);

        card = cardService.createCard(card);

        logger.info("Successfully created card: {}", card.getId());

        JSONObject responseBody = new JSONObject();
        responseBody.put("cardId", card.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    /**
     * Handle get request to /cards/{id} endpoint for getting a specific card.
     * Returns:
     * 400 BAD_REQUEST id not integer.
     * 401 UNAUTHORIZED If no user is logged on.
     * 200 OK If successfully retrieved card.
     *
     * @param cardId id of the card trying to be retrieved
     * @return Status code dependent on success. 400, 401, 406 errors. 200 OK with card id if success.
     */
    @GetMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createUser(@PathVariable("id") Integer cardId) {
        logger.info("Request to get card with id: {}", cardId);

        User user = userService.getCurrentlyLoggedInUser();
        logger.info("Got User {}", user);

        logger.info("Retrieving Card");
        Card card = cardService.findCardById(cardId);

        logger.info("Successfully found card: {}", card.getId());

        return ResponseEntity.status(HttpStatus.OK).body(card);
    }

}
