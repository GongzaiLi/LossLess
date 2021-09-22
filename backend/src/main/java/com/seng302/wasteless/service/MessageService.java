package com.seng302.wasteless.service;

import com.seng302.wasteless.dto.GetMessageDto;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Message;
import com.seng302.wasteless.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MessageService applies message logic over the card JPA repository.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService (MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Create a message in the repository
     *
     * @param message   The message to be created
     * @return          The message once created
     */
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    /**
     * Check one of the users owns the given card.
     *
     * This method is in the message service as it only user by the message controller
     * Put here mostly for the purpose of being able to be mocked for testing.
     *
     * @param senderId      The id of the sender of the message
     * @param receiverId    The id of the receiver of the message
     * @param card          The card to check ownership of
     * @return              True if one of the users owns the card, false otherwise
     */
    public boolean checkOneUserOwnsCard(Integer senderId, Integer receiverId, Card card) {
        return card.getCreator().getId().equals(senderId) || card.getCreator().getId().equals(receiverId);
    }


    public GetMessageDto findAllMessagesForUserAboutCard(Integer userId, Card card) {

        List<Message> messages = messageRepository.findAllByUserIdAndCardId(userId, card.getId());

        return new GetMessageDto(card.getId(), userId, card.getCreator().getId(), messages);
    }
}

