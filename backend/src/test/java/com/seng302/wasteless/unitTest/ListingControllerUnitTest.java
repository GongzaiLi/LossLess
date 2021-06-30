package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.InventoryController;
import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.dto.mapper.PostInventoryDtoMapper;
import com.seng302.wasteless.dto.mapper.PostListingsDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ListingController.class)
@AutoConfigureMockMvc(addFilters = false) //Disable spring security for the unit tests
public class ListingControllerUnitTest {

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

    private Business business;

    private User user;

    private Listing listing;

    @BeforeAll
    static void beforeAll() {
        //This line is important, do not remove'
        mockStatic(PostListingsDtoMapper.class);
    }

    @BeforeEach
    void setUp() {
        Product productForInventory = new Product();
        productForInventory.setId("Clown-Shows");
        productForInventory.setBusinessId(1);
        productForInventory.setName("Clown Shows");

        LocalDate expiry = LocalDate.of(2022, Month.JULY, 14);

        Inventory inventoryItemForListing = new Inventory();
        inventoryItemForListing.setExpires(expiry)
                .setTotalPrice(50)
                .setPricePerItem(10)
                .setSellBy(expiry.minusMonths(2))
                .setQuantity(5)
                .setManufactured(expiry.minusMonths(4))
                .setBestBefore(expiry.minusMonths(1))
                .setProduct(productForInventory);


        LocalDate closes = inventoryItemForListing.getExpires();

        listing = new Listing();
        listing.setInventoryItem(inventoryItemForListing)
                .setCreated(expiry.minusMonths(3))
                .setQuantity(3)
                .setPrice(17.99)
                .setMoreInfo("Seller may be willing to consider near offers")
                .setCloses(closes);


        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);

        business = mock(Business.class);
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1);
        business.setAdministrators(new ArrayList<>());
        business.setName("Jimmy's clown store");

        Product product = new Product();
        product.setId("Clown-Shows");
        product.setBusinessId(1);
        product.setName("Clown Shows");

        Mockito
                .when(authentication.getName())
                .thenReturn("james@gmail.com");

        Mockito
                .when(inventoryService.createInventory(any(Inventory.class)))
                .thenReturn(inventoryItemForListing.setId(2));

        Mockito
                .when(inventoryService.findInventoryById(2))
                .thenReturn(inventoryItemForListing.setId(2));

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(business);

        Mockito
                .when(listingsService.createListing(any(Listing.class)))
                .thenReturn(listing.setId(1));

//        Mockito
//                .when(productService.findProductById(anyString()))
//                .thenReturn(productForInventory);

        doReturn(product).when(productService).findProductById(anyString());

        //Request passed to controller is empty, could not tell you why, so the product id field is null.
        doReturn(product).when(productService).findProductById(null);


        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(business).checkUserIsAdministrator(user);
        doReturn(true).when(user).checkUserGlobalAdmin();


        Mockito
                .when(PostListingsDtoMapper.postListingsDto(any(PostListingsDto.class)))
                .thenReturn(listing);

        List<Inventory> inventories = new ArrayList<>();
        inventories.add(inventoryItemForListing);

        Mockito
                .when(inventoryService.getInventoryFromBusinessId(anyInt()))
                .thenReturn(inventories);
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateListing_andNotLoggedIn_then401Response() throws Exception {       //NOT WORKING YET
        String jsonInStringForRequest = "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session token is invalid"));


        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateListing_andInvalidBusinessID_then406Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";

        doReturn(null).when(businessService).findBusinessById(2);

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/2/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateListing_andLoggedInUser_butNotBusinessAdminOrAppAdmin_then403Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";

        doReturn(false).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(false).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(false).when(business).checkUserIsAdministrator(user);
        doReturn(false).when(user).checkUserGlobalAdmin();

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateListing_andLoggedInUser_andNotBusinessAdmin_butAppAdmin_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";

        doReturn(false).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(false).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(user).checkUserGlobalAdmin();

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateListing_andValidUserAndValidBusiness_butInvalidInventory_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 1, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";

        doReturn(null).when(inventoryService).findInventoryById(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateListing_andValidRequest_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("listingId", is(1)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    public void whenPostRequestToCreateListing_andClosesInPast_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\":2, \"quantity\": 4.5, \"price\": 6.5, \"moreInfo\": \"Something\", \"closes\": \"2019-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    public void whenPostRequestToCreateListing_andClosesOfNotTypeDate_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\":2, \"quantity\": 4.5, \"price\": 6.5, \"moreInfo\": \"Something\", \"closes\": 5}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    public void whenPostRequestToCreateListing_andInvalidQuantity_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\":2, \"quantity\": \"4.5\", \"price\": 6.5, \"moreInfo\": \"Something\", \"closes\": \"2022-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

//
//    GET LISTINGS ENDPOINT TESTS
//
//

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    public void whenGetRequestForListingsOfExistingBusiness_andOneListingExists_thenOneListingReturned() throws Exception {
        Mockito
                .when(listingsService.findByBusinessId(anyInt()))
                .thenReturn(Collections.singletonList(listing));

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/listings")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", is(1)))
                .andExpect(jsonPath("[0].quantity", is(3)))
                .andExpect(jsonPath("[0].price", is(17.99)))
                .andExpect(jsonPath("[0].moreInfo", is("Seller may be willing to consider near offers")))
                .andExpect(jsonPath("[0].closes", is("2022-07-14")))
                .andExpect(jsonPath("[0].created", is("2022-04-14")))
                .andExpect(jsonPath("[1]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    public void whenGetRequestForListings_andBusinessNotExists_thenUnacceptable() throws Exception {
        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/listings")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }
}

