package com.seng302.wasteless.dto;

import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * The DTO used to return data representing a single Card object. Used by GET card/cards endpoints.
 * Not DTO mapper is needed as the constructor takes care of everything.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class GetCardDto {
    private Integer id;
    private GetUserDto creator;
    private CardSections section;
    private String title;
    private String description;
    private List<String> keywords;
    private LocalDate created;

    /**
     * Creates a new DTO from a Card entity
     * @param card The Card entity to create this DTO from
     */
    public GetCardDto(Card card) {
        setId(card.getId());
        setCreator(GetUserDtoMapper.toGetUserDto(card.getCreator()));
        setSection(card.getSection());
        setTitle(card.getTitle());
        setDescription(card.getDescription());
        setKeywords(card.getKeywords());
        setCreated(card.getCreated());
    }
}
