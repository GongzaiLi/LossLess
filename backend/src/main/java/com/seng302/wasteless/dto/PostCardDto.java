package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.CardSections;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.validation.constraints.*;

/**
 * Dto for post card endpoint.
 */

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class PostCardDto {

    @Column(name = "section")
    @NotNull(message = "section is mandatory")
    private CardSections section;

    @Column(name = "title")
    @NotNull(message = "Title is mandatory")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "keywords")
    @NotNull(message = "Keyword is mandatory")
    private String keywords;
}
