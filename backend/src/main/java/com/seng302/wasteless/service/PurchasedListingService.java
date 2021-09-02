package com.seng302.wasteless.service;

import com.seng302.wasteless.model.PurchasedListing;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
    public Integer totalPurchasedListingValueForBusinessInDateRange(Integer businessId, LocalDate startDate, LocalDate endDate) {
        return purchasedListingRepository.sumPriceByBusiness_IdAndSaleDateBetween(businessId, startDate, endDate);}

}
