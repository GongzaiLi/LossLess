package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enum specifies types for cards and provides functionality
 * for checking to see if a card is of a legal type
 */
public enum CardTypes {
    FOR_SALE("For Sale"),
    WANTED("Wanted"),
    EXCHANGE("Exchange");

    /**
     * The text of the enum element
     */
    private final String text;

    /**
     * @param text  The text of the enum
     */
    CardTypes(final String text) {
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
