package com.seng302.wasteless.unitTest.ControllerTests;

import com.seng302.wasteless.controller.SalesReportController;
import com.seng302.wasteless.dto.SalesReportDto;
import com.seng302.wasteless.dto.SalesReportProductTotalsDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SalesReportController.class)
@AutoConfigureMockMvc(addFilters = false) //Disable spring security for the unit tests
class SalesReportControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private PurchasedListingService purchasedListingService;

    private Business business;

    private User user;


    @BeforeEach
    void setUp() throws IOException {

        business = mock(Business.class);
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1);
        business.setAdministrators(new ArrayList<>());
        business.setName("Jimmy's clown store");
        business.setCreated(LocalDate.now());

        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);


        List<SalesReportSinglePeriod> salesData = new ArrayList<>();
        List<SalesReportProductTotalsDto> salesPurchaseTotalsData = new ArrayList<>();

        Mockito
                .when(authentication.getName())
                .thenReturn("james@gmail.com");

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(business);

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        Mockito
                .when(purchasedListingService.getSalesReportDataNoPeriod(anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(salesData);

        Mockito
                .when(purchasedListingService.getSalesReportDataWithPeriod(anyInt(), any(LocalDate.class), any(LocalDate.class),
                        any(LocalDate.class), any(LocalDate.class), any(Period.class)))
                .thenReturn(salesData);

        Mockito
                .when(purchasedListingService.getProductsPurchasedTotals(anyInt(), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(salesPurchaseTotalsData);

        InputStream stubInputStream =
                IOUtils.toInputStream("This is not actually a CSV file", "UTF-8");

        Mockito
                .when(purchasedListingService.getSalesReportCSVByteSteam(anyInt()))
                .thenReturn((ByteArrayInputStream) stubInputStream);


        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(business).checkUserIsAdministrator(user);
        doReturn(LocalDate.now()).when(business).getCreated();
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andValidBusiness_Then200Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andLoggedInUser_butNotBusinessAdminOrAppAdmin_then403Response() throws Exception {

        doReturn(false).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(false).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(false).when(business).checkUserIsAdministrator(user);
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to make this request")).when(businessService).checkUserAdminOfBusinessOrGAA(business, user);

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andNotLoggedIn_then401Response() throws Exception {

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session token is invalid"));


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/businesses/1/salesReport/totalPurchases?period=day", "/businesses/99/salesReport/productsPurchasedTotals", "/businesses/99/salesReport/manufacturersPurchasedTotals"})
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportEndpoints_andBusinessDoesntExist_then406Response(String request) throws Exception {

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business with given ID does not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get(request)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andStartDateNull_andEndDateNotNull_then400Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?endDate=2000-10-12&period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andStartDateNotNull_andEndDateNull_then400Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?startDate=2000-10-12&period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andNegativeDateRange_then400Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?startDate=2000-10-12&endDate=1999-10-12&period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andValidDateRange_then200Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?startDate=1999-10-12&endDate=2000-10-12&period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andSameDayDateRange_then200Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?startDate=2000-10-12&endDate=2000-10-12&period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andThreeDayRange_then200ResponseWithThreeDaysData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/3/salesReport/totalPurchases?startDate=2000-10-12&endDate=2000-10-14&period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andFiveWeekRange_then200ResponseWithFiveWeeksData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?startDate=2021-09-01&endDate=2021-10-06&period=week")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andFiveMonthRange_then200ResponseWithFiveMonthsData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?startDate=2000-10-01&endDate=2001-02-11&period=month")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andSundayToMonday_then200ResponseWithTwoWeeksData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/2/salesReport/totalPurchases?startDate=2021-09-05&endDate=2021-09-06&period=week")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andLastDayToFirstDayOfMonth_then200ResponseWithTwoMonthsData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/2/salesReport/totalPurchases?startDate=2021-09-31&endDate=2021-10-01&period=month")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andFiveYearRange_then200ResponseWithFiveYearsData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?startDate=2000-10-01&endDate=2004-02-11&period=year")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andLastDayToFirstDayOfYear_then200ResponseWithTwoYearsData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/2/salesReport/totalPurchases?startDate=2021-12-31&endDate=2022-01-01&period=year")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andNoPeriodSet_then200ResponseWithTotalDataForRange() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?startDate=2021-12-01&endDate=2022-01-31")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andLeapDayChosenOnNonLeapYear_then200Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?startDate=2021-02-29&endDate=2021-10-01&period=month")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andLeapDayChosenOnLeapYear_then200Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?startDate=2020-02-29&endDate=2021-10-01&period=month")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andPeriodIsAasdsad_then400Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?startDate=2020-02-29&endDate=2021-10-01&period=asdasd")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductPurchasedTotals_andBusinessDoesntExist_then406Response() throws Exception{

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business with given ID does not exist"));


        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/99/salesReport/productsPurchasedTotals")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }



    /**
     * Test 1: products purchased
     * Test 2: manufacturers purchased
     * Test 3: csv
     * @param request
     * @throws Exception
     */
    @ParameterizedTest
    @ValueSource(strings = {"/businesses/1/salesReport/productsPurchasedTotals", "/businesses/1/salesReport/manufacturersPurchasedTotals", "/businesses/1/salesReport/csv"})
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportEndpointsandUserNotAllowedToAccessInformationOfBusiness_then403Response(String request) throws Exception{

        Mockito
                .when(businessService.checkUserAdminOfBusinessOrGAA(any(Business.class), any(User.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to make this request"));

        mockMvc.perform(MockMvcRequestBuilders.get(request)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_then200Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_SortByValue_then200Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?sortBy=value")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_SortByIsInvalid_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?sortBy=invalid")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_SortByAndOrderCorrect_then200Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?sortBy=quantity&order=ASC")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_OrderSpecifiedWithoutSort_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?order=ASC")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_OrderIsInvalid_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?sortBy=quantity&order=invalid")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }




    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportManufacturerPurchasedTotals_andValidRequest_then200Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/manufacturersPurchasedTotals")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportManufacturerPurchasedTotals_andValidRequest_SortByValue_then200Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/manufacturersPurchasedTotals?sortBy=value")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportManufacturerPurchasedTotals_andValidRequest_SortByIsInvalid_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/manufacturersPurchasedTotals?sortBy=invalid")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportManufacturerPurchasedTotals_andValidRequest_SortByAndOrderCorrect_then200Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/manufacturersPurchasedTotals?sortBy=quantity&order=ASC")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductManufacturerTotals_andValidRequest_OrderSpecifiedWithoutSort_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/manufacturersPurchasedTotals?order=ASC")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductManufacturerTotals_andValidRequest_OrderIsInvalid_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/manufacturersPurchasedTotals?sortBy=quantity&order=invalid")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCountSalesByDuration_andCountsValid_thenCorrectCountsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/listingDurations?startDate=2020-02-29&endDate=2021-10-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenCountSalesByDuration_andStartDateNotProvided_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/listingDurations?endDate=2021-10-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCountSalesByDuration_andNoDatesProvided_then400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/listingDurations")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_andInvalidPeriodProvided_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/2/salesReport/totalPurchases?period=invalid")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_OrderSpecifiedWithoutStartDate_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?endDate=2021-10-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_OrderSpecifiedWithoutEndDate_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?startDate=2021-10-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_AndStartDateAfterEndDate_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?startDate=2021-10-01&endDate=2020-10-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_AndStartDateBeforeEndDate_then400Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?startDate=2021-10-01&endDate=2022-10-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_OrderSpecifiedWithoutAnyDates_then200Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportProductPurchasedTotals_andValidRequest_then200Response_withValidData() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/productsPurchasedTotals?startDate=2021-10-01&endDate=2020-10-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSalesReport_andValidReportType_then404Response() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/reportthatdoesntexist")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportCSV_andValidRequest_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/csv")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportCSV_andUserUnauthorised_then401Response() throws Exception {
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session token is invalid"));

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/csv")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }



    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportCSV_andBusinessDoesNotExist_then406Response() throws Exception {
        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business with given ID does not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/csv")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetSalesReportCSV_andValidRequest_thenCorrectHeadersWithResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/csv")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=salesReport.csv"));
    }
}
