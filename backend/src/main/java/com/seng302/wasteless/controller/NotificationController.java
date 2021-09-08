package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.PatchNotificationStatusDTO;
import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

/**
 * NotificationController is used for mapping all Restful API requests involving specific notifications.
 * All paths start with the address "/notifications/{id}".
 */
@RestController
public class NotificationController {
    private final UserService userService;
    private final NotificationService notificationService;

    private static final Logger logger = LogManager.getLogger(NotificationController.class.getName());


    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * Endpoint to DELETE a notification of the logged-in user.
     *
     * @return  200 OK if successful request or 403 if notification does not belong to current user or
     * user not global admin.
     */
    @DeleteMapping("/notifications/{id}")
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


    /**
     * Handles PATCH requests to change a notification's state, i.e. whether it is read, starred, and/or archived.
     * @param notificationId Id of the notification to be patched
     * @param notificationStatusDTO Valid DTO containing data on how the notification will be patched
     * @return
     * 200 OK if notification was patched successfully
     * 403 Forbidden if the logged in user does not own the notification and is not an admin
     * 404 Not Found if notification with given id does not exist
     */
    @PatchMapping("/notifications/{id}")
    public ResponseEntity<Object> patchNotificationStatus(@PathVariable("id") Integer notificationId, @Valid @RequestBody PatchNotificationStatusDTO notificationStatusDTO) {
        Notification notification = notificationService.findNotificationById(notificationId);

        User loggedInUser = userService.getCurrentlyLoggedInUser();
        if (!notification.getUserId().equals(loggedInUser.getId()) && !loggedInUser.checkUserGlobalAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the user that this notification belongs to, and you are not an admin.");
        }

        notificationStatusDTO.applyToNotification(notification);
        notificationService.saveNotification(notification);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
