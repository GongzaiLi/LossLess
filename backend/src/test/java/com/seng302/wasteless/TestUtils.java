package com.seng302.wasteless;

import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;

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
        newListing.setCloses(closes);
        newListing.setCreated(LocalDate.now());
        newListing = listingsService.createListing(newListing);

        return newListing;
    }
}
