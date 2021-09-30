package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum NotificationType {
    LIKEDLISTING("Liked Listing"),
    UNLIKEDLISTING("Unliked Listing"),
    EXPIRED("Expired Marketplace Card"),
    PURCHASED_LISTING("Purchased listing"),
    LIKEDLISTING_PURCHASED("Liked Listing Purchased"),
    USER_CURRENCY_CHANGE("User Currency Changed"),
    BUSINESS_CURRENCY_CHANGE("Business Currency Changed"),
    MESSAGE_RECEIVED("Message Received")
    ;

    /**
     * The text string that will be returned
     */
    private final String text;

    /**
     * Constructs a new instance of the NotificationTypes enum with the given role name
     * @param text  String describing the type of notification
     */
    NotificationType(final String text) {
        this.text = text;
    }

    /**
     * Takes the text value and returns the enum heading
     * @param text The text value
     * @return the enum heading e.g PURCHASED_LISTING or if it is not a valid type then it throws a 400 Bad Request error.
     */
    public static NotificationType fromString(String text) {
        for (NotificationType heading : NotificationType.values()) {
            if (heading.text.equalsIgnoreCase(text)) {
                return heading;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is not a valid business type");
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    @JsonValue
    public String toString() {
        return text;
    }
}
