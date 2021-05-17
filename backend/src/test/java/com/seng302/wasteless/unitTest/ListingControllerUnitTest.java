package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.InventoryController;
import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.dto.mapper.PostInventoryDtoMapper;
import com.seng302.wasteless.dto.mapper.PostListingsDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(InventoryController.class)
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

    @BeforeAll
    static void beforeAll() {
        //This line is important, do not remove
        mockStatic(PostListingsDtoMapper.class);
    }

    @BeforeEach
    void setUp() {
        Product productForInventory = new Product();
        productForInventory.setId("Clown-Shows");
        productForInventory.setBusinessId(1);
        productForInventory.setName("Clown Shows");

        LocalDate today = LocalDate.now();

        Inventory inventoryItemForListing = new Inventory();
        inventoryItemForListing.setExpires(today.plusMonths(2))
                .setTotalPrice(50)
                .setPricePerItem(10)
                .setSellBy(today.plusMonths(1))
                .setQuantity(5)
                .setManufactured(today.minusMonths(1))
                .setBestBefore(today.plusMonths(1))
                .setProduct(productForInventory);


        LocalDate closes = inventoryItemForListing.getExpires();

        Listing listing = new Listing();
        listing.setInventory(inventoryItemForListing)
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
                .when(userService.findUserByEmail(anyString()))
                .thenReturn(user);

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(business);

//        Mockito
//                .when(listingsService.findByBusinessId(anyInt()))
//                .thenReturn(listing.setId(1));

        doReturn(product).when(productService).findProductById(anyString());

        //Request passed to controller is empty, could not tell you why, so the product id field is null.
        doReturn(product).when(productService).findProductById(null);

        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);

        doReturn(true).when(business).checkUserIsAdministrator(user);

        doReturn(true).when(user).checkUserGlobalAdmin();



        Mockito
                .when(PostInventoryDtoMapper.postInventoryDtoToEntityMapper(any(PostInventoryDto.class)))
                .thenReturn(inventoryItemForListing);

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
    public void whenPostRequestToCreateInventory_andValidRequest_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"productId\": \"WATT-420-BEANS\", \"quantity\": 4, \"pricePerItem\": 6.5, \"totalPrice\": 21.99, \"manufactured\": \"2020-05-12\", \"sellBy\": \"3021-05-12\",\"bestBefore\": \"3021-05-12\",\"expires\": \"3021-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("inventoryItemId", is(2)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateListing_andValidRequest_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": \"WATT-420-BEANS\", \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2020-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("listingId", is(1)));
    }

}

