package com.seng302.wasteless.service;

import com.seng302.wasteless.dto.GetMessageDto;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Message;
import com.seng302.wasteless.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
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


    /**
     * Find all messages for a user for a given card. Given that the user does not own the card
     *
     * @param userId    The id of the user to get messages for
     * @param card      The id of the card to get messages for
     * @return          All messages relating to the user for that card.
     */
    public GetMessageDto findAllMessagesForUserOnCardTheyDontOwn(Integer userId, Card card) {

        List<Message> messages = messageRepository.findAllByUserIdAndCardId(userId, card.getId());

        return new GetMessageDto(card.getId(), userId, card.getCreator().getId(), messages);
    }


    /**
     * Find all conversations (groups of messages between two users relating to a card) between the owner of a card
     * and all users who have messaged/been messaged about this card.
     *
     * @param userId    The id of the user whom owns the card
     * @param card      The card to get messages for
     * @return          List of GetMessageDto. Each GetMessageDto contains the messages between 1 user and the card owner
     */
    public List<GetMessageDto> findAllMessagesForUserOnCardTheyDoOwn(Integer userId, Card card) {

        List<GetMessageDto> messageDtos = new ArrayList<>();

        List<Integer> receiverIds = messageRepository.findAllIdsOfUsersWhoHaveMessagedOwnerOfCard(card.getId());
        List<Integer> senderIds = messageRepository.findAllIdsOfUsersWhoHaveReceivedMessageFromOwnerOfCard(card.getId());

        HashSet<Integer> messagers = new HashSet<>();
        messagers.addAll(receiverIds);
        messagers.addAll(senderIds);
        messagers.remove(userId);

        for (Integer messagerId: messagers) {
            messageDtos.add(this.findAllMessagesForUserOnCardTheyDontOwn(messagerId, card));
        }

        return messageDtos;
    }
}

