package com.seng302.wasteless.repository;



import com.seng302.wasteless.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * ListingRepository is a repository interface for Inventory objects.
 * This declares 'raw' accessors to Inventory JPA objects. Don't use this class to get/update Inventory items. Use the
 * InventoryService instead as it may have business logic implemented.
 */
@RepositoryRestResource
public interface ListingRepository extends JpaRepository<Listing, Integer>, JpaSpecificationExecutor<Listing> {

    Listing findFirstById(Integer id);

    List<Listing> findAllByBusinessId(Integer id, Pageable pageable);

    Page<Listing> inventoryItemProductNameContainsAllIgnoreCase(String productName, Pageable pageable);

    Long countListingByBusinessId(Integer id);
}
