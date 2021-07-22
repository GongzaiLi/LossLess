package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enum specifies types for cards and provides functionality
 * for checking to see if a card is of a legal type
 */
public enum CardSections {
    FOR_SALE("ForSale"),
    WANTED("Wanted"),
    EXCHANGE("Exchange");

    /**
     * The text of the enum element
     */
    private final String text;

    /**
     * @param text  The text of the enum
     */
    CardSections(final String text) {
        this.text = text;
    }

    /**
     * Takes the text value and returns the enum heading
     * @param text The text value
     * @return the enum heading e.g FOR_SALE
     */
    public static CardSections fromString(String text) {
        for (CardSections heading : CardSections.values()) {
            if (heading.text.equalsIgnoreCase(text)) {
                return heading;
            }
        }
        return null;
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
