package com.seng302.wasteless.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class Inventory {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private String id;

    @NotNull(message = "Product Code is Mandatory")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @Positive
    @Max(1000000000)
    @NotNull(message = "Product quantity is Mandatory")
    @Column(name = "quantity")
    private int quantity;

    @PositiveOrZero
    @Max(1000000000)
    @Column(name = "price_per_item")
    private double pricePerItem;

    @PositiveOrZero
    @Max(1000000000)
    @Column(name = "total_price")
    private double totalPrice;

    @PastOrPresent
    @Column(name = "manufactured")
    private LocalDate manufactured;

    @FutureOrPresent
    @Column(name = "sell_by")
    private LocalDate sellBy;

    @FutureOrPresent
    @Column(name = "best_before")
    private LocalDate bestBefore;

    @FutureOrPresent
    @NotNull(message = "Expiry Date is Mandatory")
    @Column(name = "expires")
    private LocalDate expires;

}
