package com.seng302.wasteless.service;

import com.seng302.wasteless.model.BusinessTypes;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.model.PurchasedListing;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.ListingRepository;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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

    private final InventoryService inventoryService;

    private final PurchasedListingRepository purchasedListingRepository;

    private final UserService userService;

    private static final String BUSINESSFIELD = "business";
    private static final String ADDRESSFIELD = "address";

    @Autowired
    public ListingsService(ListingRepository listingRepository, InventoryService inventoryService, PurchasedListingRepository purchasedListingRepository, UserService userService) {
        this.listingRepository = listingRepository;
        this.inventoryService = inventoryService;
        this.purchasedListingRepository = purchasedListingRepository;
        this.userService = userService;
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
     * Returns a Specification that matches all listings with close dates before or equal to the given close date
     *
     * @param date Upper inclusive bound for close date
     * @return Returns a Specification that matches all listings with close dates before or equal to the given close date
     */
    public static Specification<Listing> closesLessThanOrEqualTo(LocalDateTime date) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("closes"), date);
    }

    /**
     * Returns a Specification that matches all listings with close dates after or equal to the given close date
     *
     * @param date Lower inclusive bound for close date
     * @return Returns a Specification that matches all listings with close dates after or equal to the given close date
     */
    public static Specification<Listing> closesGreaterThanOrEqualTo(LocalDateTime date) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("closes"), date);
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
                builder.lower(root.get(BUSINESSFIELD).get(ADDRESSFIELD).get("country")),
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
                builder.lower(root.get(BUSINESSFIELD).get(ADDRESSFIELD).get("city")),
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
                builder.lower(root.get(BUSINESSFIELD).get(ADDRESSFIELD).get("suburb")),
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
                builder.lower(root.get(BUSINESSFIELD).get("name")),
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

        return (root, query, builder) -> root.get(BUSINESSFIELD).get("businessType").in(businessTypes);
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
     * Given an Listing object, 'updates' it by saving and persisting it in the database.
     * @param listingItem The Listing item item to update
     *
     */
    public void updateListing(Listing listingItem) {
        listingRepository.save(listingItem);
    }

    /**
     * Returns the listing with the given ID.
     * @param id Id to find the listing of
     * @return The listing with the given ID.
     * @throws ResponseStatusException If no listing exists with the given id
     */
    public Listing findFirstById(Integer id) {
        var listing = listingRepository.findFirstById(id);
        if (listing.isPresent()) {
            return listing.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "No listing exists with the given id");
        }
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
     * @param searchQuery The search query - matches listings' product names by substring (case insensitive)
     * @param priceLower  Lower inclusive bound for listing prices
     * @param priceUpper  Upper inclusive bound for listing prices
     * @param businessName  Business name to match against listings* @param businessTypes List of business types to match against listings
     * @param businessTypes List of business types to match against listings
     * @param address     Address to match against suburb, city, and country of lister of listing
     * @param closingDateStart  Lower inclusive bound for listing close dates
     * @param closingDateEnd  Upper inclusive bound for listing close dates
     * @param pageable    Object containing pagination and sorting info
     * @return A Page containing matching listings.
     */
    public Page<Listing> searchListings(
            Optional<String> searchQuery,
            Optional<Double> priceLower,
            Optional<Double> priceUpper,
            Optional<String> businessName,
            Optional<List<String>> businessTypes,
            Optional<String> address,
            Optional<LocalDateTime> closingDateStart,
            Optional<LocalDateTime> closingDateEnd,
            Pageable pageable) {
        Specification<Listing> querySpec = productNameMatches(searchQuery.orElse(""));

        if (priceLower.isPresent()) querySpec = querySpec.and(priceGreaterThanOrEqualTo(priceLower.get()));
        if (priceUpper.isPresent()) querySpec = querySpec.and(priceLessThanOrEqualTo(priceUpper.get()));
        if (businessName.isPresent()) querySpec = querySpec.and(sellerBusinessNameMatches(businessName.get()));
        if (businessTypes.isPresent() && !businessTypes.get().stream().allMatch(String::isEmpty))
            querySpec = querySpec.and(sellerBusinessTypeMatches(businessTypes.get()));
        if (closingDateStart.isPresent()) querySpec = querySpec.and(closesGreaterThanOrEqualTo(closingDateStart.get()));
        if (closingDateEnd.isPresent()) querySpec = querySpec.and(closesLessThanOrEqualTo(closingDateEnd.get()));

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
     * Purchases the given listing, and deletes it. The listing is assumed to exist (ie it cannot have already
     * been deleted in the DB). Will update the listing's inventory item's quantity as well.
     *
     * Deletes all likes on all users for this listing before deleting the listing
     *
     * @param listing The listing to purchase
     * @param purchaser The user that purchased this listing
     * @return A saved PurchasedListing object representing a record of this purchase
     */
    public PurchasedListing purchase(Listing listing, User purchaser) {
        PurchasedListing purchaseRecord = listing.purchase(purchaser);
        inventoryService.updateInventory(listing.getInventoryItem());
        userService.unlikePurchasedListing(listing);
        listingRepository.delete(listing);
        return purchasedListingRepository.save(purchaseRecord);
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
