package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.repository.InventoryRepository;
import com.seng302.wasteless.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListingsService {
    private final ListingRepository listingRepository;

    @Autowired
    public ListingsService(ListingRepository listingRepository) { this.listingRepository = listingRepository;  }


    /**
     * Given an Listing object, 'creates' it by saving and persisting it in the database.
     * @param listingItem The Listing item item to create
     * @return The created listing item. The returned item should have a valid database id you can get with .getId()
     */
    public Listing createListing(Listing listingItem) {
        return listingRepository.save(listingItem);
    }

    /**
     * Get the entire inventory of items for a given business
     *
     * @param id The id of the business
     * @return A list containing every item in the business' listings.
     * Returns an empty list if there are no items in the business' listings, or if the business does not exist
     */
    public Listing findByBusinessId(int id) {
        return new Listing();
    }
}
