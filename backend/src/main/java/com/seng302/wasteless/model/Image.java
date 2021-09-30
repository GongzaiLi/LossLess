package com.seng302.wasteless.model;


import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.BusinessViews;
import com.seng302.wasteless.view.InventoryViews;
import com.seng302.wasteless.view.ListingViews;
import com.seng302.wasteless.view.MessageViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * An implementation of ProductImage model.
 * This class creates a ProductImage JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Accessors(chain = true) //Allows chaining of getters and setters
public class Image {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class, BusinessViews.SearchBusinessesView.class, MessageViews.GetMessageView.class})
    private Integer id;

    @NotNull(message = "Must have a filename")
    @Column(name = "fileName")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class, BusinessViews.SearchBusinessesView.class, MessageViews.GetMessageView.class})
    private String fileName;

    @NotNull(message = "Must have a thumbnail filename")
    @Column(name = "thumbnail_Filename")
    @JsonView({InventoryViews.GetInventoryView.class, ListingViews.GetListingView.class, BusinessViews.SearchBusinessesView.class, MessageViews.GetMessageView.class})
    private String thumbnailFilename;
}
