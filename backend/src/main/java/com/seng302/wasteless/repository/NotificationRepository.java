package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    /**
     * Returns all cards that belong to the current user and have the given archived state.
     * @param userId The id of the current user.
     * @param archived Whether archived or non-archived notifications should be retrieved
     * @return A (possibly empty) list of all notifications that belong to the current user.
     */
    List<Notification> findByUserIdAndArchivedOrderByCreatedDesc(Integer userId, Boolean archived);


    /**
     * Returns a notification with the given id for the logged-in user.
     * @param id The id of the notification,
     * @return The notification that belongs to the current user if there exists one.
     */
    Notification findFirstById(Integer id);

}
