package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.PurchasedListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

/**
 * This is the repository interface for PurchasedListing objects.
 * This declares 'raw' accessors to PurchasedListing JPA objects, particularly to save them into the DB
 */
@RepositoryRestResource
public interface PurchasedListingRepository extends JpaRepository<PurchasedListing, Integer>{
    /**
     * Returns all Purchased Listing records that belong to a given business
     * @param businessId Id of business to get listing purchase records for
     * @return All Purchased Listing records that belong to the given business
     */
    List<PurchasedListing> findAllByBusinessId(Integer businessId);


    /**
     * Returns a Purchased Listing records that belong to a given purchaseListing Id
     * @param purchaseListingId id of purchase listing
     * @return A purchaseListing entity
     */
    PurchasedListing findFirstById(Integer purchaseListingId);

    /**
     * Returns the total number of purchases for a specified business
     * @param businessId the id of the business
     */
    Integer countAllByBusiness_Id(Integer businessId);

    /**
     * Returns the total number of purchases for a specified business
     * in a specified date range
     * @param businessId the id of the business
     */
    Integer countAllByBusiness_IdAndSaleDateBetween(Integer businessId, LocalDate startDate, LocalDate endDate);
}
