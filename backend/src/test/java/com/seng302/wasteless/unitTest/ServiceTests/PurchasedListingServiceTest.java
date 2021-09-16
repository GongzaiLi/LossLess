package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.TestUtils;
import com.seng302.wasteless.dto.SalesReportDto;
import com.seng302.wasteless.dto.SalesReportProductTotalsDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import com.seng302.wasteless.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)     // Allows non-static BeforeAll methods. baeldung.com/java-beforeall-afterall-non-static
public class PurchasedListingServiceTest {
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

    @Autowired
    private PurchasedListingService purchasedListingService;

    @Autowired
    private PurchasedListingRepository purchasedListingRepository;

    private Listing listing1;

    private Business business;

    private User curUser;


    @BeforeAll
    void setUp() {
        curUser = newUserWithEmail("big@dave");
        addressService.createAddress(curUser.getHomeAddress());
        curUser = userService.createUser(curUser);

        business = new Business();
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setAdministrators(new ArrayList<>());
        business.setName("Wonka Water");
        business.setAddress(curUser.getHomeAddress());
        business.setCreated(LocalDate.now());
        businessService.createBusiness(business);

        listing1 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 1.0, LocalDate.of(2099, Month.JANUARY, 1), 1, 1);
        Listing listing2 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 2.0, LocalDate.of(2099, Month.JANUARY, 1), 1, 1);
        Listing listing3 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 5.0, LocalDate.of(2099, Month.JANUARY, 1), 1, 1);
        Listing listing4 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 15.0, LocalDate.of(2099, Month.JANUARY, 1),1, 1);

        listingsService.purchase(listing1, curUser);
        listingsService.purchase(listing2, curUser);
        listingsService.purchase(listing3, curUser);
        listingsService.purchase(listing4, curUser);

        Business business2 = new Business();
        business2.setBusinessType(BusinessTypes.RETAIL_TRADE);
        business2.setAdministrators(new ArrayList<>());
        business2.setName("Wonka Milk");
        business2.setAddress(curUser.getHomeAddress());
        business2.setCreated(LocalDate.now());
        businessService.createBusiness(business2);

        Product product1 = new Product();
        product1.setId("Clown-Shows");
        product1.setBusinessId(2);
        product1.setName("Clown Shows");
        product1.setCreated(LocalDate.now());
        product1.setDescription("For you feet");
        product1.setManufacturer("James");
        product1.setRecommendedRetailPrice(1.0);
        productService.createProduct(product1);

        Product product2 = new Product();
        product2.setId("Clown-Hats");
        product2.setBusinessId(2);
        product2.setName("Clown Hats");
        product2.setCreated(LocalDate.now());
        product2.setDescription("For you head");
        product2.setManufacturer("James");
        product2.setRecommendedRetailPrice(2.0);
        productService.createProduct(product2);

        Product product3 = new Product();
        product3.setId("Clown-Nose");
        product3.setBusinessId(2);
        product3.setName("Clown Nose");
        product3.setCreated(LocalDate.now());
        product3.setDescription("For you Face");
        product3.setManufacturer("James");
        product3.setRecommendedRetailPrice(3.0);
        productService.createProduct(product3);

        Listing listingWith5Quantity5LikesForProduct1 = TestUtils.createListingForSameProductAndBusinessWithLikes(this.inventoryService, this.listingsService, product1, business2, 3.0, LocalDate.of(2099, Month.JANUARY, 1), 5, 10, 5);
        Listing listingWith2Quantity2LikesForProduct1 = TestUtils.createListingForSameProductAndBusinessWithLikes(this.inventoryService, this.listingsService, product1, business2, 2.0, LocalDate.of(2099, Month.JANUARY, 1), 2, 10, 2);
        Listing listingWith1Quantity1LikesForProduct1 = TestUtils.createListingForSameProductAndBusinessWithLikes(this.inventoryService, this.listingsService, product1, business2, 1.0, LocalDate.of(2099, Month.JANUARY, 1), 1, 10, 1);

        Listing listingWith10Quantity10LikesForProduct2 = TestUtils.createListingForSameProductAndBusinessWithLikes(this.inventoryService, this.listingsService, product2, business2, 5.0, LocalDate.of(2099, Month.JANUARY, 1), 10, 10, 10);
        Listing listingWith5Quantity5LikesForProduct2 = TestUtils.createListingForSameProductAndBusinessWithLikes(this.inventoryService, this.listingsService, product2, business2, 4.0, LocalDate.of(2099, Month.JANUARY, 1), 5, 10, 5);
        Listing listingWith1Quantity1LikesForProduct2 = TestUtils.createListingForSameProductAndBusinessWithLikes(this.inventoryService, this.listingsService, product2, business2, 3.0, LocalDate.of(2099, Month.JANUARY, 1), 1, 10, 1);

        Listing listingWith10Quantity10LikesForProduct3 = TestUtils.createListingForSameProductAndBusinessWithLikes(this.inventoryService, this.listingsService, product3, business2, 1.0, LocalDate.of(2099, Month.JANUARY, 1), 10, 10, 10);

        listingsService.purchase(listingWith5Quantity5LikesForProduct1, curUser);
        listingsService.purchase(listingWith2Quantity2LikesForProduct1, curUser);
        listingsService.purchase(listingWith1Quantity1LikesForProduct1, curUser);
        listingsService.purchase(listingWith10Quantity10LikesForProduct2, curUser);
        listingsService.purchase(listingWith5Quantity5LikesForProduct2, curUser);
        listingsService.purchase(listingWith1Quantity1LikesForProduct2, curUser);
        listingsService.purchase(listingWith10Quantity10LikesForProduct3, curUser);

    }


    @Test
    void whenGetSalesReportData_andNoPeriodSet_thenReturnedDataHasLength1() {
        List<SalesReportDto> salesReportData = purchasedListingService.getSalesReportDataNoPeriod(1, LocalDate.now().minusDays(3), LocalDate.now().plusDays(3));
        assertEquals(1, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsDayIn5DayDateRange_thenReturnedDataHasLength5() {
        List<SalesReportDto> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(1, LocalDate.now(), LocalDate.now().plusDays(4), LocalDate.now(), LocalDate.now().plusDays(4), Period.ofDays(1));
        assertEquals(5, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsWeekIn7WeekDateRange_thenReturnedDataHasLength7() {
        List<SalesReportDto> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(1, LocalDate.now(), LocalDate.now().plusWeeks(6), LocalDate.now(), LocalDate.now().plusWeeks(6), Period.ofWeeks(1));
        assertEquals(7, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsMonthIn3MonthDateRange_thenReturnedDataHasLength3() {
        List<SalesReportDto> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(1, LocalDate.now(), LocalDate.now().plusMonths(2), LocalDate.now(), LocalDate.now().plusMonths(2), Period.ofMonths(1));
        assertEquals(3, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsYearIn4YearDateRange_thenReturnedDataHasLength4() {
        List<SalesReportDto> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(1, LocalDate.now(), LocalDate.now().plusYears(3), LocalDate.now(), LocalDate.now().plusYears(3), Period.ofYears(1));
        assertEquals(4, salesReportData.size());
    }

    @Test
    void whenGeneratePurchasesForProduct_andBusinessCreatedIsNow_thenAllPurchasesHaveDatesInOrder_andBusinessCreatedIsPast() {
        // Creates a new business
        business.setId(420);
        business.setCreated(LocalDate.now());
        business = businessService.createBusiness(business);
        purchasedListingService.generatePurchasesForProduct(listing1.getInventoryItem().getProduct(), curUser, business);

        List<PurchasedListing> generated = purchasedListingRepository.findAllByBusinessId(business.getId());
        for (PurchasedListing purchasedListing : generated) {
            Assertions.assertFalse(business.getCreated().isAfter(purchasedListing.getListingDate()));
            Assertions.assertFalse(purchasedListing.getListingDate().isAfter(purchasedListing.getSaleDate()));
            Assertions.assertFalse(purchasedListing.getSaleDate().isAfter(purchasedListing.getClosingDate()));
        }
    }

    @Test
    void whenGetProductsPurchasedTotals_thenCorrectNumberOfDtosReturned() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(2, null, Sort.Direction.ASC);
        assertEquals(3, purchasedTotalsData.size());
    }

    @Test
    void whenGetProductsPurchasedTotals_thenCorrectNumberOfTotalProductPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(2, null, Sort.Direction.ASC);
        assertEquals(8, purchasedTotalsData.get(0).getTotalProductPurchases());
        assertEquals(16, purchasedTotalsData.get(1).getTotalProductPurchases());
    }

    @Test
    void whenGetProductsPurchasedTotals_thenCorrectTotalValueOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(2, null, Sort.Direction.ASC);
        assertEquals(6, purchasedTotalsData.get(0).getTotalValue());
        assertEquals(12, purchasedTotalsData.get(1).getTotalValue());
    }

    @Test
    void whenGetProductsPurchasedTotals_thenCorrectNumberOfLikesOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(2, null, Sort.Direction.ASC);
        assertEquals(8, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(16, purchasedTotalsData.get(1).getTotalLikes());
    }

    @Test
    void whenGetProductsPurchasedTotalsAndSortQuantity_ASC_thenCorrectNumberOfTotalProductPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(2, "quantity", Sort.Direction.ASC);
        assertEquals(8, purchasedTotalsData.get(0).getTotalProductPurchases());
        assertEquals(10, purchasedTotalsData.get(1).getTotalProductPurchases());
        assertEquals(16, purchasedTotalsData.get(2).getTotalProductPurchases());
    }

    @Test
    void whenGetProductsPurchasedTotalAndSortValue_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(2, "value", Sort.Direction.ASC);
        assertEquals(1, purchasedTotalsData.get(0).getTotalValue());
        assertEquals(6, purchasedTotalsData.get(1).getTotalValue());
        assertEquals(12, purchasedTotalsData.get(2).getTotalValue());
    }

    @Test
    void whenGetProductsPurchasedTotalsAndSortLikes_ASC_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(2, "likes", Sort.Direction.ASC);
        assertEquals(8, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(10, purchasedTotalsData.get(1).getTotalLikes());
        assertEquals(16, purchasedTotalsData.get(2).getTotalLikes());
    }

    @Test
    void whenGetProductsPurchasedTotalsAndSortLikes_DESC_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(2, "likes", Sort.Direction.DESC);
        assertEquals(16, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(10, purchasedTotalsData.get(1).getTotalLikes());
        assertEquals(8, purchasedTotalsData.get(2).getTotalLikes());

    }

}
