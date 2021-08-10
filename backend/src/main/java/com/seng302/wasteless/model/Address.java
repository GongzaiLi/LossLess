package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.BusinessViews;
import com.seng302.wasteless.view.ListingViews;
import com.seng302.wasteless.view.UserViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Type for Address object for users and buisnesses used by DTOs to return the correct (and correctly formatted) data.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Accessors(chain = true) //Allows chaining of getters and setters
public class Address {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @JsonIgnore
    private Integer id;

    @JsonView({UserViews.PostUserRequestView.class, BusinessViews.PostBusinessRequestView.class})
    @NotBlank(message = "street_number is mandatory")
    @Column(name = "street_number")
    @Size(min = 0, max = 50)
    private String streetNumber;

    @JsonView({UserViews.PostUserRequestView.class, BusinessViews.PostBusinessRequestView.class})
    @NotBlank(message = "street_name is mandatory")
    @Column(name = "street_name")
    @Size(min = 0, max = 50)
    private String streetName;

    @JsonView({UserViews.PostUserRequestView.class, BusinessViews.PostBusinessRequestView.class, BusinessViews.SearchBusinessesView.class, ListingViews.GetListingView.class})
    @NotBlank(message = "city is mandatory")
    @Column(name = "city")
    @Size(min = 0, max = 50)
    private String city;

    @JsonView({UserViews.PostUserRequestView.class, BusinessViews.PostBusinessRequestView.class, BusinessViews.SearchBusinessesView.class, ListingViews.GetListingView.class})
    @Column(name = "region")
    @Size(min = 0, max = 50)
    private String region;

    @JsonView({UserViews.PostUserRequestView.class, BusinessViews.PostBusinessRequestView.class, BusinessViews.SearchBusinessesView.class, ListingViews.GetListingView.class})
    @NotBlank(message = "country is mandatory")
    @Column(name = "country")
    @Size(min = 0, max = 50)
    private String country;

    @JsonView({UserViews.PostUserRequestView.class, BusinessViews.PostBusinessRequestView.class})
    @NotBlank(message = "postcode is mandatory")
    @Column(name = "postcode")
    @Size(min = 0, max = 50)
    private String postcode;

    @JsonView({UserViews.PostUserRequestView.class, BusinessViews.PostBusinessRequestView.class, BusinessViews.SearchBusinessesView.class, ListingViews.GetListingView.class})
    @Column(name = "suburb")
    private String suburb;

}