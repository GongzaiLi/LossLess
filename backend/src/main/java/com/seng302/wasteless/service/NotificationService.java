package com.seng302.wasteless.service;


import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationType;
import com.seng302.wasteless.repository.NotificationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private static final Logger logger = LogManager.getLogger(com.seng302.wasteless.service.NotificationService.class.getName());

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) { this.notificationRepository = notificationRepository; }
    /**
     * get notifications for user
     * @param userId        The id of the user to get notifications for
     * @return          The found notifications, if any otherwise empty list
     */
    public List<Notification> findAllNotificationsByUserId(Integer userId) {
        return  notificationRepository.findAllNotificationsByUserId(userId);
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
        return notification;
    }

}
