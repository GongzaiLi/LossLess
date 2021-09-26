package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


/**
 * MessageRepository is a repository interface for the message entity.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /**
     * Find all messages a user has sent or received for card. (Meant to be used when the user does not own the card)
     *
     * @param userId    The id of the user to get messages of
     * @param cardId    The id of card to get messages of
     * @return          The messages of a user for a card.
     */
    @Query(value = "select * from Message M where M.card_id = :cardId and (M.sender_id = :userId or M.receiver_id = :userId)", nativeQuery = true)
    List<Message> findAllByUserIdAndCardId(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    /**
     * find all ids of users who are the sender of messages for a card
     *
     * @param cardId    The id of the card to get messages for.
     * @return          all ids of users who are the sender of messages for a card
     */
    @Query(value = "select distinct M.sender_id from Message M where M.card_id = :cardId", nativeQuery = true)
    List<Integer> findAllIdsOfUsersWhoHaveMessagedOwnerOfCard(@Param("cardId") Integer cardId);

    /**
     * find all ids of users who are the receiver of messages for a card
     *
     * @param cardId    The id of the card to get messages for.
     * @return          all ids of users who are the receiver of messages for a card
     */
    @Query(value = "select distinct M.receiver_id from Message M where M.card_id = :cardId", nativeQuery = true)
    List<Integer> findAllIdsOfUsersWhoHaveReceivedMessageFromOwnerOfCard(@Param("cardId") Integer cardId);
}
