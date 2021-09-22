package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.LoginDto;
import com.seng302.wasteless.dto.PostMessageDto;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Message;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.MessageService;
import com.seng302.wasteless.service.UserService;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * MessageController is used for mapping all Restful API requests starting with the address "/messages".
 */
@RestController
public class MessageController {
    private static final Logger logger = LogManager.getLogger(com.seng302.wasteless.controller.MessageController.class.getName());

    private final UserService userService;
    private final CardService cardService;
    private final MessageService messageService;

    @Autowired
    public MessageController (UserService userService,
                              CardService cardService,
                              MessageService messageService) {
        this.userService = userService;
        this.cardService = cardService;
        this.messageService = messageService;
    }

    /**
     * Create a message between two users, associated with a card.
     *
     * At least one of the users must own the card.
     * Cannot send message to self.
     *
     * Sender is taken from the currently logged in user.
     *
     * Returns:
     * 201 If message successfully created
     * 401 If user is unauthorised
     * 400 If card or receiver do not exist
     *
     * @param messageDTO    DTO specifying fields of the message
     * @return  201 Created and message ID, 401 Unauthorised, 400 Bad request.
     */
    @PostMapping("/messages")
    public ResponseEntity<Object> createMessage(@Validated @RequestBody PostMessageDto messageDTO) {

        User currentlyLoggedInUser = userService.getCurrentlyLoggedInUser();

        if (currentlyLoggedInUser.getId().equals(messageDTO.getReceiverId())) {
            logger.debug("Cannot save message, user with id {} tried to send message to themself", currentlyLoggedInUser.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot send message to yourself.");
        }

        Card cardForMessage = cardService.findCardById(messageDTO.getCardId());

        User receiver = userService.findUserById(messageDTO.getReceiverId());

        if (!messageService.checkOneUserOwnsCard(currentlyLoggedInUser.getId(), receiver.getId(), cardForMessage)) {
            logger.debug("Cannot save message, neither sender with id {} or receiver with id {} owns card with id {}",
                    currentlyLoggedInUser.getId(), messageDTO.getReceiverId(), cardForMessage.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Neither sender nor receiver owns this card.");
        }

        Message message = new Message(cardForMessage.getId(), currentlyLoggedInUser.getId(), receiver.getId(), messageDTO.getMessageText(), LocalDateTime.now());

        message = messageService.createMessage(message);

        JSONObject responseBody = new JSONObject();
        responseBody.put("messageId", message.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
}
