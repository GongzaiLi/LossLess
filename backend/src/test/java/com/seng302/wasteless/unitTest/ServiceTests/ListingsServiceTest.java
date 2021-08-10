package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)     // Allows non-static BeforeAll methods. baeldung.com/java-beforeall-afterall-non-static
public class ListingsServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ListingsService listingsService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private AddressService addressService;

    /**
     * Creates a Listing with the given product name and price. This method is re-used in other tests so we need
     * to take in the services and parameters.
     */
    public static void createListingWithNameAndPrice(ProductService productService, InventoryService inventoryService,
                                                     ListingsService listingsService, BusinessService businessService,
                                                     AddressService addressService,  String name, Double price,
                                                     String country, String city, String suburb) {

        Address address = new Address();
        address.setCountry(country);
        address.setSuburb(suburb);
        address.setCity(city);
        address.setStreetNumber("1");
        address.setStreetName("Ilam Rd");
        address.setPostcode("8041");
        addressService.createAddress(address);

        Business business = new Business();
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setAdministrators(new ArrayList<>());
        business.setName("Jimmy's clown store");
        business.setAddress(address);
        businessService.createBusiness(business);

        Product product = new Product();
        product.setName(name);
        productService.createProduct(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setExpires(LocalDate.MAX);
        inventory.setBusinessId(0);
        inventoryService.createInventory(inventory);

        Listing newListing = new Listing();
        newListing.setInventoryItem(inventory);
        newListing.setQuantity(69);
        newListing.setBusiness(business);
        newListing.setPrice(price);
        listingsService.createListing(newListing);
    }

    @BeforeAll
    void setUp() {
        createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Black Water No Sugar", 1.0, "NZ", "Christchurch", "Riccarton");
        createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Back Water", 1.5, "NZ", "Christchurch", "Riccarton");
        createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Willy Wonka", 2.0, "NZ", "Christchurch", "Riccarton");
        createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Wonka Willy", 100.0, "NZ", "Christchurch", "Riccarton");
    }

    //
    //  FILTER BY PRODUCT NAME
    //

    @Test
    void whenSearchByProductName_andCaseInsensitivePartialMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.of("water"), Optional.empty(), Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertTrue(names.containsAll(Arrays.asList("Back Water", "Black Water No Sugar"))
                && Arrays.asList("Back Water", "Black Water No Sugar").containsAll(names));
    }

    @Test
    void whenSearchByProductName_andFullMatchExists_thenFullMatchReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.of("Back Water"), Optional.empty(), Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertTrue(names.contains("Back Water")
                && Collections.singletonList("Back Water").containsAll(names));
    }

    @Test
    void whenSearchByProductName_andNoMatchesExist_thenEmptyListReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.of("Blah"), Optional.empty(), Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    //
    //  FILTER BY PRICE
    //

    @Test
    void whenFilterByPriceRange_andUpperAndLowerInclusive_thenExcludedListingsNotReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.of(1.5), Optional.of(2.0), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        System.out.println(names);
        assertTrue(names.containsAll(Arrays.asList("Willy Wonka", "Back Water"))
                && Arrays.asList("Back Water", "Willy Wonka").containsAll(names));
    }

    @Test
    void whenFilterByPriceRange_andUpperAndLowerExclusive_thenAllListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.of(0.0), Optional.of(10000.0), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertTrue(names.containsAll(Arrays.asList("Willy Wonka", "Black Water No Sugar", "Back Water", "Wonka Willy"))
                && Arrays.asList("Willy Wonka", "Black Water No Sugar", "Back Water", "Wonka Willy").containsAll(names));
    }

    @Test
    void whenFilterByPriceRange_andRangeTooLow_thenNoListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.of(0.0), Optional.of(0.1), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    @Test
    void whenFilterByPriceRange_andRangeTooHigh_thenNoListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.of(101.0), Optional.of(101.0), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    //
    // FILTER BY ADDRESS
    //

    @Test
    void whenFilterByAddressCountry_andAddressMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("NZ"), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(4, names.size());
    }

    @Test
    void whenFilterByAddressCity_andAddressMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("Riccarton"), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(4, names.size());
    }

    @Test
    void whenFilterByAddressSuburb_andAddressMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("Christchurch"), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(4, names.size());
    }

    @Test
    void whenFilterByAddress_andNoMatchesExists_thenNoMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("Aus"), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }
}
