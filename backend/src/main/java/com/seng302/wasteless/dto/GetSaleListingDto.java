package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.Inventory;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;


/**
 * The DTO used to return data representing a single searched for sale listings object. Used by GET searchListings endpoint.
 */


@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class GetSaleListingDto {

    private Integer id;
    private Inventory inventoryItem;
    private int quantity;
    private double price;
    private String moreInfo;
    private LocalDate created;
    private LocalDate closes;

    private String businessName;
    private Address businessAddress;


}