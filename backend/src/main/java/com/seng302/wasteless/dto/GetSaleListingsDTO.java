package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Listing;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data transfer object for results from GET SearchListings endpoint.
 * Aids in pagination by containing the total number of results.
 */

@Data // generate setters and getters for all fields (lombok pre-processor)
public class GetSaleListingsDTO {

    private List<GetSaleListingDTO> results;
    private Long totalItems;

    /**
     * Creates GetCardsDto from a single page (list/slice) of Cards, and the total number of such cards.
     * @param pageOfListings A List representing a single page of Cards
     * @param totalItems The total number of such cards *across all pages*.
     */
    public GetSaleListingsDTO(List<Listing> pageOfListings, Long totalItems) {
        this.results = pageOfListings.stream().map(GetSaleListingDTO::new).collect(Collectors.toList());
        this.totalItems = totalItems;
    }
}
