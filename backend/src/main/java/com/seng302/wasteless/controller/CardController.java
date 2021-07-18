package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.CardSections;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Object> createCard(@Valid @RequestBody PostCardDto cardDtoRequest) {
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

    // Commented out code as this is for the S302T700-172 Validation
//    /**
//     * Returns a json object of bad field found in the request
//     *
//     * @param exception The exception thrown by Spring when it detects invalid data
//     * @return Map of field name that had the error and a message describing the error.
//     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Map<String, String> handleValidationExceptions(
//            MethodArgumentNotValidException exception) {
//        Map<String, String> errors;
//        errors = new HashMap<>();
//        exception.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
////            logger.error(errorMessage); it doesnt work I am not sure why
//            errors.put(fieldName, errorMessage);
//        });
//        return errors;
//    }

}
