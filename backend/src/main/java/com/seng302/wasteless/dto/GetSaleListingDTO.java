package com.seng302.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.mapper.GetUserDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.view.ListingViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The DTO used to return data representing a single searched for sale listings object. Used by GET searchListings endpoint.
 * No DTO mapper is needed as the constructor takes care of everything.
 */

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class GetSaleListingDTO {

    private Integer id;
    private GetInventoryDto inventoryItem;
    private int quantity;
    private double price;
    private String moreInfo;
    private LocalDate created;
    private LocalDate closes;

    private String businessName;
    private Address businessAddress;

    /**
     * Creates a new DTO from a Listing entity
     * @param listing The Listing entity to create this DTO from
     */
    public GetSaleListingDTO(Listing listing) {

    }

}
