package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Listing;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.time.LocalDate;

/**
 * The DTO used to return data representing a single listing object.
 * Not DTO mapper is needed as the constructor takes care of everything.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class GetListingDto {
    private Integer id;
    private Inventory inventoryItem;
    private int quantity;
    private double price;
    private String moreInfo;
    private LocalDate created;
    private LocalDate closes;
    private Integer usersLiked;
    private Boolean currentUserLikes;

    /**
     * Creates a new DTO from a Listing entity
     * @param listing The Listing entity to create this DTO from
     * @param user logged in user, so can add if that user likes this listing
     */
    public GetListingDto(Listing listing) {
        setId(listing.getId());
        setInventoryItem(listing.getInventoryItem());
        setQuantity(listing.getQuantity());
        setPrice(listing.getPrice());
        setMoreInfo(listing.getMoreInfo());
        setCreated(listing.getCreated());
        setCloses(listing.getCloses());
        setUsersLiked(listing.getUsersLiked());
        setCurrentUserLikes(Boolean.FALSE);
    }

    public GetListingDto(Listing listing, Boolean currentUserLikes) {
        setId(listing.getId());
        setInventoryItem(listing.getInventoryItem());
        setQuantity(listing.getQuantity());
        setPrice(listing.getPrice());
        setMoreInfo(listing.getMoreInfo());
        setCreated(listing.getCreated());
        setCloses(listing.getCloses());
        setUsersLiked(listing.getUsersLiked());
        setCurrentUserLikes(currentUserLikes);
    }
}
