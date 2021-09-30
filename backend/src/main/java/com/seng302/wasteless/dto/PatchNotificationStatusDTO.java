package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Notification;
import com.seng302.wasteless.model.NotificationTag;
import lombok.Data;
import lombok.ToString;

/**
 * Data transfer object for requests to modify a notification's state.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class PatchNotificationStatusDTO {
    /**
     * Would have liked to have used Optionals for this, but unfortunately Spring Boot
     * will leave Optionals as null and not EMPTY when we don't include the field in
     * the JSON of the request
     */
    private Boolean read;
    private Boolean starred;
    private Boolean archived;
    private String tag = "";

    /**
     * @param notification the notification being updated
     * @param newTag the new tag of the notification, can be null
     */
    public void applyToNotification(Notification notification, NotificationTag newTag) {
        if (read != null) {
            notification.setRead(read);
        }
        if (starred != null) {
            notification.setStarred(starred);
        }
        if (archived != null) {
            notification.setArchived(archived);
        }
        //This does not need a null check because the user needs to be able to remove the tag
        notification.setTag(newTag);
    }
}
