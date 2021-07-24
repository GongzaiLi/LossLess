package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.CardSections;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.validation.constraints.*;
import java.util.List;

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
    private String section;

    @Column(name = "title")
    @Size(min = 1, max = 25)
    @NotNull(message = "Title is mandatory")
    private String title;

    @Column(name = "description")
    @Size(max = 250)
    private String description;

    @ElementCollection
    @Column(name = "keywords")
    @NotNull(message = "Keyword is mandatory")
    @Size (min = 1, max = 5)
    private List<@NotBlank @NotNull @Size(max = 10) String> keywords;
}
