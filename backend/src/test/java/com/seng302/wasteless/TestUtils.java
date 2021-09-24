package com.seng302.wasteless;

import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class TestUtils {
    public static Address newThrowawayAddress() {
        var throwawayAddress = new Address();
        throwawayAddress.setCountry("NZ");
        throwawayAddress.setSuburb("Riccarton");
        throwawayAddress.setCity("Christchurch");
        throwawayAddress.setStreetNumber("1");
        throwawayAddress.setStreetName("Ilam Rd");
        throwawayAddress.setPostcode("8041");
        return throwawayAddress;
    }

    public static User newUserWithEmail(String email) {
        var newUser = new User();
        newUser.setRole(UserRoles.USER);
        newUser.setEmail(email);
        newUser.setPassword(new BCryptPasswordEncoder().encode("a"));
        newUser.setDateOfBirth(LocalDate.now().minusYears(17));
        newUser.setBio("Bio");
        newUser.setFirstName("FirstName");
        newUser.setLastName("LastName");
        newUser.setHomeAddress(newThrowawayAddress());
        newUser.setCreated(LocalDate.now());
        return newUser;
    }

    /**
     * Creates a new business with a given business ID
     * @param name
     * @return newBusiness the new created business
     */
    public static Business newBusinessWithName(String name) {
        var newBusiness = new Business();
        newBusiness.setName(name);
        newBusiness.setAddress(newThrowawayAddress());
        newBusiness.setCreated(LocalDate.now());
        newBusiness.setBusinessType(BusinessTypes.RETAIL_TRADE);
        newBusiness.setDescription("Description");
        return newBusiness;
    }

    /**
     * Creates a Listing with the given product name and price. This method is re-used in other tests so we need
     * to take in the services and parameters.
     */
    public static Listing createListingWithNameAndPrice(ProductService productService, InventoryService inventoryService,
                                                        ListingsService listingsService, BusinessService businessService,
                                                        AddressService addressService, String name, Double price,
                                                        String country, String city, String suburb, String businessName, BusinessTypes businessTypes, LocalDate closes,
                                                        Integer listingQuantity, Integer inventoryQuantity) {

        Address address = new Address();
        address.setCountry(country);
        address.setSuburb(suburb);
        address.setCity(city);
        address.setStreetNumber("1");
        address.setStreetName("Ilam Rd");
        address.setPostcode("8041");
        addressService.createAddress(address);

        Business business = new Business();
        business.setBusinessType(businessTypes);
        business.setAdministrators(new ArrayList<>());
        business.setName(businessName);
        business.setAddress(address);
        business.setCreated(LocalDate.now());
        businessService.createBusiness(business);

        Product product = new Product();
        product.setName(name);
        product.setManufacturer(businessName);
        productService.createProduct(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setExpires(LocalDate.MAX);
        inventory.setBusinessId(0);
        inventory.setQuantityUnlisted(inventoryQuantity - listingQuantity);
        inventory.setQuantity(inventoryQuantity);
        inventoryService.createInventory(inventory);

        Listing newListing = new Listing();
        newListing.setInventoryItem(inventory);
        newListing.setQuantity(listingQuantity);
        newListing.setBusiness(business);
        newListing.setPrice(price);
        newListing.setCloses(closes.atTime(23,59));
        newListing.setCreated(LocalDate.now());
        newListing.setUsersLiked(0);
        newListing = listingsService.createListing(newListing);

        return newListing;
    }

    /**
     * Creates a Listing with the given product name and price. This method is re-used in other tests so we need
     * to take in the services and parameters.
     */
    public static Listing createListingForSameBusiness(ProductService productService, InventoryService inventoryService,
                                                        ListingsService listingsService,
                                                        Business business, String name, Double price, LocalDate closes,
                                                        Integer listingQuantity, Integer inventoryQuantity) {

        Product product = new Product();
        product.setName(name);
        product.setManufacturer(name);
        productService.createProduct(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setExpires(LocalDate.MAX);
        inventory.setBusinessId(0);
        inventory.setQuantityUnlisted(inventoryQuantity - listingQuantity);
        inventory.setQuantity(inventoryQuantity);
        inventoryService.createInventory(inventory);

        Listing newListing = new Listing();
        newListing.setInventoryItem(inventory);
        newListing.setQuantity(listingQuantity);
        newListing.setBusiness(business);
        newListing.setPrice(price);
        newListing.setCloses(closes.atTime(23,59));
        newListing.setCreated(LocalDate.now());
        newListing.setUsersLiked(0);
        newListing = listingsService.createListing(newListing);

        return newListing;
    }

    /**
     * Creates a Listing with the given price. This method is re-used in other tests so we need
     * to take in the services and parameters.
     */
    public static Listing createListingForSameProductAndBusinessWithLikes (InventoryService inventoryService,
                                                                           ListingsService listingsService,
                                                                           Product product, Business business, Double price, LocalDate closes,
                                                                           Integer listingQuantity, Integer inventoryQuantity, Integer numOfLikes) {

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setExpires(LocalDate.MAX);
        inventory.setBusinessId(0);
        inventory.setQuantityUnlisted(inventoryQuantity - listingQuantity);
        inventory.setQuantity(inventoryQuantity);
        inventoryService.createInventory(inventory);

        Listing newListing = new Listing();
        newListing.setInventoryItem(inventory);
        newListing.setQuantity(listingQuantity);
        newListing.setBusiness(business);
        newListing.setPrice(price);
        newListing.setCloses(closes.atTime(23,59));
        newListing.setCreated(LocalDate.now());
        newListing.setUsersLiked(numOfLikes);
        newListing = listingsService.createListing(newListing);

        return newListing;
    }
}
