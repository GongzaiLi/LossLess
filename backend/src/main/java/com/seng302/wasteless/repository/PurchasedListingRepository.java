package com.seng302.wasteless.repository;

import com.seng302.wasteless.model.PurchasedListing;
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
     * @return All Purchased Listing records that belong to a given business, and were purchased between the start and end dates
     */
    List<PurchasedListing> findAllByBusinessIdAndSaleDateBetween(Integer businessId, LocalDate startDate, LocalDate endDate);

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

    /**
     * Returns the total value of purchases for a specified business
     * @param businessId the id of the business
     */
    @Query(value = "Select sum(PL.price from PurchasedListing PL where PL.business_id = :businessId)", nativeQuery = true)
    Integer sumPriceByBusiness_Id(@Param("businessId") Integer businessId);

    /**
     * Returns the total value of purchases for a specified business
     * in a specified date range
     * @param businessId     the id of the business
     * @param startDate     The start date for the date range. Format yyyy-MM-dd
     * @param endDate       The end date for the date range. Format yyyy-MM-dd
     */
    @Query(value = "select sum(PL.price) from PurchasedListing PL where PL.business = :businessId and PL.saledate >= :startDate" +
            " and PL.saledate <= :endDate ", nativeQuery = true)
    Double sumPriceByBusiness_IdAndSaleDateBetween(@Param("businessId") Integer businessId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /**
     * Returns total number of sales for a given product.
     *
     * Business id does not need to be supplied as all product database ids are unique and this method
     * is not accessed without user being authenticated as a owner of a business of which this product
     * was created.
     *
     * @param productId     The database id of the product
     * @return              total number of sales for a given product of a business.
     */
    @Query(value = "select sum(PL.quantity) from PurchasedListing PL where PL.product = :productId", nativeQuery = true)
    Integer sumProductsSoldByProduct_DatabaseId(@Param("productId") Long productId);

    /**
     * Returns total price of all sales for a given product
     *
     * @param productId     The database id of the product
     * @return              total price of all sales for a given product
     */
    @Query(value = "select sum(PL.price) from PurchasedListing PL where PL.product = :productId", nativeQuery = true)
    Double sumPriceByProduct_DatabaseId(@Param("productId") Long productId);

    /**
     * Returns total likes of all sales for a given product
     *
     * @param productId     The database id of the product
     * @return              total likes of all sales for a given product
     */
    @Query(value = "select sum(PL.numberoflikes) from PurchasedListing PL where PL.product = :productId", nativeQuery = true)
    Integer sumTotalLikesByProduct_DatabaseId(@Param("productId") Long productId);

    /**
     * Returns list of database ids of all products that a business has sold any amount of
     *
     * @param businessId    the id of the business
     */
    @Query(value = "select distinct(PL.product) from PurchasedListing PL where PL.business = :businessId", nativeQuery = true)
    List<Long> getAllProductDatabaseIdsBySalesOfBusiness(@Param("businessId") Integer businessId);

}
