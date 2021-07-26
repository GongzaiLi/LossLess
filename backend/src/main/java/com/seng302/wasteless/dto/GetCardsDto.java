package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Card;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data transfer object for results from GET cards endpoint.
 * Aids in pagination by containing the total number of results.
 */

@Data // generate setters and getters for all fields (lombok pre-processor)
public class GetCardsDto {
    private List<GetCardDto> results;
    private Long totalItems;

    /**
     * Creates GetCardsDto from a single page (list/slice) of Cards, and the total number of such cards.
     * @param pageOfCards A List representing a single page of Cards
     * @param totalItems The total number of such cards *across all pages*.
     */
    public GetCardsDto(List<Card> pageOfCards, Long totalItems) {
        this.results = pageOfCards.stream().map(GetCardDto::new).collect(Collectors.toList());
        this.totalItems = totalItems;
    }
}
