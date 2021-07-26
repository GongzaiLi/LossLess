package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Inventory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

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
    private List<Inventory> inventory;

    private Integer totalItems;


}
