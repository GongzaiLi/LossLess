package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enum specifies types for sorting the get listings results
 */
public enum GetListingsSortTypes {
    NAME("name"),
    PRICE("price"),
    CREATED("created"),
    CLOSES("closes"),
    ;

    /**
     * The text of the enum element
     */
    private final String text;

    /**
     * @param text  The text of the enum
     */
    GetListingsSortTypes(final String text) {
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

