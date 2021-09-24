package com.seng302.wasteless.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.time.LocalDate;

/**
 * Data transfer object for SalesReport endpoint, used to return the correct data in the correct format.
 * The object includes the total purchased listings for a business during a period with their total value.
 * A sales query is transformed into SalesReportDto via the SalesReportDtoMapper
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class SalesReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalPurchases;
    private Double totalValue;

    public SalesReportDto(LocalDate startDate, LocalDate endDate, Integer totalPurchases, Double totalValue) {
        setStartDate(startDate);
        setEndDate(endDate);
        setTotalPurchases(totalPurchases);
        setTotalValue(totalValue);
    }

}
