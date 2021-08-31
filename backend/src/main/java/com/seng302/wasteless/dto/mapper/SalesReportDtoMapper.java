package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.SalesReportDto;
import com.seng302.wasteless.service.PurchasedListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * SalesReportDtoMapper is used to transform a sales report query into a SalesReportDto object.
 */
@Component
public class SalesReportDtoMapper {
    private static PurchasedListingService purchasedListingService;

    @Autowired
    public SalesReportDtoMapper(PurchasedListingService purchasedListingService) {
        SalesReportDtoMapper.purchasedListingService = purchasedListingService;
    }

    /**
     * SalesReportDtoMapper is used to transform a sales report query for a business into a SalesReportDto object.
     * Gets the count of the purchased listing for the business from the start date to the end date.
     *
     * @param businessId       The id of the business to get the sales report for
     * @param startDate        The date to get the sales report from
     * @param endDate          The date to get the sales report up to
     *
     * @return                 The total purchases for the business
     */
    public static SalesReportDto toGetSalesReportDto(Integer businessId, LocalDate startDate, LocalDate endDate) {

        int totalPurchases;
        double totalValue;

        if (startDate == null && endDate == null) {
            totalPurchases = 10;   // Hardcoded total purchased listings purchasedListingService should be used
            totalValue = 100.00; // Hardcoded total value of purchased listings
        } else {
            totalPurchases = 1;
            totalValue = 10.00;
        }
        return new SalesReportDto().setTotalPurchases(totalPurchases)
                .setTotalValue(totalValue);
    }
}
