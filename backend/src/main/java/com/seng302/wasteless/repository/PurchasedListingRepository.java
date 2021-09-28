package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.ManufacturerSummary;
import com.seng302.wasteless.model.ProductSummary;
import com.seng302.wasteless.model.PurchasedListing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * @param businessId Id of business to get purchases for
     * @param startDate  The start date for the date range.
     * @param endDate    The end date for the date range.
     * @return All Purchased Listing records that belong to a given business, and were purchased between the start and end dates.
     * This will be sorted by the sale dates of the purchases
     */
    List<PurchasedListing> findAllByBusinessIdAndSaleDateBetweenOrderBySaleDate(Integer businessId, LocalDate startDate, LocalDate endDate);

    /**
     * Returns a Purchased Listing records that belong to a given purchaseListing Id
     * @param purchaseListingId id of purchase listing
     * @return A purchaseListing entity
     */
    PurchasedListing findFirstById(Integer purchaseListingId);

    /**
     * Returns the total number of purchases for a specified business
     * in a specified date range
     * @param businessId the id of the business
     */
    Integer countAllByBusinessIdAndSaleDateBetween(Integer businessId, LocalDate startDate, LocalDate endDate);

    /**
     * Returns the total value of purchases for a specified business
     * in a specified date range
     * @param businessId     the id of the business
     * @param startDate     The start date for the date range. Format yyyy-MM-dd
     * @param endDate       The end date for the date range. Format yyyy-MM-dd
     */
    @Query(value = "select sum(PL.price) from PurchasedListing PL where PL.business = :businessId and PL.saleDate >= :startDate" +
            " and PL.saleDate <= :endDate ", nativeQuery = true)
    Double sumPriceByBusinessIdAndSaleDateBetween(@Param("businessId") Integer businessId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * Returns total sales, value and likes for purchases of a business, grouped by product
     *
     * @param businessId Id of business to get purchases for
     * @param startDate  Start of date range to get purchases for
     * @param endDate    End of date range to get purchases for
     * @param pageable   Pageable object representing pagination and sorting
     * @return total sales, value and likes for purchases of a business, grouped by product
     */
    @Query(value = "SELECT PL.product AS productId, SUM(PL.price) AS value, SUM(PL.numberoflikes) AS likes, SUM(PL.quantity) AS quantity " +
            "from PurchasedListing PL " +
            "where PL.business = :businessId and PL.saleDate >= :startDate and PL.saleDate <= :endDate " +
            "GROUP BY PRODUCT ", nativeQuery = true)
    List<ProductSummary> getPurchasesGroupedByProduct(Integer businessId, LocalDate startDate,
                                                      LocalDate endDate, Pageable pageable);

    /**
     * Returns total sales, value and likes for purchases of a business, grouped by the manufacturer of the product
     * for each purchase
     *
     * @param businessId Id of business to get purchases for
     * @param startDate  Start of date range to get purchases for
     * @param endDate    End of date range to get purchases for
     * @param pageable   Pageable object representing pagination and sorting
     * @return total sales, value and likes for purchases of a business, grouped by product
     */
    @Query(value = "SELECT PL.manufacturer AS manufacturer, SUM(PL.price) AS value, SUM(PL.numberoflikes) AS likes, SUM(PL.quantity) AS quantity " +
            "from PurchasedListing PL " +
            "where PL.business = :businessId and PL.saleDate >= :startDate and PL.saleDate <= :endDate " +
            "GROUP BY MANUFACTURER ", nativeQuery = true)
    List<ManufacturerSummary> getPurchasesGroupedByManufacturer(Integer businessId, LocalDate startDate,
                                                                LocalDate endDate, Pageable pageable);

}
