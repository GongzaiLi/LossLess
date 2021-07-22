package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enum specifies types for sorting the user search
 */
public enum UserSearchSortTypes {
    NAME("firstName"),
    NICKNAME("nickname"),
    EMAIL("email"),
    ROLE("role"),
    NONE("id"),
    ;

    /**
     * The text of the enum element
     */
    private final String text;

    /**
     * @param text  The text of the enum
     */
    UserSearchSortTypes(final String text) {
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
