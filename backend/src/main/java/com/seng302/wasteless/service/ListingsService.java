package com.seng302.wasteless.service;

import com.seng302.wasteless.model.BusinessTypes;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    /**
     * Returns a Specification that matches all listings with price greater than or equal to the given price
     *
     * @param price Lower inclusive bound for price
     * @return A Specification that matches all listings with price greater than or equal to the given price
     */
    public static Specification<Listing> priceGreaterThanOrEqualTo(Double price) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), price);
    }

    /**
     * Returns a Specification that matches all listings with price less than or equal to the given price
     *
     * @param price Upper inclusive bound for price
     * @return A Specification that matches all listings with price less than or equal to the given price
     */
    public static Specification<Listing> priceLessThanOrEqualTo(Double price) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), price);
    }

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
     * Returns a Specification that matches all listings with the country portion of an address.
     * Matches are case-insensitive
     *
     * @param country Country to match listings by
     * @return Specification that matches all listings with address potion country matching given country
     */
    public static Specification<Listing> sellerAddressCountryMatches(String country) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("business").get("address").get("country")),
                "%" + country.toLowerCase(Locale.ROOT) + "%");
    }

    /**
     * Returns a Specification that matches all listings with the City portion of an address.
     * Matches are case-insensitive
     *
     * @param city City to match listings by
     * @return Specification that matches all listings with address potion city matching given city
     */
    public static Specification<Listing> sellerAddressCityMatches(String city) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("business").get("address").get("city")),
                "%" + city.toLowerCase(Locale.ROOT) + "%");
    }

    /**
     * Returns a Specification that matches all listings with the Suburb portion of an address.
     * Matches are case-insensitive
     *
     * @param suburb Suburb to match listings by
     * @return Specification that matches all listings with address potion suburb matching given suburb
     */
    public static Specification<Listing> sellerAddressSuburbMatches(String suburb) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("business").get("address").get("suburb")),
                "%" + suburb.toLowerCase(Locale.ROOT) + "%");
    }

    /**
     * Returns a Specification that matches all listings with the name of a business.
     * Matches are case-insensitive.
     *
     * @param businessName business name to match listings by
     * @return Specification that matches all listings with the name of the business matching given the business name.
     */
    private Specification<Listing> sellerBusinessNameMatches(String businessName) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("business").get("name")),
                "%" + businessName.toLowerCase(Locale.ROOT) + "%");
    }

    /**
     * Returns a Specification that matches all listings with valid business types within the scope of
     * the business types enum.
     * Matches are case-insensitive.
     *
     * @param types list of Business types to match listings
     * @return Specification that matches all listings with the type of the business matching given the business types
     */
    private Specification<Listing> sellerBusinessTypeMatches(List<String> types) {
        List<BusinessTypes> businessTypes = new ArrayList<>();
        for (String type : types) {
            if (!type.isEmpty()) {
                businessTypes.add(BusinessTypes.fromString(type));
            }
        }

        return (root, query, builder) -> root.get("business").get("businessType").in(businessTypes);
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
     * Searches listings by product name using a given pageable and filter parameters.
     * Any or all of the filter/search params are optional. The Pageable cannot be null
     * but can simply be a Pageable.unpaged() object
     *
     * @param searchQuery   The search query - matches listings' product names by substring (case insensitive)
     * @param priceLower    Lower inclusive bound for listing prices
     * @param priceUpper    Upper inclusive bound for listing prices
     * @param businessName  Business name to match against listings
     * @param businessTypes List of business types to match against listings
     * @param address       Address to match against suburb, city, and country of lister of listing
     * @param pageable      Object containing pagination and sorting info
     * @return A Page containing matching listings.
     */
    public Page<Listing> searchListings(
            Optional<String> searchQuery,
            Optional<Double> priceLower,
            Optional<Double> priceUpper,
            Optional<String> businessName,
            Optional<List<String>> businessTypes,
            Optional<String> address,
            Pageable pageable) {
        Specification<Listing> querySpec = productNameMatches(searchQuery.orElse(""));

        if (priceLower.isPresent()) querySpec = querySpec.and(priceGreaterThanOrEqualTo(priceLower.get()));
        if (priceUpper.isPresent()) querySpec = querySpec.and(priceLessThanOrEqualTo(priceUpper.get()));
        if (businessName.isPresent()) querySpec = querySpec.and(sellerBusinessNameMatches(businessName.get()));
        if (businessTypes.isPresent() && !businessTypes.get().isEmpty())
            querySpec = querySpec.and(sellerBusinessTypeMatches(businessTypes.get()));

        if (address.isPresent()) {
            querySpec = querySpec.and(
                    sellerAddressCountryMatches(address.get())
                            .or(sellerAddressCityMatches(address.get()))
                            .or(sellerAddressSuburbMatches(address.get()))
            );
        }


        return listingRepository.findAll(querySpec, pageable);
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
