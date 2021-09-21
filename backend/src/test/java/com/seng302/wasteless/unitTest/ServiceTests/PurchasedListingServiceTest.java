package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.TestUtils;
import com.seng302.wasteless.dto.SalesReportDto;
import com.seng302.wasteless.dto.SalesReportManufacturerTotalsDto;
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
import java.util.*;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
        // Allows non-static BeforeAll methods. baeldung.com/java-beforeall-afterall-non-static
class PurchasedListingServiceTest {
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
    private Business business2;

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

        listing1 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 1.0, LocalDate.of(2022, Month.JANUARY, 1), 1, 1);
        Listing listing2 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 2.0, LocalDate.of(2022, Month.JANUARY, 2), 1, 1);
        Listing listing3 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 5.0, LocalDate.of(2022, Month.SEPTEMBER, 15), 1, 1);
        Listing listing4 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 15.0, LocalDate.of(2022, Month.JANUARY, 1),1, 1);
        Listing listing5 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 5.0, LocalDate.of(2022, Month.SEPTEMBER, 15), 1, 1);

        purchasedListingRepository.save(listingsService.purchase(listing1, curUser).setSaleDate(LocalDate.of(2021, Month.FEBRUARY, 1)));
        purchasedListingRepository.save(listingsService.purchase(listing2, curUser).setSaleDate(LocalDate.of(2021, Month.FEBRUARY, 2)));
        purchasedListingRepository.save(listingsService.purchase(listing3, curUser).setSaleDate(LocalDate.of(2022, Month.SEPTEMBER, 15)));
        purchasedListingRepository.save(listingsService.purchase(listing4, curUser).setSaleDate(LocalDate.of(2020, Month.FEBRUARY, 29)));
        purchasedListingRepository.save(listingsService.purchase(listing5, curUser).setSaleDate(LocalDate.of(2022, Month.SEPTEMBER, 14)));

        business2 = new Business();
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
        product2.setManufacturer("Not James");
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
        List<SalesReportSinglePeriod> salesReportData = purchasedListingService.getSalesReportDataNoPeriod(business.getId(), LocalDate.now().minusDays(3), LocalDate.now().plusDays(3));
        assertEquals(1, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsDayIn5DayDateRange_thenReturnedDataHasLength5() {
        List<SalesReportSinglePeriod> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(business.getId(), LocalDate.now(), LocalDate.now().plusDays(4), LocalDate.now(), LocalDate.now().plusDays(4), Period.ofDays(1));
        assertEquals(5, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsWeekIn7WeekDateRange_thenReturnedDataHasLength7() {
        List<SalesReportSinglePeriod> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(business.getId(), LocalDate.now(), LocalDate.now().plusWeeks(6), LocalDate.now(), LocalDate.now().plusWeeks(6), Period.ofWeeks(1));
        assertEquals(7, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsMonthIn3MonthDateRange_thenReturnedDataHasLength3() {
        List<SalesReportSinglePeriod> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(business.getId(), LocalDate.now(), LocalDate.now().plusMonths(2), LocalDate.now().minusDays(1), LocalDate.now().plusMonths(2), Period.ofMonths(1));
        assertEquals(3, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsYearIn4YearDateRange_thenReturnedDataHasLength4() {
        List<SalesReportSinglePeriod> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(business.getId(), LocalDate.now(), LocalDate.now().plusYears(3), LocalDate.now(), LocalDate.now().plusYears(3), Period.ofYears(1));
        assertEquals(4, salesReportData.size());
    }

    @Test
    void whenCountSalesByDuration_andAllSalesWithinDuration_thenCorrectCountsReturned() {
        Map<Long, Integer> salesReportData = purchasedListingService.countSalesByDurationBetweenSaleAndClose(1, LocalDate.now().minusYears(3), LocalDate.now().plusYears(3));
        Assertions.assertEquals(1, salesReportData.get(0L));
        Assertions.assertEquals(1, salesReportData.get(1L));
        Assertions.assertEquals(1, salesReportData.get(672L));
        Assertions.assertEquals(2, salesReportData.get(334L));

        Assertions.assertEquals(new HashSet<>(Arrays.asList(672L, 0L, 1L, 334L)), salesReportData.keySet());
    }

    @Test
    void whenCountSalesByDuration_andTwoSalesWithinDuration_thenCountOfTwoReturned() {
        Map<Long, Integer> salesReportData = purchasedListingService.countSalesByDurationBetweenSaleAndClose(1, LocalDate.of(2021, Month.FEBRUARY, 1), LocalDate.of(2021, Month.FEBRUARY, 2));
        Assertions.assertEquals(2, salesReportData.get(334L));

        Assertions.assertEquals(new HashSet<>(Collections.singletonList(334L)), salesReportData.keySet());
    }

    @Test
    void whenCountSalesByDuration_andNoSalesWithinDuration_thenNoCountsReturned() {
        Map<Long, Integer> salesReportData = purchasedListingService.countSalesByDurationBetweenSaleAndClose(1, LocalDate.now().minusYears(3), LocalDate.now().minusYears(3));

        Assertions.assertTrue(salesReportData.isEmpty());
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
            Assertions.assertTrue(purchasedListing.getClosingDate().toLocalDate().isAfter(purchasedListing.getSaleDate()));
        }
    }

    @Test
    void whenGetProductsPurchasedTotals_thenCorrectNumberOfDtosReturned() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(business2.getId(), null, Sort.Direction.ASC);
        assertEquals(3, purchasedTotalsData.size());
    }

    @Test
    void whenGetProductsPurchasedTotals_thenCorrectNumberOfTotalProductPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(business2.getId(), null, Sort.Direction.ASC);
        assertEquals(8, purchasedTotalsData.get(0).getTotalProductPurchases());
        assertEquals(16, purchasedTotalsData.get(1).getTotalProductPurchases());
    }

    @Test
    void whenGetProductsPurchasedTotals_thenCorrectTotalValueOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(business2.getId(), null, Sort.Direction.ASC);
        assertEquals(6, purchasedTotalsData.get(0).getTotalValue());
        assertEquals(12, purchasedTotalsData.get(1).getTotalValue());
    }

    @Test
    void whenGetProductsPurchasedTotals_thenCorrectNumberOfLikesOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(business2.getId(), null, Sort.Direction.ASC);
        assertEquals(8, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(16, purchasedTotalsData.get(1).getTotalLikes());
    }

    @Test
    void whenGetProductsPurchasedTotalsAndSortQuantity_ASC_thenCorrectNumberOfTotalProductPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(business2.getId(), "quantity", Sort.Direction.ASC);
        assertEquals(8, purchasedTotalsData.get(0).getTotalProductPurchases());
        assertEquals(10, purchasedTotalsData.get(1).getTotalProductPurchases());
        assertEquals(16, purchasedTotalsData.get(2).getTotalProductPurchases());
    }

    @Test
    void whenGetProductsPurchasedTotalAndSortValue_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(business2.getId(), "value", Sort.Direction.ASC);
        assertEquals(1, purchasedTotalsData.get(0).getTotalValue());
        assertEquals(6, purchasedTotalsData.get(1).getTotalValue());
        assertEquals(12, purchasedTotalsData.get(2).getTotalValue());
    }

    @Test
    void whenGetProductsPurchasedTotalsAndSortLikes_ASC_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(business2.getId(), "likes", Sort.Direction.ASC);
        assertEquals(8, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(10, purchasedTotalsData.get(1).getTotalLikes());
        assertEquals(16, purchasedTotalsData.get(2).getTotalLikes());
    }

    @Test
    void whenGetProductsPurchasedTotalsAndSortLikes_DESC_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportProductTotalsDto> purchasedTotalsData = purchasedListingService.getProductsPurchasedTotals(business2.getId(), "likes", Sort.Direction.DESC);
        assertEquals(16, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(10, purchasedTotalsData.get(1).getTotalLikes());
        assertEquals(8, purchasedTotalsData.get(2).getTotalLikes());

    }

    @Test
    void whenGetManufacturersPurchasedTotals_thenCorrectNumberOfDtosReturned() {
        List<SalesReportManufacturerTotalsDto> purchasedTotalsData = purchasedListingService.getManufacturersPurchasedTotals(business2.getId(), null, Sort.Direction.ASC);
        assertEquals(2, purchasedTotalsData.size());
    }

    @Test
    void whenGetManufacturersPurchasedTotals_thenCorrectNumberOfTotalManufacturerPurchasesInDtos() {
        List<SalesReportManufacturerTotalsDto> purchasedTotalsData = purchasedListingService.getManufacturersPurchasedTotals(business2.getId(), null, Sort.Direction.ASC);
        assertEquals(18, purchasedTotalsData.get(0).getTotalProductPurchases());
        assertEquals(16, purchasedTotalsData.get(1).getTotalProductPurchases());
    }

    @Test
    void whenGetManufacturersPurchasedTotals_thenCorrectTotalValueOfPurchasesInDtos() {
        List<SalesReportManufacturerTotalsDto> purchasedTotalsData = purchasedListingService.getManufacturersPurchasedTotals(business2.getId(), null, Sort.Direction.ASC);
        assertEquals(7, purchasedTotalsData.get(0).getTotalValue());
        assertEquals(12, purchasedTotalsData.get(1).getTotalValue());
    }

    @Test
    void whenGetManufacturersPurchasedTotals_thenCorrectNumberOfLikesOfPurchasesInDtos() {
        List<SalesReportManufacturerTotalsDto> purchasedTotalsData = purchasedListingService.getManufacturersPurchasedTotals(business2.getId(), null, Sort.Direction.ASC);
        assertEquals(18, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(16, purchasedTotalsData.get(1).getTotalLikes());
    }

    @Test
    void whenGetManufacturersPurchasedTotalsAndSortQuantity_ASC_thenCorrectNumberOfTotalManufacturerPurchasesInDtos() {
        List<SalesReportManufacturerTotalsDto> purchasedTotalsData = purchasedListingService.getManufacturersPurchasedTotals(business2.getId(), "quantity", Sort.Direction.ASC);
        assertEquals(16, purchasedTotalsData.get(0).getTotalProductPurchases());
        assertEquals(18, purchasedTotalsData.get(1).getTotalProductPurchases());
    }

    @Test
    void whenGetManufacturersPurchasedTotalAndSortValue_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportManufacturerTotalsDto> purchasedTotalsData = purchasedListingService.getManufacturersPurchasedTotals(business2.getId(), "value", Sort.Direction.ASC);
        assertEquals(7, purchasedTotalsData.get(0).getTotalValue());
        assertEquals(12, purchasedTotalsData.get(1).getTotalValue());
    }

    @Test
    void whenGetManufacturersPurchasedTotalsAndSortLikes_ASC_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportManufacturerTotalsDto> purchasedTotalsData = purchasedListingService.getManufacturersPurchasedTotals(business2.getId(), "likes", Sort.Direction.ASC);
        assertEquals(16, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(18, purchasedTotalsData.get(1).getTotalLikes());
    }

    @Test
    void whenGetManufacturersPurchasedTotalsAndSortLikes_DESC_thenCorrectOrderOfPurchasesInDtos() {
        List<SalesReportManufacturerTotalsDto> purchasedTotalsData = purchasedListingService.getManufacturersPurchasedTotals(business2.getId(), "likes", Sort.Direction.DESC);
        assertEquals(18, purchasedTotalsData.get(0).getTotalLikes());
        assertEquals(16, purchasedTotalsData.get(1).getTotalLikes());

    }

}
