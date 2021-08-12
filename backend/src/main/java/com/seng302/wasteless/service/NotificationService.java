package com.seng302.wasteless.service;


import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Notification;
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
    public Notification createNotification(Notification notification) {return notificationRepository.save(notification); }

}
