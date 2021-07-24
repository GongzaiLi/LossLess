package com.seng302.wasteless.repository;



import com.seng302.wasteless.model.Listing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * ListingRepository is a repository interface for Inventory objects.
 * This declares 'raw' accessors to Inventory JPA objects. Don't use this class to get/update Inventory items. Use the
 * InventoryService instead as it may have business logic implemented.
 */
@RepositoryRestResource
public interface ListingRepository extends JpaRepository<Listing, Integer> {

    Listing findFirstById(Integer id);

    List<Listing> findAllByBusinessId(Integer id, Pageable pageable);

    @Query(value = "select L.* from LISTING L JOIN INVENTORY I ON L.INVENTORY_ID = I.ID JOIN PRODUCT P ON P.DATABASE_ID = I.PRODUCT_ID where L.BUSINESSID = :businessId ORDER BY LOWER(P.NAME) ASC offset :offsetNumber rows fetch next :countNumber rows only", nativeQuery = true)
    List<Listing> findAllByBusinessIdSortedByProductNameASC(@Param("businessId") Integer businessId, @Param("countNumber") Integer count, @Param("offsetNumber") Integer offset);

    @Query(value = "select L.* from LISTING L JOIN INVENTORY I ON L.INVENTORY_ID = I.ID JOIN PRODUCT P ON P.DATABASE_ID = I.PRODUCT_ID where L.BUSINESSID = :businessId ORDER BY LOWER(P.NAME) DESC offset :offsetNumber rows fetch next :countNumber rows only", nativeQuery = true)
    List<Listing> findAllByBusinessIdSortedByProductNameDESC(@Param("businessId") Integer businessId, @Param("countNumber") Integer count, @Param("offsetNumber") Integer offset);

    Integer countListingByBusinessId(Integer id);

}
