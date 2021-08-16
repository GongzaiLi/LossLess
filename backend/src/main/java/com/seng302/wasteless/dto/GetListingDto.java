package com.seng302.wasteless.dto;

import com.seng302.wasteless.dto.mapper.GetBusinessesDtoMapper;
import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Listing;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.time.LocalDate;

/**
 * The DTO used to return data representing a single listing object.
 * No DTO mapper is needed as the constructor takes care of everything.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class GetListingDto {
    private Integer id;
    private GetBusinessesDto business;
    private Inventory inventoryItem;
    private Integer quantity;
    private Double price;
    private String moreInfo;
    private LocalDate created;
    private LocalDate closes;
    private Integer usersLiked;
    private Boolean currentUserLikes;

    /**
     * Creates a new DTO from a Listing entity
     * @param listing The Listing entity to create this DTO from
     * @param currentUserLikes boolean for if current user likes this listing
     */
    public GetListingDto(Listing listing, Boolean currentUserLikes) {
        setId(listing.getId());
        setBusiness(GetBusinessesDtoMapper.toGetBusinessesDto(listing.getBusiness()));
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
