package com.seng302.wasteless.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Data transfer object for UserSearch endpoint, used to return the correct data in the correct format,
 * returns list of users that have had an offset and count applied to the whole search and the total number of results
 * for the search.
 *A search enquiry is transformed into UserSearchDto via the UserSearchDtoMapper
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class UserSearchDto {
    private List<GetUserDto> results;
    private Long totalItems;
}
