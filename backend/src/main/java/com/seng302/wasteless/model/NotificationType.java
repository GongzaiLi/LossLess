package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
    LIKEDLISTING("Liked Listing"),
    UNLIKEDLISTING("Unliked Listing"),
    EXPIRED("Expired Marketplace Card");

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

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    @JsonValue
    public String toString() {
        return text;
    }
}
