package com.seng302.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.view.ListingViews;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Data transfer object for GetListing endpoint, used to return the correct data in the correct format.
 * Does not have a corresponding mapper. Make this class directly.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class GetListingDto {

    @JsonView(ListingViews.GetListingView.class)
    private List<Listing> listings;

    @JsonView(ListingViews.GetListingView.class)
    private Long totalItems;
}
