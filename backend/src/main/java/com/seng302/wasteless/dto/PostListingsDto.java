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
public class PostListingsDto {


    @NotNull(message = "Inventory item ID is Mandatory")
//    @NotBlank(message = "Inventory item ID is Mandatory")
    private Integer inventoryItemId;

    @Positive
    @Max(1000000000)            //max = inventoryItem.quantity()
    @Min(1)
    @NotNull(message = "Inventory item quantity is Mandatory")
    @Column(name = "quantity")
    private int quantity;

    @PositiveOrZero
    @Max(1000000000)
    @Column(name = "price")
    private double price;

    @Column(name = "moreInfo")
    private String moreInfo;

    @Future                     // default = inventory item expiry
    @Column(name = "closes")
    private LocalDate closes;
}
