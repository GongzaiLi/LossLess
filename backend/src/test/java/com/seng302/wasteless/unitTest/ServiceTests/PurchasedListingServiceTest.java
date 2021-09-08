package com.seng302.wasteless.unitTest.ServiceTests;

import com.seng302.wasteless.TestUtils;
import com.seng302.wasteless.dto.SalesReportDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import com.seng302.wasteless.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)     // Allows non-static BeforeAll methods. baeldung.com/java-beforeall-afterall-non-static
public class PurchasedListingServiceTest {

    private static final Logger logger = LogManager.getLogger(com.seng302.wasteless.controller.SalesReportController.class.getName());


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

    private User curUser;


    @BeforeAll
    void setUp() {
        curUser = newUserWithEmail("big@dave");
        addressService.createAddress(curUser.getHomeAddress());
        curUser = userService.createUser(curUser);

        Business business = new Business();
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setAdministrators(new ArrayList<>());
        business.setName("Wonka Water");
        business.setAddress(curUser.getHomeAddress());
        business.setCreated(LocalDate.now());
        businessService.createBusiness(business);

        Listing listing1 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 1.0, LocalDate.of(2099, Month.JANUARY, 1), 1, 1);
        Listing listing2 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 2.0, LocalDate.of(2099, Month.JANUARY, 1), 1, 1);
        Listing listing3 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 5.0, LocalDate.of(2099, Month.JANUARY, 1), 1, 1);
        Listing listing4 = TestUtils.createListingForSameBusiness(this.productService, this.inventoryService, this.listingsService, business, "Black Water No Sugar", 15.0, LocalDate.of(2099, Month.JANUARY, 1),1, 1);

        listingsService.purchase(listing1, curUser);
        listingsService.purchase(listing2, curUser);
        listingsService.purchase(listing3, curUser);
        listingsService.purchase(listing4, curUser);


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

}