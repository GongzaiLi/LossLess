package com.seng302.wasteless.service;


import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;


    @Autowired
    public NotificationService(NotificationRepository notificationRepository) { this.notificationRepository = notificationRepository; }
    /**
     * get notifications for user
     * @param userId        The id of the user to get notifications for
     * @return          The found notifications, if any otherwise empty list
     */
    public List<Notification> findAllNotificationsByUserId(Integer userId) {
        return  notificationRepository.findAllNotificationsByUserId_OrderByCreatedDesc(userId);
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
    public Notification createNotification(Integer userId, Integer subjectId, NotificationType type, String message) {
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
     * Get a notification by id for the logged-in user
     * @param id        The id of the notifications to be retrieved
     * @return          The found notification
     */
    public Notification findNotificationById(Integer id) {
        return notificationRepository.findFirstById(id);
    }

    /**
     * Deletes a Notification object and persists the action in the database
     * @param notification The Notification object to be created.
     */
    public void deleteNotification(Notification notification) {notificationRepository.delete(notification); }

}
