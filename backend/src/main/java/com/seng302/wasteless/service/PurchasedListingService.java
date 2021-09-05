package com.seng302.wasteless.service;

import com.seng302.wasteless.dto.SalesReportDto;
import com.seng302.wasteless.model.PurchasedListing;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * PurchasedListing service applies product logic over the Product JPA repository.
 */
@Service
public class PurchasedListingService {

    private final PurchasedListingRepository purchasedListingRepository;

    @Autowired
    public PurchasedListingService(PurchasedListingRepository purchasedListingRepository) {
        this.purchasedListingRepository = purchasedListingRepository;
    }


    /**
     * Returns the PurchasedListing with the given ID.
     * @param id Id to find the listing of
     * @return The PurchasedListing with the given ID.
     */
    public PurchasedListing findPurchasedListingById(Integer id) {
        return purchasedListingRepository.findFirstById(id);
    }

    /**
     * Returns the total number of purchased listings for a business.
     * @param businessId Id of the business
     * @return The count of purchased listings
     */
    public Integer countPurchasedListingForBusiness(Integer businessId) {return purchasedListingRepository.countAllByBusiness_Id(businessId);}

    /**
     * Returns the total number of purchased listings for a business
     * in a specified date range
     * @param businessId Id of the business
     * @param startDate start date of range
     * @param endDate end date of range
     * @return The count of purchased listings
     */
    public Integer countPurchasedListingForBusinessInDateRange(Integer businessId, LocalDate startDate, LocalDate endDate) {
        return purchasedListingRepository.countAllByBusiness_IdAndSaleDateBetween(businessId, startDate, endDate);}

    /**
     * Returns the total value of purchased listings for a business.
     * @param businessId Id of the business
     * @return The count of purchased listings
     */
    public Integer totalPurchasedListingValueForBusiness(Integer businessId) {
        return purchasedListingRepository.sumPriceByBusiness_Id(businessId);}

    /**
     * Returns the total value of purchased listings for a business.
     * @param businessId    Id of the business
     * @param startDate     The start date for the date range. Format yyyy-MM-dd
     * @param endDate       The end date for the date range. Format yyyy-MM-dd
     * @return The count of purchased listings
     */
    public Double totalPurchasedListingValueForBusinessInDateRange(Integer businessId, LocalDate startDate, LocalDate endDate) {
        return purchasedListingRepository.sumPriceByBusiness_IdAndSaleDateBetween(businessId, startDate, endDate);}

    /**
     * For loops through each specified period in a date range and returns the total number of purchases and the total
     * value of those purchases for each date range within a chosen business.
     *
     * @param businessId the id of the business
     * @param startDate the start date of the date range
     * @param endDate the end date of the date range
     * @param firstPeriodStart the start date of the firstPeriod
     * @param lastPeriodEnd the end date fo the last period
     * @param periodOfData the specified period for datta
     * @return a list of SalesReportDtos to be sent to the frontend
     */
    public List<SalesReportDto> getSalesReportDataWithPeriod(Integer businessId, LocalDate startDate, LocalDate endDate, LocalDate firstPeriodStart, LocalDate lastPeriodEnd, Period periodOfData) {
        List<SalesReportDto> responseBody = new ArrayList<>();

        LocalDate searchStart;
        LocalDate searchEnd;
        for (LocalDate date = firstPeriodStart; date.isBefore(lastPeriodEnd.plusDays(1)); date = date.plus(periodOfData)) {
            searchStart = date;
            searchEnd = searchStart.plus(periodOfData).minusDays(1);
            if (searchStart.isBefore(startDate)) {
                searchStart = startDate;
            }
            if (searchEnd.isAfter(endDate)) {
                searchEnd = endDate;
            }
            Integer totalPurchases = this.countPurchasedListingForBusinessInDateRange(businessId, searchStart, searchEnd);
            Double totalValue = this.totalPurchasedListingValueForBusinessInDateRange(businessId, searchStart, searchEnd);

            if (totalValue == null) {
                totalValue = 0.0;
            }

            SalesReportDto reportDto = new SalesReportDto(searchStart, searchEnd, totalPurchases, totalValue);
            responseBody.add(reportDto);
        }
        return responseBody;
    }

    /**
     * Returns the total number of purchases and the total value of those purchases for a specified date range and
     * business.
     *
     * @param businessId the id of the business
     * @param startDate the start date of the date range
     * @param endDate the end date of the date range
     * @return a list with a single SalesReportDto to be sent to the frontend
     */
    public List<SalesReportDto> getSalesReportDataNoPeriod(Integer businessId, LocalDate startDate, LocalDate endDate) {

        Integer totalPurchases = this.countPurchasedListingForBusinessInDateRange(businessId, startDate, endDate);
        Double totalValue = this.totalPurchasedListingValueForBusinessInDateRange(businessId, startDate, endDate);

        if (totalValue == null) {
            totalValue = 0.0;
        }

        SalesReportDto reportDto = new SalesReportDto(startDate, endDate, totalPurchases, totalValue);
        List<SalesReportDto> responseBody = new ArrayList<>();
        responseBody.add(reportDto);

        return responseBody;
    }

}
