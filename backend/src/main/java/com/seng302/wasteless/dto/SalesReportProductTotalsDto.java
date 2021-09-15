package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Data transfer object for ProductPurchaseTotals endpoint, used to return the correct data in the correct format.
 * This dto is intended to be created Per Purchase.
 * The object includes the total purchase for a product business during a period with their total value and total likes.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class SalesReportProductTotalsDto {
    private Product product;
    private Integer totalProductPurchases;
    private Double totalValue;
    private Integer totalLikes;

    public SalesReportProductTotalsDto(Product product, Integer totalProductPurchases, Double totalValue, Integer totalLikes) {
        setProduct(product);
        setTotalProductPurchases(totalProductPurchases);
        setTotalValue(totalValue);
        setTotalLikes(totalLikes);
    }
}
