package com.seng302.wasteless.model;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;


/**
 * An implementation of Listing model. (A listing for the sale of some product from a business's inventory)
 * This class creates a Business JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Accessors(chain = true) //Allows chaining of getters and setters
public class Listing {


    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column
    private Integer businessId;

    @NotNull(message = "Inventory id is Mandatory")
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;


    @Positive
    @Max(1000000000)
    @NotNull(message = "Quantity is Mandatory")
    @Column(name = "quantity")
    private int quantity;

    @PositiveOrZero
    @Max(1000000000)
    @NotNull(message = "Price is Mandatory")
    @Column(name = "price")
    private double price;

    @Column(name = "moreInfo")
    private String moreInfo;

    @PastOrPresent
    @Column(name = "created")
    private  LocalDate created;


    @FutureOrPresent
    @Column(name = "closes")
    private LocalDate closes;


}
