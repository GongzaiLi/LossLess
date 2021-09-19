package com.seng302.wasteless.repository;
import com.seng302.wasteless.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * MessageRepository is a repository interface for the message entity.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface MessageRepository extends JpaRepository<Message, Integer> {

}
