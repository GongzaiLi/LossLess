package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.model.User;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object for GetListing endpoint, used to return the correct data in the correct format.
 * Does not have a corresponding mapper. Make this class directly.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class GetListingsDto {

    private List<GetListingDto> listings;

    private Long totalItems;

    /**
     * Creates GetCardsDto from a single page (list/slice) of listings, and the total number of such listings.
     * @param listings A List representing a single page of listings
     * @param totalItems The total number of such listings *across all pages*.
     * @param user currently logged in user to check if they like listing
     */
    public GetListingsDto(List<Listing> listings, Long totalItems, User user) {
        List<GetListingDto> dtoListings = new ArrayList<>();
        for (Listing listing: listings) {
            GetListingDto dtoListing = new GetListingDto(listing, user.checkUserLikesListing(listing));
            dtoListings.add(dtoListing);
        }
        this.listings = dtoListings;
        this.totalItems = totalItems;
    }

}
