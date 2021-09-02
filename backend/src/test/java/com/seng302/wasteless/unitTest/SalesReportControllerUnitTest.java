package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.controller.SalesReportController;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.BusinessTypes;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SalesReportController.class)
@AutoConfigureMockMvc(addFilters = false) //Disable spring security for the unit tests
public class SalesReportControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private ProductService productService;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private ListingsService listingsService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private PurchasedListingService purchasedListingService;

    private Business business;

    private User user;


    @BeforeEach
    void setUp() {

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

        Mockito
                .when(authentication.getName())
                .thenReturn("james@gmail.com");

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(business);

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);



        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(business).checkUserIsAdministrator(user);
        doReturn(LocalDate.now()).when(business).getCreated();


        //Returns the totalProducts value equal to the businessId that was passed
        when(purchasedListingService.countPurchasedListingForBusiness(anyInt())).thenAnswer(i -> i.getArguments()[0]);

        //Returns the totalProducts value equal to the businessId that was passed
        when(purchasedListingService.countPurchasedListingForBusinessInDateRange(anyInt(), any(LocalDate.class), any(LocalDate.class))).thenAnswer(i -> i.getArguments()[0]);
        when(purchasedListingService.totalPurchasedListingValueForBusinessInDateRange(anyInt(), any(LocalDate.class), any(LocalDate.class))).thenAnswer(i -> 5.0);
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andValidBusiness_Then200Response() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].totalPurchases", is(5)))
                .andExpect(jsonPath("[0].totalValue", is(5.0)));
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

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenGetSaleReportTotalCount_andBusinessDoesntExist_then403Response() throws Exception {

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business with given ID does not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases?period=day")
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

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/5/salesReport/totalPurchases?startDate=2000-10-12&endDate=2000-10-14&period=day")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].totalPurchases", is(5)))
                .andExpect(jsonPath("[0].totalValue", is(5.0)))
                .andExpect(jsonPath("[1].totalPurchases", is(5)))
                .andExpect(jsonPath("[1].totalValue", is(5.0)))
                .andExpect(jsonPath("[2].totalPurchases", is(5)))
                .andExpect(jsonPath("[2].totalValue", is(5.0)))
                .andExpect(jsonPath("$.length()", is(3)));
    }

}
