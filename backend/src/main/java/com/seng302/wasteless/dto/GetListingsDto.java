package com.seng302.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.view.ListingViews;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data transfer object for GetListing endpoint, used to return the correct data in the correct format.
 * Does not have a corresponding mapper. Make this class directly.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class GetListingsDto {

    @JsonView(ListingViews.GetListingView.class)
    private List<GetListingDto> listings;

    @JsonView(ListingViews.GetListingView.class)
    private Long totalItems;

//    /**
//     * Creates GetCardsDto from a single page (list/slice) of listings, and the total number of such listings.
//     * @param listings A List representing a single page of listings
//     * @param totalItems The total number of such listings *across all pages*.
//     */
//    public GetListingsDto(List<Listing> listings, Long totalItems) {
//        this.listings = listings.stream().map(GetListingDto::new).collect(Collectors.toList());
//        this.totalItems = totalItems;
//    }

}
