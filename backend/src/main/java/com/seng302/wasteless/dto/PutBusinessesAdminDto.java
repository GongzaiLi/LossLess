package com.seng302.wasteless.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Data transfer object for businesses makeAdministrator and businesses revokeAdministrator endpoint,
 * used to return the correct data in the correct format,
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class PutBusinessesAdminDto {
    private Integer userId;
}
