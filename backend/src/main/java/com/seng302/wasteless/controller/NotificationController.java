package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.PatchNotificationStatusDTO;
import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationTag;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.*;
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

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
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

        notificationStatusDTO.applyToNotification(notification, tag);
        notificationService.saveNotification(notification);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
