package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    /**
     * Returns all cards that belong to the current user.
     * @param userId The id of the current user.
     * @return A (possibly empty) list of all notifications that belong to the current user.
     */
    List<Notification> findAllNotificationsByUserId_OrderByCreatedDesc(Integer userId);
}
