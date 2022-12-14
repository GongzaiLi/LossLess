package com.seng302.wasteless.repository;


import com.seng302.wasteless.model.Listing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

/**
 * ListingRepository is a repository interface for Inventory objects.
 * This declares 'raw' accessors to Listing JPA objects. Don't use this class to get/update Listing items. Use the
 * ListingService instead as it may have business logic implemented.
 */
@RepositoryRestResource
public interface ListingRepository extends JpaRepository<Listing, Integer>, JpaSpecificationExecutor<Listing> {

    Optional<Listing> findFirstById(Integer id);

    List<Listing> findAllByBusinessId(Integer businessId, Pageable pageable);

    Long countListingByBusinessId(Integer businessId);
}
