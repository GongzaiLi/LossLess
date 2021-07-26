package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.GetCardDto;
import com.seng302.wasteless.dto.GetCardsDto;
import com.seng302.wasteless.dto.PostCardDto;
import com.seng302.wasteless.dto.mapper.PostCardDtoMapper;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.UserService;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CardController is used for mapping all Restful API requests starting with the address "/cards".
 */
@RestController
public class CardController {
    private static final Logger logger = LogManager.getLogger(CardController.class.getName());

    @Value("${max-display-period-seconds}")
    private Integer maxDisplayPeriodSeconds;

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
     *
     * @param section The section for which cards will be retrieved from. This should be in the query parameter named 'section
     * @return A 200 response with the list of cards in the given section
     */
    @GetMapping("/cards")
    public ResponseEntity<Object> getCards(@RequestParam String section, Pageable pageable) {
        logger.info("GET /cards, section={}", section);

        cardService.checkValidSection(section);
        CardSections cardSection = CardSections.fromString(section);

        Page<Card> cards = cardService.findBySection(cardSection, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GetCardsDto(cards.getContent(), cards.getTotalElements()));
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

        cardService.checkValidSection(cardDtoRequest.getSection());

        Card card = PostCardDtoMapper.postCardDtoToEntityMapper(cardDtoRequest);

        logger.info("Setting created date");
        card.setCreated(LocalDateTime.now());

        logger.info("Setting card expiring date");
        card.setDisplayPeriodEnd(LocalDateTime.now().plusSeconds(maxDisplayPeriodSeconds));

        logger.info("Setting card creator");
        card.setCreator(user);

        card = cardService.createCard(card);

        logger.info("Successfully created card: {}", card.getId());

        JSONObject responseBody = new JSONObject();
        responseBody.put("cardId", card.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    /**
     * Handle get request to /cards/{id}/expiring endpoint for getting a users expiring cards.
     * Request are validated for create fields by Spring, if bad then returns 400 with map of errors
     *
     * Returns:
     * 400 BAD_REQUEST If invalid section, invalid creatorId or invalid title.
     * 401 UNAUTHORIZED If no user is logged on.
     * 403 FORBIDDEN If trying to get cards of another user.
     * 200 If successfully got list of expiring cards.
     *
     * @return Status code dependent on success. 400, 401, 403 errors. 200 List of cards.
     */
    @GetMapping("cards/{id}/expiring")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> getExpiringCards(@PathVariable("id") Integer userId) {
        logger.info("Request to get a user's expiring cards with user id: {}", userId);

        User user = userService.getCurrentlyLoggedInUser();

        if (!userId.equals(user.getId())) {
            logger.info("User ({}) tried to access the expiring cards of another user ({}).", user.getId(), userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot view the expiring cards of another user.");
        }

        List<Card> allCards = cardService.getAllUserCards(user.getId());
        List<Card> expiredCards = new ArrayList<>();

        for (Card card : allCards) {
            if (card.getDisplayPeriodEnd().minusWeeks(1).isBefore(LocalDateTime.now())) {
                expiredCards.add(card);
            }
        }
        logger.info("User's soon to expire cards: {}.", expiredCards);

        List<GetCardDto> expiredCardDTOs = expiredCards.stream().map(GetCardDto::new).collect(Collectors.toList());   // Make list of DTOs from list of Cards. WHY IS JAVA SO VERBOSE????

        return ResponseEntity.status(HttpStatus.OK).body(expiredCardDTOs);
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

        logger.info("Retrieving Card");
        Card card = cardService.findCardById(cardId);

        logger.info("Successfully found card: {}", card.getId());

        GetCardDto cardDTO = new GetCardDto(card);

        return ResponseEntity.status(HttpStatus.OK).body(cardDTO);
    }

    /**
     * Returns a json object of bad field found in the request
     *
     * @param exception The exception thrown by Spring when it detects invalid data
     * @return Map of field name that had the error and a message describing the error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors;
        errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
    /**
     * Handle delete request to /cards endpoint for deletion of card.
     * Request are validated for create fields by Spring, if bad then returns 400 with map of errors
     *
     * Returns:
     * 400 BAD_REQUEST If invalid id, id is not an integer
     * 401 UNAUTHORIZED If no user is logged on.
     * 403 FORBIDDEN If user does not own the card and is not a GAA
     * 406 NOT_ACCEPTABLE If the card already doesn't exist
     * 200 If successfully deleted card.
     * @param id The unique id of the card to be deleted.
     * @return Status code dependent on success. 400, 401, 403, 406 errors. 200 If deleted successfully.
     */
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Object> deleteCard(@PathVariable Integer id) {
        logger.info("Request to delete card id: {}", id);

        User user = userService.getCurrentlyLoggedInUser();

        Card card = cardService.findCardById(id);

        if (!card.getCreator().getId().equals(user.getId()) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot delete card. User: {} does not own this card and is not global admin", user);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this card");
        }
        logger.info("User: {} validated as owner of card or global admin.", user);

        cardService.deleteCard(card);
        return ResponseEntity.status(HttpStatus.OK).body("Card deleted successfully");
    }

    /**
     * Handle extend put request to /cards endpoint for extension of card expiry.
     * Request are validated for create fields by Spring, if bad then returns 400 with map of errors
     *
     * Returns:
     * 400 BAD_REQUEST If invalid id, id is not an integer
     * 401 UNAUTHORIZED If no user is logged on.
     * 403 FORBIDDEN If user does not own the card and is not a GAA
     * 406 NOT_ACCEPTABLE If the card doesn't exist
     * 200 If successfully extended card.
     * @param id The unique id of the card to be extended.
     * @return Status code dependent on success. 400, 401, 403, 406 errors. 200 If extended successfully.
     */
    @PutMapping("/cards/{id}/extenddisplayperiod")
    public ResponseEntity<Object> extendCard(@PathVariable Integer id) {
        logger.info("Request to extend card id: {}", id);

        User user = userService.getCurrentlyLoggedInUser();

        Card card = cardService.findCardById(id);

        if (!card.getCreator().getId().equals(user.getId()) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot extend card. User: {} does not own this card and is not global admin", user);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this card");
        }
        logger.info("User: {} validated as owner of card or global admin.", user);

        card.setDisplayPeriodEnd(LocalDateTime.now().plusSeconds(maxDisplayPeriodSeconds));
        logger.info("User: {} Extended card: {} by two weeks.", user, card);

        cardService.createCard(card);
        return ResponseEntity.status(HttpStatus.OK).body("End of display period successfully extended by two weeks");
    }

}
