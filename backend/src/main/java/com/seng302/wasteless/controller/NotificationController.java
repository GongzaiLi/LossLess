package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.NotificationService;
import com.seng302.wasteless.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class NotificationController {

    private static final Logger logger = LogManager.getLogger(UserController.class.getName());

    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(UserService userService,
                          NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * Endpoint to DELETE a notification of the logged-in user.
     *
     * @return  200 OK if successful request or 406 if notification does nt belong to current user or
     * user not global admin.
     */
    @DeleteMapping("/users/notifications/{id}")
    public ResponseEntity<Object> deleteNotification(@PathVariable Integer id) {
        logger.info("Request to delete notification with id: {}", id);

        User user = userService.getCurrentlyLoggedInUser();
        Notification notification = notificationService.findNotificationById(id);

        if (!notification.getUserId().equals(user.getId()) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot delete notification {}. It does not belong to user {} and " +
                    "user is not global admin", id, user.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This notification does not belong to you.");
        }
        logger.info("Notification {} retrieved for user with id: {}", notification, user.getId());

        notificationService.deleteNotification(notification);
        return ResponseEntity.status(HttpStatus.OK).body("Notification deleted successfully");
    }

}
