package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GetProductSortTypes {
    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    MANUFACTURER("manufacturer"),
    RRP("recommendedRetailPrice"),
    CREATED("created")
    ;

    /**
     * The text of the enum element
     */
    private final String text;

    /**
     * @param text  The text of the enum
     */
    GetProductSortTypes(final String text) {
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
