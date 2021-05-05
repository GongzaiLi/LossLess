package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enum specifies types for users and provides functionality
 * for checking to see if a user is of a legal type
 */
public enum UserRoles {
    USER("user"),
    GLOBAL_APPLICATION_ADMIN("globalApplicationAdmin"),
    DEFAULT_GLOBAL_APPLICATION_ADMIN("defaultGlobalApplicationAdmin")
    ;

    /**
     * The name of the user role as specified in the API spec (eg. 'user', 'globalApplicationAdmin')
     */
    private final String text;

    /**
     * Constructs a new instance of the UserRoles enum with the given role name
     * @param text  The name of the user role as specified in the API spec
     */
    UserRoles(final String text) {
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
