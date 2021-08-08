package com.seng302.wasteless.dto;

import com.seng302.wasteless.dto.mapper.GetSaleListingDtoMapper;
import com.seng302.wasteless.model.Listing;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data transfer object for results from GET SearchListings endpoint.
 * Aids in pagination by containing the total number of results.
 */

@Data // generate setters and getters for all fields (lombok pre-processor)
public class GetSaleListingsDto {

    private List<GetSaleListingDto> results;
    private Long totalItems;

    /**
     * Creates a GetSaleListingDTO from a single page (list/slice) of Listings, and the total number of such listings.
     *
     * @param pageOfListings A List representing a single page of Listings
     * @param totalItems The total number of such listings *across all pages*.
     */
    public GetSaleListingsDto(List<Listing> pageOfListings, Long totalItems) {
        this.results = pageOfListings.stream().map(GetSaleListingDtoMapper::toGetSaleListingDto).collect(Collectors.toList());
        this.totalItems = totalItems;
    }
}
