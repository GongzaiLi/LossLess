package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.TestUtils;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import static com.seng302.wasteless.TestUtils.newUserWithEmail;
import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)     // Allows non-static BeforeAll methods. baeldung.com/java-beforeall-afterall-non-static
class ListingsServiceTest {
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

    @Autowired
    private UserService userService;

    private User curUser;

    @BeforeAll
    void setUp() {
        TestUtils.createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Black Water No Sugar", 1.0, "NZ", "Christchurch", "Riccarton", "Wonka Water", BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES, LocalDate.of(2099, Month.JANUARY, 1), 69, 69);
        TestUtils.createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Back Water", 1.5, "NZ", "Christchurch", "Riccarton", "Wonka Water", BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES,LocalDate.of(2099, Month.FEBRUARY, 1), 69, 69);
        TestUtils.createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Willy Wonka", 2.0, "NZ", "Christchurch", "Riccarton", "Peaches and Wonka", BusinessTypes.RETAIL_TRADE, LocalDate.of(2099, Month.MARCH, 1), 69, 69);
        TestUtils.createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Wonka Willy", 100.0, "NZ", "Christchurch", "Riccarton", "Fraud", BusinessTypes.CHARITABLE_ORGANISATION, LocalDate.of(2099, Month.MARCH, 10), 69, 69);

        curUser = newUserWithEmail("big@dave");
        addressService.createAddress(curUser.getHomeAddress());
        curUser = userService.createUser(curUser);
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

    @ParameterizedTest
    @CsvSource({
            "NZ, 4",
            "Riccarton, 4",
            "Christchurch, 4",
            "Aus, 0"
    })
    void whenFilterByLocation_thenNumberMatchesReturnedIs(String location, Integer numResults) {
        Page<Listing> listings = listingsService.searchListings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(location), Optional.empty(), Optional.empty(),Pageable.unpaged());
        List<String> names = listings.map(listing -> listing.getInventoryItem().getProduct().getName()).getContent();
        assertEquals(numResults, names.size());
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

    //
    //  Get Listing by Id
    //

    @Test
    void whenGetListingById_andListingExists_thenListingReturned() {
        Assertions.assertNotNull(listingsService.findFirstById(1));
    }

    @Test
    void whenGetListingById_andListingNotExists_then406Thrown() {
        Assertions.assertThrows(ResponseStatusException.class, () -> listingsService.findFirstById(666));
    }

    //
    //  Purchase Listing
    //

    @Test
    void whenPurchaseListing_andListingQuantityLessThanInventoryQuantity_thenInventoryQuantityDecreased() {
        var listing = TestUtils.createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Yoonique", 100.0, "NZ", "Christchurch", "Riccarton", "Fraud", BusinessTypes.CHARITABLE_ORGANISATION, LocalDate.of(2099, Month.MARCH, 10),
                4, 5);
        Integer inventoryId = listing.getInventoryItem().getId();
        listingsService.purchase(listing, curUser);
        assertEquals(1, inventoryService.findInventoryById(inventoryId).getQuantity());
        assertEquals(1, inventoryService.findInventoryById(inventoryId).getQuantityUnlisted());

        var listingId = listing.getId();
        assertThrows(ResponseStatusException.class, () -> listingsService.findFirstById(listingId));
    }

    @Test
    void whenPurchaseListing_andListingQuantityEqualToInventoryQuantity_thenInventoryQuantitySetToZero() {
        var listing = TestUtils.createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Yoonique", 100.0, "NZ", "Christchurch", "Riccarton", "Fraud", BusinessTypes.CHARITABLE_ORGANISATION, LocalDate.of(2099, Month.MARCH, 10),
                5, 5);
        Integer inventoryId = listing.getInventoryItem().getId();
        listingsService.purchase(listing, curUser);
        assertEquals(0, inventoryService.findInventoryById(inventoryId).getQuantity());
        assertEquals(0, inventoryService.findInventoryById(inventoryId).getQuantityUnlisted());
    }

    @Test
    void whenPurchaseListing_andListingHasNoLikes_thenPurchaseListingRecordCreated() {
        var listing = TestUtils.createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Yoonique", 100.0, "NZ", "Christchurch", "Riccarton", "Fraud", BusinessTypes.CHARITABLE_ORGANISATION, LocalDate.of(2099, Month.MARCH, 10),
                5, 5);

        PurchasedListing purchasedListing = listingsService.purchase(listing, curUser);

        Assertions.assertEquals(listing.getCreated(), purchasedListing.getListingDate());
        Assertions.assertEquals(listing.getInventoryItem().getProduct().getId(), purchasedListing.getProduct().getId());
        Assertions.assertEquals(listing.getQuantity(), purchasedListing.getQuantity());
        Assertions.assertEquals(listing.getPrice(), purchasedListing.getPrice());
        Assertions.assertEquals(curUser.getId(), purchasedListing.getPurchaser().getId());
        Assertions.assertNotNull(purchasedListing.getSaleDate());
    }

    @Test
    void whenPurchaseListing_andListingHasOneLike_thenLikeRecordedInPurchaseListingRecord() {
        var listing = TestUtils.createListingWithNameAndPrice(this.productService, this.inventoryService, this.listingsService, this.businessService, this.addressService, "Yoonique", 100.0, "NZ", "Christchurch", "Riccarton", "Fraud", BusinessTypes.CHARITABLE_ORGANISATION, LocalDate.of(2099, Month.MARCH, 10),
                5, 5);

        PurchasedListing purchasedListing = listingsService.purchase(listing, curUser);

        Assertions.assertEquals(listing.getUsersLiked(), purchasedListing.getNumberOfLikes());
    }
}
