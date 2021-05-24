package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.BusinessTypes;
import com.seng302.wasteless.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;


/**
 * Data transfer object for GetInventory endpoint, used to return the correct data in the correct format.
 * Inventory entities are transformed into GetInventoryDto via the GetInventoryDtoMapper
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class GetInventoryDto {
    private Integer id;
    private Product product;
    private String description;
    private Integer quantity;
    private Double pricePerItem;
    private Double totalPrice;
    private LocalDate manufactured;
    private LocalDate sellBy;
    private LocalDate bestBefore;
    private LocalDate expires;


}
