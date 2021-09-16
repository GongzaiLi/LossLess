package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Data transfer object for manufacturersPurchasedTotals endpoint, used to return the correct data in the correct format.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class SalesReportManufacturerTotalsDto {
    private String manufacturer;
    private Integer totalProductPurchases;
    private Double totalValue;
    private Integer totalLikes;

    public SalesReportManufacturerTotalsDto(String manufacturer, Integer totalProductPurchases, Double totalValue, Integer totalLikes) {
        setManufacturer(manufacturer);
        setTotalProductPurchases(totalProductPurchases);
        setTotalValue(totalValue);
        setTotalLikes(totalLikes);
    }
}
