package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Listing;
import org.springframework.stereotype.Service;

@Service
public class ListingsService {

    public Listing createListing(Listing listingItem) {
        return listingItem;
    }

    public Listing findByBusinessId(int anyInt) {
        return new Listing();
    }
}
