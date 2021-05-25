package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.InventoryViews;
import com.seng302.wasteless.view.ListingViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * An implementation of Inventory model.
 * This class creates a Inventory JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Accessors(chain = true) //Allows chaining of getters and setters
public class Inventory {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @NotNull
    @Column
    private Integer businessId;

    @NotNull(message = "Product Code is Mandatory")
    @ManyToOne
    @JoinColumn(name = "product_id") //Stored in table as id with reference to product table
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    private Product product;

    @PositiveOrZero
    @Max(1000000000)
    @NotNull(message = "Product quantity is Mandatory")
    @Column(name = "quantity")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    private int quantity;

    @PositiveOrZero
    @Max(1000000000)
    @Column(name = "price_per_item")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    private double pricePerItem;

    @PositiveOrZero
    @Max(1000000000)
    @Column(name = "total_price")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    private double totalPrice;

    @PastOrPresent
    @Column(name = "manufactured")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    private LocalDate manufactured;

    @FutureOrPresent
    @Column(name = "sell_by")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    private LocalDate sellBy;

    @FutureOrPresent
    @Column(name = "best_before")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    private LocalDate bestBefore;

    @FutureOrPresent
    @NotNull(message = "Expiry Date is Mandatory")
    @Column(name = "expires")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    private LocalDate expires;

}
