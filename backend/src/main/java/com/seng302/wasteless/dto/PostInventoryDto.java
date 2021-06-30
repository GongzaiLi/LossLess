package com.seng302.wasteless.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Dto for post inventory endpoint.
 */

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class PostInventoryDto {

    @NotBlank(message = "Product Code is Mandatory")
    private String productId;

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
