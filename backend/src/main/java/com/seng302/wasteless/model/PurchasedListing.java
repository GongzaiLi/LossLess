package com.seng302.wasteless.model;



import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.PurchasedListingView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Accessors(chain = true) //Allows chaining of getters and setters
public class PurchasedListing {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "business")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private Business business;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "purchaser")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private User purchaser;


    @NotNull
    @JoinColumn(name = "sale_date")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private LocalDate saleDate;

    @PositiveOrZero
    @JoinColumn(name = "number_of_likes", columnDefinition = "integer default 0")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private Integer numberOfLikes;


    @NotNull
    @JoinColumn(name = "listing_date")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private LocalDate listingDate;

    @NotNull
    @JoinColumn(name = "closing_date")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private LocalDate closingDate;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "product")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private Product product;


    @NotNull
    @PositiveOrZero
    @Column(name = "quantity")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private Integer quantity;

    @NotNull
    @PositiveOrZero
    @Column(name = "price")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    private Double price;

    /**
     * Creates a new Purchased Listing record from a Listing that was purchased
     * and the User that purchased it.
     * Will also set the sale date to the current date.
     * @param listing The Listing that was purchased.
     * @param purchaser The User that purchased the listing.
     */
    public PurchasedListing(Listing listing, User purchaser) {
        setBusiness(listing.getBusiness());
        setPurchaser(purchaser);
        setSaleDate(LocalDate.now());
        setListingDate(listing.getCreated());
        setClosingDate(listing.getCloses());
        setProduct(listing.getInventoryItem().getProduct());
        setQuantity(listing.getQuantity());
        setPrice(listing.getPrice());
        setNumberOfLikes(listing.getUsersLiked());
    }
}

