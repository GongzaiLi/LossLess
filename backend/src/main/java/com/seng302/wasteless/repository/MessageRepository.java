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

    @Query(value = "select * from Message M where M.card_id = :cardId and (M.sender_id = :userId or M.receiver_id = :userId)", nativeQuery = true)
    List<Message> findAllByUserIdAndCardId(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

}
