package com.seng302.wasteless.User;

import com.fasterxml.jackson.annotation.JsonValue;

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
