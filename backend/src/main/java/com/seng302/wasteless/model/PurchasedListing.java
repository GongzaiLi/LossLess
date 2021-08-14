package com.seng302.wasteless.model;



import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    private Business business;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user")
    private User purchaser;


    @NotNull
    @JoinColumn(name = "sale_date")
    private LocalDate saleDate;


    @NotNull
    @JoinColumn(name = "listing_date")
    private LocalDate listingDate;

    @NotNull
    @JoinColumn(name = "closing_date")
    private LocalDate closingDate;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;


    @NotNull
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull
    @Column(name = "price")
    private Double price;

}

