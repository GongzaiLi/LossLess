package com.seng302.wasteless.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Type for BusinessAdministered of businesses, used by DTOs to return the correct (and correctly formatted) data.
 */

@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
@Accessors(chain = true) //Allows chaining of getters and setters
public class BusinessAdministered {
    private int id;
    private List<String> administrators;
    private int primaryAdministratorId;
    private String name;
    private String description;
    private String address;
    private BusinessTypes businessType;
    private String created;
}
