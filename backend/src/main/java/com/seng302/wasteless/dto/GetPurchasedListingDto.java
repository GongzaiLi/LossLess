package com.seng302.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.PurchasedListing;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.view.PurchasedListingView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * The DTO used to return data representing a single purchasedListing object.
 * No DTO mapper is needed as the constructor takes care of everything.
 */

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@JsonView(PurchasedListingView.GetPurchasedListingView.class)
public class GetPurchasedListingDto {

    private Integer id;
    private Business business;
    private User purchaser;
    private LocalDate saleDate;
    private Integer numberOfLikes;
    private LocalDate listingDate;
    private LocalDate closingDate;
    private Product product;
    private Integer quantity;
    private Double price;

    /**
     * Constructor to convert a purchasedListing into a GetPurchasedListingDto
     *
     * @param purchasedListing The listing to use to create the purchasedListing
     */
    public GetPurchasedListingDto(PurchasedListing purchasedListing) {
        this.id = purchasedListing.getId();
        this.business = purchasedListing.getBusiness();
        this.purchaser = purchasedListing.getPurchaser();
        this.saleDate = purchasedListing.getSaleDate();
        this.numberOfLikes = purchasedListing.getNumberOfLikes();
        this.listingDate = purchasedListing.getListingDate();
        this.closingDate = purchasedListing.getClosingDate();
        this.product = purchasedListing.getProduct();
        this.quantity = purchasedListing.getQuantity();
        this.price = purchasedListing.getPrice();
    }
}
