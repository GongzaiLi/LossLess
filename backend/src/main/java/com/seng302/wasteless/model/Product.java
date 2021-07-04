package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.InventoryViews;
import com.seng302.wasteless.view.ListingViews;
import com.seng302.wasteless.view.ProductViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Product model.
 * This class creates a Product JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "database_id")
    private Long databaseId;

    @JsonView({ProductViews.PostProductRequestView.class, InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    @Column(name = "code", unique = true)
    private String id;

    @JsonView({ProductViews.PostProductRequestView.class, InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    @Column(name = "name")
    @NotBlank(message = "product name is mandatory")
    private String name;

    @JsonView({ProductViews.PostProductRequestView.class, InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    @Column(name = "description")
    private String description;

    @JsonView({ProductViews.PostProductRequestView.class, InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    @Column(name = "manufacturer")
    private String manufacturer;

    @PositiveOrZero
    @JsonView({ProductViews.PostProductRequestView.class, InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    @Column(name = "recommended_retail_price")
    private Double recommendedRetailPrice;

    @JsonView({ProductViews.PostProductRequestView.class, InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class})
    @Column(name = "created")
    private LocalDate created;

    @Column(name = "business_id")
    private Integer businessId;

    @ElementCollection
    @Column(name = "image_ids")
    private List<Integer> imageIds;


    /**
     * Formats a code by taking the BusinessId and the Product Object's ID
     * setting it to uppercase and replaces spaces with "-" and replaces
     * and removes alphanumeric
     *
     * @param businessId Id of business product belongs to
     * @return String that is formatted as the Product Code
     */
    public String createCode(Integer businessId) {
        return businessId + "-" + this.getId().toUpperCase().replaceAll("\\P{Alnum}+$", "")
                .replaceAll(" ", "-");
    }

    /**
     * Add an image to a product
     * Never call this directly, only call it from product service.
     *
     * @param productImageId Id of the image to add to the product
     */
    public void addImage(Integer productImageId) {
        this.imageIds.add(productImageId);
    }

    /**
     * Remove an image from a product
     * Never call this directly, only call it from product service.
     *
     * @param productImageId Id of the image to remove from the product
     */
    public void removeImage(Integer productImageId) {
        this.imageIds.remove(productImageId);
    }

}
