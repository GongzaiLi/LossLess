package com.seng302.wasteless.service;


import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.NotificationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationService.class.getName());

    private final NotificationRepository notificationRepository;


    @Autowired
    public NotificationService(NotificationRepository notificationRepository) { this.notificationRepository = notificationRepository; }

    /**
     * Get notification with given ID
     * @param notificationId The id of the user to get notifications for
     * @return The found notification
     * @throws ResponseStatusException 404 if no notification exists with the given ID
     */
    public Notification findNotificationById(Integer notificationId) {
        Optional<Notification> possibleNotification = notificationRepository.findById(notificationId);
        if (possibleNotification.isEmpty()) {
            logger.warn("No notification exists with the ID: {}", notificationId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No notification exists with the given ID");
        } else {
            return possibleNotification.get();
        }
    }

    /**
     * todo findAllUnArchivedNotificationsByUserId
     * Get notifications for user that have not been archived
     * @param userId        The id of the user to get notifications for
     * @return          The found notifications, if any otherwise empty list
     */
    public static Specification<Notification> filterAllUnArchivedNotificationsByUserId(Integer userId) {
        //ByStarredDescCreatedDesc
        Specification<Notification> findUser = (root, query, builder) -> builder.equal(root.get("userId"), userId);
        return findUser.and((root, query, builder) -> builder.isFalse(root.get("archived")));
    }

    /**
     * todo
     * @return
     */
    private Specification<Notification> filterNotificationTags(List<String> tags) {
        List<NotificationTag> notificationTags = new ArrayList<>();
        for (String tag : tags) {
            if (!tag.isEmpty()) {
                notificationTags.add(NotificationTag.valueOf(tag.toUpperCase()));
            }
        }
        return (root, query, builder) -> root.get("tag").in(notificationTags);
    }


    /**
     * todo
     * @param userId
     * @param tags
     * @return
     */
    public List<Notification> filterNotifications(Integer userId, Optional<List<String>> tags) {
        Specification<Notification> querySpec = filterAllUnArchivedNotificationsByUserId(userId);
        if (tags.isPresent()) querySpec = querySpec.and(filterNotificationTags(tags.get()));
        return notificationRepository.findAll(querySpec, Sort.by("starred").descending().and(Sort.by("created").descending()));
    }

    /**
     * Creates a Notification by saving the notification object and persisting it in the database
     * @param notification The Notification object to be created.
     * @return The created Notification object.
     */
    public Notification saveNotification(Notification notification) {return notificationRepository.save(notification); }

    /**
     * Creates a notification object from the available inputs and returns the object. This object still needs to be saved
     * using saveNotification() to save the object to the database
     * @param userId Integer Id of the user this notification is for. Can not be Null
     * @param subjectId The Integer id of the subject the notification is created for if applicable. Can be null
     * @param type String detailing the type of notification being created. Can not be Null
     * @param message String with the contents of the message of the notification. Can be null
     * @return Returns the created Notification object.
     */
    public static Notification createNotification(Integer userId, Integer subjectId, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setSubjectId(subjectId);
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setCreated(LocalDateTime.now());
        return notification;
    }

    /**
     * Send a notification made from the parameters to every user in the usersToNotify list
     * This uses a batched update to run faster.
     *
     * @param usersToNotify List of ids of users to send the notification to
     * @param subjectId The Integer id of the subject the notification is created for if applicable. Can be null
     * @param type String detailing the type of notification being created. Can not be Null
     * @param message String with the contents of the message of the notification. Can be null
     */
    public void notifyAllUsers(List<Integer> usersToNotify, Integer subjectId, NotificationType type, String message) {
        List<Notification> notifications = usersToNotify.stream().map(
                userId -> createNotification(userId, subjectId, type, message)
        ).collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }


    /**
     * Deletes a Notification object and persists the action in the database
     * @param notification The Notification object to be deleted.
     */
    public void deleteNotification(Notification notification) {notificationRepository.delete(notification); }

}
