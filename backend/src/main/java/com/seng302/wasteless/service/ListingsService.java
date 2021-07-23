package com.seng302.wasteless.service;

import com.seng302.wasteless.model.GetListingsSortTypes;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Listing service applies product logic over the Product JPA repository.
 */
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
     * Gets count listings for a given business starting at offset 'page'
     *
     * sorts by sortBy and sortDirection
     *
     * Special case where sortBy is NAME, as it needs to be handled in a special way.
     *
     * @param id The id of the business
     * @
     * @return A list containing matching listings in the business.
     * Returns an empty list if there are no listings in the business, or if the business does not exist
     */
    public List<Listing> findCountByBusinessIdFromOffset(int id, int offset, int count, GetListingsSortTypes sortBy, String sortDirection) {

        if (GetListingsSortTypes.NAME.equals(sortBy)) {
            if (sortDirection.equals("DESC")) {
                return listingRepository.findAllByBusinessIdSortedByProductNameDESC(id, count, offset*count);
            } else {
                return listingRepository.findAllByBusinessIdSortedByProductNameASC(id, count, offset*count);
            }
        } else {
            Pageable pageable = PageRequest.of(
                    offset,
                    count,
                    sortDirection.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                    sortBy.toString()
            );

            return listingRepository.findAllByBusinessId(id, pageable);
        }
    }

    /**
     * Get the count of all listings of a business.
     *
     * @param id The id of the business
     * @return  The count of listings of the business
     */
    public Integer getCountOfAllListingsOfBusiness(int id) {
        return listingRepository.countListingByBusinessId(id);
    }
}
