package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.TestUtils;
import com.seng302.wasteless.dto.SalesReportDto;
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
        List<SalesReportDto> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(1, LocalDate.now(), LocalDate.now().plusMonths(2), LocalDate.now().minusDays(1), LocalDate.now().plusMonths(2), Period.ofMonths(1));
        assertEquals(3, salesReportData.size());
    }

    @Test
    void whenGetSalesReportData_andPeriodIsYearIn4YearDateRange_thenReturnedDataHasLength4() {
        List<SalesReportDto> salesReportData = purchasedListingService.getSalesReportDataWithPeriod(1, LocalDate.now(), LocalDate.now().plusYears(3), LocalDate.now(), LocalDate.now().plusYears(3), Period.ofYears(1));
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
            Assertions.assertFalse(purchasedListing.getSaleDate().isAfter(purchasedListing.getClosingDate()));
        }
    }

}
