package com.seng302.wasteless.service;

import com.seng302.wasteless.model.PurchasedListing;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
