package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Listing service applies product logic over the Product JPA repository.
 */
@Service
public class ListingsService {
    private final ListingRepository listingRepository;

    @Autowired
    public ListingsService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

//    public static Specification<Listing> priceWithinRange(Integer lower, Integer upper) {
//        return (root, query, builder) -> builder.and(
//                builder.greaterThanOrEqualTo(root.get(Listing_.price), lower),
//                builder.lessThanOrEqualTo(root.get(Listing_.price), upper)
//        );
//    }

    /**
     * Returns a Specification that matches all listings with a product name that contains
     * the given product name. Matches are case-insensitive.
     *
     * @param productName Product name to match listings by
     * @return Specification that matches all listings with product name containing the name given
     */
    public static Specification<Listing> productNameMatches(String productName) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("inventoryItem").get("product").get("name")),
                "%" + productName.toLowerCase(Locale.ROOT) + "%");
    }


    /**
     * Given an Listing object, 'creates' it by saving and persisting it in the database.
     *
     * @param listingItem The Listing item item to create
     * @return The created listing item. The returned item should have a valid database id you can get with .getId()
     */
    public Listing createListing(Listing listingItem) {
        return listingRepository.save(listingItem);
    }

    /**
     * Gets listings for a given business using a given pageable
     *
     * @param id The id of the business
     * @return A list containing matching listings in the business.
     * Returns an empty list if there are no listings in the business, or if the business does not exist
     */
    public List<Listing> findBusinessListingsWithPageable(int id, Pageable pageable) {
        return listingRepository.findAllByBusinessId(id, pageable);
    }

    /**
     * Searches listings by product name using a given pageable
     *
     * @param searchQuery The search query - matches listings' product names by substring (case insensitive)
     * @return A list containing matching listings.
     */
    public Page<Listing> searchListings(String searchQuery, Pageable pageable) {
        return listingRepository.findAll(productNameMatches(searchQuery), pageable);
    }

    /**
     * Get the count of all listings of a business.
     *
     * @param id The id of the business
     * @return The count of listings of the business
     */
    public Long getCountOfAllListingsOfBusiness(int id) {
        return listingRepository.countListingByBusinessId(id);
    }
}
