package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetMessageDto;
import com.seng302.wasteless.dto.PostMessageDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.CardService;
import com.seng302.wasteless.service.MessageService;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.MessageViews;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MessageController is used for mapping all Restful API requests starting with the address "/messages".
 */
@RestController
public class MessageController {
    private static final Logger logger = LogManager.getLogger(com.seng302.wasteless.controller.MessageController.class.getName());

    private final UserService userService;
    private final CardService cardService;
    private final MessageService messageService;
    private final NotificationService notificationService;

    @Autowired
    public MessageController (UserService userService,
                              CardService cardService,
                              MessageService messageService,
                              NotificationService notificationService) {
        this.userService = userService;
        this.cardService = cardService;
        this.messageService = messageService;
        this.notificationService = notificationService;
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

        Notification notification =  NotificationService.createNotification(receiver.getId(),message.getId(), NotificationType.MESSAGE_RECEIVED,String.format("You have received a new message from %s %s about the marketplace item: %s.", currentlyLoggedInUser.getFirstName(),currentlyLoggedInUser.getLastName(), cardForMessage.getTitle()));
        notificationService.saveNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }


    /**
     * Get all messages for a user relating to a card.
     *
     * returns:
     *  200 and Either one GetMessageDto representing a conversation between the logged in user and the owner of a card, or
     *  a list of GetMessageDto representing a all conversations between the logged in user (who owns the card) and all
     *  users who have messaged the card.
     *  401 Unauthorised: Currently logged in user does not exist.
     *  406 Not Acceptable: if card does not exist.
     *
     * @param cardId    The id of the card to get messages for
     * @return          See above.
     */
    @JsonView({MessageViews.GetMessageView.class})
    @GetMapping("/messages/{cardId}")
    public ResponseEntity<Object> getMessage(@PathVariable("cardId") Integer cardId) {
        User currentlyLoggedInUser = userService.getCurrentlyLoggedInUser();

        Card cardForMessage = cardService.findCardById(cardId);

        if (cardForMessage.getCreator().getId().equals(currentlyLoggedInUser.getId())) { //Card owner

            List<GetMessageDto> messagesDtos = messageService.findAllMessagesForUserOnCardTheyDoOwn(currentlyLoggedInUser.getId(), cardForMessage);
            return ResponseEntity.status(HttpStatus.OK).body(messagesDtos);
        } else {    //Not card owner
            GetMessageDto messageDto = messageService.findAllMessagesForUserOnCardTheyDontOwn(currentlyLoggedInUser, cardForMessage);
            return ResponseEntity.status(HttpStatus.OK).body(messageDto);
        }


    }
}
