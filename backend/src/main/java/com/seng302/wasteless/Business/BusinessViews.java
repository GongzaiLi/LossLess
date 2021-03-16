package com.seng302.wasteless.Business;

/**
 * Interfaces used with @JsonView annotation to allow for only specified
 * fields to be accepted/returned with request/responses.
 */
public class BusinessViews {
    public interface PostBusinessRequestView{};
    public interface GetBusinessView{};
}
