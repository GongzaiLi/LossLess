package com.seng302.wasteless.service;

import com.seng302.wasteless.dto.mapper.PostListingsDtoMapper;
import com.seng302.wasteless.model.Listings;
import org.springframework.stereotype.Service;

@Service
public class ListingsService {

    public Listings createListing(Listings listingItem) {
        return listingItem;
    }
}
