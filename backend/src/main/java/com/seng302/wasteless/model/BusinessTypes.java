package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enum specifies types for businesses and provides functionality
 * for checking to see if a business is of a legal type
 */
public enum BusinessTypes {
    ACCOMMODATION_AND_FOOD_SERVICES("Accommodation and Food Services"),
    RETAIL_TRADE("Retail Trade"),
    CHARITABLE_ORGANISATION("Charitable organisation"),
    NON_PROFIT_ORGANISATION("Non-profit organisation")
    ;

    /**
     * The text of the enum element
     */
    private final String text;

    /**
     * Takes the text value and returns the enum heading
     * @param text The text value
     * @return the enum heading e.g RETAIL_TRADE
     */
    public static BusinessTypes fromString(String text) {
        for (BusinessTypes heading : BusinessTypes.values()) {
            if (heading.text.equalsIgnoreCase(text)) {
                return heading;
            }
        }
        return null;
    }


    /**
     * @param text  The text of the enum
     */
    BusinessTypes(final String text) {
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
