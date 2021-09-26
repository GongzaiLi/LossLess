package com.seng302.wasteless.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * A object that represents  the total purchased listings and value for a business during a period.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class SalesReportSinglePeriod {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalPurchases;
    private Double totalValue;

    public SalesReportSinglePeriod(LocalDate startDate, LocalDate endDate, Integer totalPurchases, Double totalValue) {
        setStartDate(startDate);
        setEndDate(endDate);
        setTotalPurchases(totalPurchases);
        setTotalValue(totalValue);
    }

}
