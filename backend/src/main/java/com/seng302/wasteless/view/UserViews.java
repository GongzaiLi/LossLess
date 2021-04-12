package com.seng302.wasteless.view;

/**
 * Interfaces used with @JsonView annotation to allow for only specified
 * fields to be accepted/returned with request/responses.
 */
public class UserViews {
    public interface SearchUserView {}
    public interface PostUserRequestView {}
}
