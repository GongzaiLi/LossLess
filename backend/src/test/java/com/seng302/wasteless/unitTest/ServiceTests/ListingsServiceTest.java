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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


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
        businessService.createBusiness(business);

        Product product = new Product();
        product.setName(name);
        productService.createProduct(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setExpires(LocalDate.MAX);
        inventory.setBusinessId(0);
        inventory.setQuantityInListing(listingQuantity);
        inventory.setQuantity(inventoryQuantity);
        inventoryService.createInventory(inventory);

        Listing newListing = new Listing();
        newListing.setInventoryItem(inventory);
        newListing.setQuantity(listingQuantity);
        newListing.setBusiness(business);
        newListing.setPrice(price);
        newListing.setCloses(closes);
        newListing = listingsService.createListing(newListing);

        return newListing;
    }

    @BeforeAll
    void setUp() {
        createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Black Water No Sugar", 1.0, "NZ", "Christchurch", "Riccarton", "Wonka Water", BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES, LocalDate.of(2099, Month.JANUARY, 1), 69, 69);
        createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Back Water", 1.5, "NZ", "Christchurch", "Riccarton", "Wonka Water", BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES,LocalDate.of(2099, Month.FEBRUARY, 1), 69, 69);
        createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Willy Wonka", 2.0, "NZ", "Christchurch", "Riccarton", "Peaches and Wonka", BusinessTypes.RETAIL_TRADE, LocalDate.of(2099, Month.MARCH, 1), 69, 69);
        createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Wonka Willy", 100.0, "NZ", "Christchurch", "Riccarton", "Fraud", BusinessTypes.CHARITABLE_ORGANISATION, LocalDate.of(2099, Month.MARCH, 10), 69, 69);
    }

    //
    //  FILTER BY PRODUCT NAME
    //

    @Test
    void whenSearchByProductName_andCaseInsensitivePartialMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.of("water"), Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertTrue(names.containsAll(Arrays.asList("Back Water", "Black Water No Sugar"))
                && Arrays.asList("Back Water", "Black Water No Sugar").containsAll(names));
    }

    @Test
    void whenSearchByProductName_andFullMatchExists_thenFullMatchReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.of("Back Water"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertTrue(names.contains("Back Water")
                && Collections.singletonList("Back Water").containsAll(names));
    }

    @Test
    void whenSearchByProductName_andNoMatchesExist_thenEmptyListReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.of("Blah"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    //
    //  FILTER BY PRICE
    //

    @Test
    void whenFilterByPriceRange_andUpperAndLowerInclusive_thenExcludedListingsNotReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.of(1.5), Optional.of(2.0), Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        System.out.println(names);
        assertTrue(names.containsAll(Arrays.asList("Willy Wonka", "Back Water"))
                && Arrays.asList("Back Water", "Willy Wonka").containsAll(names));
    }

    @Test
    void whenFilterByPriceRange_andUpperAndLowerExclusive_thenAllListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.of(0.0), Optional.of(10000.0), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertTrue(names.containsAll(Arrays.asList("Willy Wonka", "Black Water No Sugar", "Back Water", "Wonka Willy"))
                && Arrays.asList("Willy Wonka", "Black Water No Sugar", "Back Water", "Wonka Willy").containsAll(names));
    }

    @Test
    void whenFilterByPriceRange_andRangeTooLow_thenNoListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.of(0.0), Optional.of(0.1), Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    @Test
    void whenFilterByPriceRange_andRangeTooHigh_thenNoListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.of(101.0), Optional.of(101.0), Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    //
    // FILTER BY ADDRESS
    //

    @Test
    void whenFilterByAddressCountry_andAddressMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("NZ"), Optional.empty(), Optional.empty(),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(4, names.size());
    }

    @Test
    void whenFilterByAddressCity_andAddressMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("Riccarton"),Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(4, names.size());
    }

    @Test
    void whenFilterByAddressSuburb_andAddressMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("Christchurch"), Optional.empty(), Optional.empty(),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(4, names.size());
    }

    @Test
    void whenFilterByAddress_andNoMatchesExists_thenNoMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("Aus"), Optional.empty(), Optional.empty(),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    //
    // FILTER BY BUSINESS NAME
    //

    @Test
    void whenFilterByBusinessName_andBusinessNameMatchesExists_thenAllPartialMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("Wonka"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(3, names.size());
    }

    @Test
    void whenFilterByBusinessName_andNoMatchesExists_thenNoMatchesReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("Apple"), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    //
    // FILTER BY BUSINESS TYPE
    //

    @Test
    void whenFilterByBusinessType_andBusinessTypeMatchesExists_thenAllPartialMatchesReturned() {
        List<String> types = Arrays.asList("Accommodation and Food Services", "Retail trade");
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(types), Optional.empty(), Optional.empty(), Optional.empty(),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(3, names.size());
    }

    @Test
    void whenFilterByBusinessType_andNoMatchesExists_thenNoMatchesReturned() {
        List<String> types = Arrays.asList("Non-profit organisation");
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(types), Optional.empty(), Optional.empty(), Optional.empty(),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    @Test
    void whenFilterByInvalidBusinessType_thenNoMatchesReturned() {
        try {
            List<String> types = Arrays.asList("organisation");
            listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(types), Optional.empty(), Optional.empty(), Optional.empty(),Pageable.unpaged());
        } catch (ResponseStatusException e) {
            assertEquals(400, e.getRawStatusCode());
        }
    }

    @Test
    void whenFilterByOneValidBusinessTypeAndOneInvalidBusinessType_andValidBusinessTypeMatchesExists_thenAllPartialMatchesReturned() {
        try {
            List<String> types = Arrays.asList("organisation", "Accommodation and Food Services");
            listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(types), Optional.empty(), Optional.empty(), Optional.empty(),Pageable.unpaged());
        } catch (ResponseStatusException e) {
            assertEquals(400, e.getRawStatusCode());
        }
    }

    //
    //  FILTER BY CLOSING DATE RANGE
    //

    @Test
    void whenFilterByClosingDateRange_andUpperAndLowerInclusive_thenExcludedListingsNotReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(),Optional.empty(), Optional.of(LocalDate.of(2099, Month.FEBRUARY, 1)),Optional.of(LocalDate.of(2099, Month.MARCH, 1)),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        System.out.println(names);
        assertTrue(names.containsAll(Arrays.asList("Willy Wonka", "Back Water"))
                && Arrays.asList("Back Water", "Willy Wonka").containsAll(names));
    }

    @Test
    void whenFilterByClosingDateRange_andUpperAndLowerExclusive_thenAllListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(), Optional.of(LocalDate.of(2000, Month.FEBRUARY, 1)),Optional.of(LocalDate.of(2100, Month.MARCH, 1)),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertTrue(names.containsAll(Arrays.asList("Willy Wonka", "Black Water No Sugar", "Back Water", "Wonka Willy"))
                && Arrays.asList("Willy Wonka", "Black Water No Sugar", "Back Water", "Wonka Willy").containsAll(names));
    }

    @Test
    void whenFilterByClosingDateRange_andRangeTooLow_thenNoListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(),Optional.empty(), Optional.of(LocalDate.of(2000, Month.FEBRUARY, 1)),Optional.of(LocalDate.of(2001, Month.MARCH, 1)),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }

    @Test
    void whenFilterByClosingDateRange_andRangeTooHigh_thenNoListingsReturned() {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(),Optional.empty(), Optional.of(LocalDate.of(2100, Month.FEBRUARY, 1)),Optional.of(LocalDate.of(2101, Month.MARCH, 1)),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(0, names.size());
    }
}
