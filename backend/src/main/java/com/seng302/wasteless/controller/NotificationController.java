package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.PatchNotificationStatusDTO;
import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationTag;
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

        Notification notification = notificationService.findNotificationById(id);

        String logString = "Cannot delete notification " + notification.getId();
        validateUser(notification, logString);

        notificationService.deleteNotification(notification);
        return ResponseEntity.status(HttpStatus.OK).body("Notification deleted successfully");
    }


    /**
     * Handles PATCH requests to change a notification's state, i.e. whether it is read, starred, and/or archived.
     * if tag is null set tag to null if tag is not sent set tag to current value otherwise update tag to new value
     * @param notificationId Id of the notification to be patched
     * @param notificationStatusDTO Valid DTO containing data on how the notification will be patched
     * @return
     * 200 OK if notification was patched successfully
     * 400 Bad Request invalid tag type
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
        NotificationTag tag;
        try {
            if (notificationStatusDTO.getTag() == null) {
                tag = null;
            } else if (notificationStatusDTO.getTag().equals("")) {
                tag = notification.getTag();
            } else{
                tag = NotificationTag.valueOf(notificationStatusDTO.getTag().toUpperCase());
            }
        }catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The tag specified is not valid");
        }

        String logString = "Cannot update notification " + notification.getId();
        validateUser(notification, logString);

        notificationStatusDTO.applyToNotification(notification, tag);
        notificationService.saveNotification(notification);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Checks if a notification belongs to a user.
     * @param notification The notification to be checked
     * @param logString The appropriate message to be logged.
     */
    public void validateUser(Notification notification, String logString) {
        User user = userService.getCurrentlyLoggedInUser();
        if (!notification.getUserId().equals(user.getId()) && !user.checkUserGlobalAdmin()) {
            logger.warn("{}. This notification does not belong to user with id: {} and user is not an admin.", logString, user.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This notification does not belong to you.");
        }
        logger.info("Notification {} retrieved for user with id: {}", notification, user.getId());
    }
}
