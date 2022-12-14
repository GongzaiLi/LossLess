package com.seng302.wasteless.unitTest.ControllerTests;

import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.dto.mapper.GetBusinessesDtoMapper;
import com.seng302.wasteless.dto.mapper.PostListingsDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ListingController.class)
@AutoConfigureMockMvc(addFilters = false) //Disable spring security for the unit tests
 class ListingControllerUnitTest {

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

    private Listing listing;

    private List<Listing> listingList;

    private List<Notification> notificationList;

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
                .setQuantityUnlisted(5)
                .setManufactured(expiry.minusMonths(4))
                .setBestBefore(expiry.minusMonths(1))
                .setProduct(productForInventory);

        Inventory inventoryItemForListing2 = new Inventory();
        inventoryItemForListing2.setExpires(expiry)
                .setTotalPrice(50)
                .setPricePerItem(10)
                .setSellBy(expiry.minusMonths(2))
                .setQuantity(5)
                .setManufactured(expiry.minusMonths(4))
                .setBestBefore(expiry.minusYears(3))
                .setProduct(productForInventory);

        Inventory inventoryItemForListing3 = new Inventory();
        inventoryItemForListing3.setExpires(expiry)
                .setTotalPrice(50)
                .setPricePerItem(10)
                .setSellBy(expiry.minusYears(3))
                .setQuantity(5)
                .setManufactured(expiry.minusMonths(4))
                .setBestBefore(expiry.minusMonths(1))
                .setProduct(productForInventory);

        Inventory inventoryItemForListing4 = new Inventory();
        inventoryItemForListing4.setExpires(expiry.minusYears(3))
                .setTotalPrice(50)
                .setPricePerItem(10)
                .setSellBy(expiry.minusMonths(2))
                .setQuantity(5)
                .setManufactured(expiry.minusMonths(4))
                .setBestBefore(expiry.minusMonths(1))
                .setProduct(productForInventory);


        business = mock(Business.class);
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1);
        business.setAdministrators(new ArrayList<>());
        business.setName("Jimmy's clown store");


        listing = new Listing();
        listing.setInventoryItem(inventoryItemForListing)
                .setBusiness(business)
                .setId(1)
                .setCreated(expiry.minusMonths(3))
                .setQuantity(3)
                .setPrice(17.99)
                .setMoreInfo("Seller may be willing to consider near offers")
                .setCloses(LocalDate.of(2021, Month.DECEMBER, 1).atTime(23,59));


        listingList = new ArrayList<>();
        listingList.add(
                listing = new Listing()
                        .setBusiness(business)
                        .setInventoryItem(inventoryItemForListing2)
                .setCreated(expiry.minusMonths(3))
                .setQuantity(3)
                .setPrice(17.99)
                .setMoreInfo("Seller may be willing to consider near offers")
                .setCloses(LocalDate.of(2021, Month.JULY, 14).atTime(23,59)));

        listingList.add(
                listing = new Listing()
                        .setBusiness(business)
                        .setInventoryItem(inventoryItemForListing3)
                        .setCreated(expiry.minusMonths(3))
                        .setQuantity(3)
                        .setPrice(17.99)
                        .setMoreInfo("Seller may be willing to consider near offers")
                        .setCloses(LocalDate.of(2021, Month.MARCH, 27).atTime(23,59)));

        listingList.add(
                listing = new Listing()
                        .setBusiness(business)
                        .setInventoryItem(inventoryItemForListing4)
                        .setCreated(expiry.minusMonths(3))
                        .setQuantity(3)
                        .setPrice(17.99)
                        .setMoreInfo("Seller may be willing to consider near offers")
                        .setCloses(LocalDate.of(2022, Month.JANUARY, 1).atTime(23,59)));


        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);



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
                .when(inventoryService.findInventoryById(3))
                .thenReturn(inventoryItemForListing2.setId(2));

        Mockito
                .when(inventoryService.findInventoryById(4))
                .thenReturn(inventoryItemForListing3.setId(2));

        Mockito
                .when(inventoryService.findInventoryById(5))
                .thenReturn(inventoryItemForListing4.setId(2));

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(business);

        Mockito
                .when(listingsService.createListing(any(Listing.class)))
                .thenReturn(listing.setId(1));

        Mockito
                .when(listingsService.searchListings(any(Optional.class), any(Optional.class), any(Optional.class), any(Optional.class), any(Optional.class), any(Optional.class),any(Optional.class), any(Optional.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(listingList));

        Mockito
                .when(listingsService.searchListings(eq(Optional.of("blah")), any(Optional.class), any(Optional.class), any(Optional.class), any(Optional.class),  any(Optional.class),any(Optional.class), any(Optional.class),any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(listing)));

        Mockito
                .when(productService.findProductById(anyString()))
                .thenReturn(productForInventory);

        doReturn(product).when(productService).findProductById(anyString());

        //Request passed to controller is empty, could not tell you why, so the product id field is null.
        doReturn(product).when(productService).findProductById(null);


        doReturn(new ArrayList<>()).when(business).getAdministrators();
        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);
        doReturn(true).when(business).checkUserIsAdministrator(user);
        doReturn(LocalDate.MIN).when(business).getCreated();
        doReturn(true).when(user).checkUserGlobalAdmin();
        doReturn(UserRoles.USER).when(user).getRole();


        Mockito
                .when(PostListingsDtoMapper.postListingsDto(any(PostListingsDto.class)))
                .thenReturn(listing);

        List<Inventory> inventories = new ArrayList<>();
        inventories.add(inventoryItemForListing);
        inventories.add(inventoryItemForListing2);
        inventories.add(inventoryItemForListing3);
        inventories.add(inventoryItemForListing4);

        notificationList= new ArrayList<>();

        Mockito
                .when(inventoryService.searchInventoryFromBusinessId(anyInt(), any(), any()))
                .thenReturn(inventories);

        new GetBusinessesDtoMapper(businessService, userService);

        Mockito
                .when(notificationService.filterNotifications(anyInt(), any(), any()))
                .thenReturn(notificationList);
        Mockito
                .when(notificationService.saveNotification(any(Notification.class)))
                .thenAnswer((answer) -> {
                    Notification notif = (Notification) answer.getArguments()[0];
                    notificationList.add(notif);
                    return  notif;
                });
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
     void whenPostRequestToCreateListing_andNotLoggedIn_then401Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12T23:59:59Z\"}";

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
     void whenPostRequestToCreateListing_andLoggedInUser_butNotBusinessAdminOrAppAdmin_then403Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12T23:59:59Z\"}";

        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to make this request")).when(businessService).checkUserAdminOfBusinessOrGAA(business, user);

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
     void whenPostRequestToCreateListing_andLoggedInUser_andNotBusinessAdmin_butAppAdmin_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12T23:59:59Z\"}";

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
     void whenPostRequestToCreateListing_andValidUserAndValidBusiness_butInvalidInventory_then400Response() throws Exception {
        String jsonInStringForRequest = "{\"inventoryItemId\": 1, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12T23:59:59Z\"}";

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory item with given id does not exist")).when(inventoryService).findInventoryById(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Testing with Best before in past id=3, sell by in past id=4 and both future id=2
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "{\"inventoryItemId\": 2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12T23:59:59Z\"}",
            "{\"inventoryItemId\": 3, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12T23:59:59Z\"}",
            "{\"inventoryItemId\": 4, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12T23:59:59Z\"}"})
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
     void whenPostRequestToCreateListing_andValidRequest_then201Response(String request) throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(request)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("listingId", is(1)));
    }


    /**
     * Test 1: inventoryItemExpiryInPast
     * Test 2: ClosesInPast
     * Test 3: ClosesOfNotTypeDate
     * Test 4: InvalidQuantityFormat
     * Test 4: Quantity of Listing to large
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "{\"inventoryItemId\":5, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": 21.99, \"closes\": \"2022-05-12T23:59:59Z\"}",
            "{\"inventoryItemId\":2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": \"Something\", \"closes\": \"2019-05-12T23:59:59Z\"}",
            "{\"inventoryItemId\":2, \"quantity\": 4, \"price\": 6.5, \"moreInfo\": \"Something\", \"closes\": 5}",
            "{\"inventoryItemId\":2, \"quantity\": \"4.5\", \"price\": 6.5, \"moreInfo\": \"Something\", \"closes\": \"2022-05-12T23:59\"}",
            "{\"inventoryItemId\":2, \"quantity\": 10, \"price\": 6.5, \"moreInfo\": \"Something\", \"closes\": \"2022-05-12T23:59\"}"})
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToCreateListing_andInValidRequest_then400Response(String request) throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/listings")
                .content(request)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andEmptyString_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("listings", hasSize(3)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andQueryString_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("searchQuery", "blah")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("listings", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andClosingDateStartInvalid_then400Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("closingDateStart", "blah")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andClosingDateEndInvalid_then400Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("closingDateEnd", "blah")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andClosingDateStartValid_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("closingDateStart", "2015-09-15 15:02:32")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andClosingDateEndValid_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("closingDateEnd", "2015-09-15 15:02:32")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andClosingDatesBothValid_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("closingDateStart ", "2015-09-15 15:02:32")
                .queryParam("closingDateEnd", "2016-09-15 15:02:32")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andClosingDateStartEmpty_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("closingDateStart", "")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andClosingDateEndEmpty_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("closingDateEnd", "")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToSearchListings_andClosingDatesBothEmpty_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("closingDateStart ", "")
                .queryParam("closingDateEnd", "")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPutRequestToLikeAListing_andListingNotAlreadyLiked_then200Response() throws Exception {
        Mockito.when(listingsService.findFirstById(1)).thenReturn(listing);
        Mockito.when(user.toggleListingLike(listing)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/listings/1/like")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("liked", is(true)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPutRequestToLikeAListing_andListingNotAlreadyLiked_then200ResponseAndNotificationAdded() throws Exception {
        Mockito.when(listingsService.findFirstById(1)).thenReturn(listing);
        Mockito.when(user.toggleListingLike(listing)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/listings/1/like")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(user.getId(),notificationList.get(0).getUserId());
        Assertions.assertEquals(listing.getId().toString(),notificationList.get(0).getSubjectId());
        Assertions.assertEquals(NotificationType.LIKEDLISTING,notificationList.get(0).getType());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPutRequestToLikeAListing_andListingAlreadyLiked_then200Response() throws Exception {
        Mockito.when(listingsService.findFirstById(1)).thenReturn(listing);
        Set<Listing> set = new HashSet<>();
        set.add(listing);
        Mockito.when(user.getListingsLiked()).thenReturn(set);
        user.toggleListingLike(listing);
        mockMvc.perform(MockMvcRequestBuilders.put("/listings/1/like")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("liked", is(false)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPutRequestToLikeAListing_andListingAlreadyLiked_then200ResponseAndNotificationAdded() throws Exception {
        Mockito.when(listingsService.findFirstById(1)).thenReturn(listing);
        Set<Listing> set = new HashSet<>();
        set.add(listing);
        Mockito.when(user.getListingsLiked()).thenReturn(set);
        user.toggleListingLike(listing);
        mockMvc.perform(MockMvcRequestBuilders.put("/listings/1/like")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(user.getId(),notificationList.get(0).getUserId());
        Assertions.assertEquals(listing.getId().toString(),notificationList.get(0).getSubjectId());
        Assertions.assertEquals(NotificationType.UNLIKEDLISTING,notificationList.get(0).getType());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPutRequestToLikeAListing_andListingNotExist_then200Response() throws Exception {
        Mockito.when(listingsService.findFirstById(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Listing does not exist"));
        mockMvc.perform(MockMvcRequestBuilders.put("/listings/1/like")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToListings_andListingExists_then200Response() throws Exception {
        Mockito.when(listingsService.findFirstById(1)).thenReturn(listing);

        mockMvc.perform(MockMvcRequestBuilders.get("/listings/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToListings_andInvalidListingId_thenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/listings/%s", "badId"))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToListings_andListingDoesNotExist_then406Response() throws Exception {

        Mockito.when(listingsService.findFirstById(1))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Listing does not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/listings/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToListings_andUserNotLoggedIn_then401Response() throws Exception {

        Mockito.when(userService.getCurrentlyLoggedInUser())
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session token is invalid"));

        mockMvc.perform(MockMvcRequestBuilders.get("/listings/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPostRequestToPurchaseListings_andListingExists_then200Response() throws Exception {

        Mockito
                .when(listingsService.findFirstById(1))
                .thenReturn(listing);
        Mockito
                .when(listingsService.purchase(any(), any()))
                .thenReturn(new PurchasedListing(listing, user));

        mockMvc.perform(MockMvcRequestBuilders.post("/listings/1/purchase")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenPostRequestToPurchaseListings_andListingNotExists_then406Response() throws Exception {
        Mockito
                .when(listingsService.findFirstById(anyInt()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE));

        mockMvc.perform(MockMvcRequestBuilders.post("/listings/42069/purchase")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToPurchaseListings_andPurchaseListingsIdExists_then200Response() throws Exception {
        PurchasedListing purchasedListing  = new PurchasedListing();
        purchasedListing.setId(1);
        purchasedListing.setBusiness(business);
        purchasedListing.setPurchaser(user);
        purchasedListing.setListingDate(LocalDate.now());
        purchasedListing.setClosingDate(LocalDate.now().atTime(23,59));
        purchasedListing.setPrice(1.0);
        purchasedListing.setNumberOfLikes(1);
        purchasedListing.setQuantity(1);
        purchasedListing.setSaleDate(LocalDate.now());

        Mockito
                .when(purchasedListingService.findPurchasedListingById(anyInt()))
                .thenReturn(purchasedListing);


        mockMvc.perform(MockMvcRequestBuilders.get("/purchase/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToPurchaseListings_andPurchaseListingsIdIsNotExists_then400Response() throws Exception {

        Mockito
                .when(purchasedListingService.findPurchasedListingById(anyInt()))
                .thenReturn(null);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/purchase/1"))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assertions.assertEquals("purchase Id does not exist.", result.getResponse().getContentAsString());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToPurchaseListings_andPurchaseListingsIdExists_andLoginUserIsNotPurchaser_then403Response() throws Exception {

        PurchasedListing purchasedListing  = new PurchasedListing();
        purchasedListing.setId(1);
        purchasedListing.setBusiness(business);
        purchasedListing.setPurchaser(user);
        purchasedListing.setListingDate(LocalDate.now());
        purchasedListing.setClosingDate(LocalDate.now().atTime(23,59));
        purchasedListing.setPrice(1.0);
        purchasedListing.setNumberOfLikes(1);
        purchasedListing.setQuantity(1);
        purchasedListing.setSaleDate(LocalDate.now());

        Mockito
                .when(purchasedListingService.findPurchasedListingById(anyInt()))
                .thenReturn(purchasedListing);

        var newUser = new User();
        newUser.setId(2);
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(newUser);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/purchase/1"))
                .andExpect(status().isForbidden())
                .andReturn();
        Assertions.assertEquals("You are not allowed to make this request", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToPurchaseListings_andPurchaseListingsIdExists_andLoginUserIsNotPurchaser_andNotUserAdminOfBusinessOrGAA_then403Response() throws Exception {

        PurchasedListing purchasedListing  = new PurchasedListing();
        purchasedListing.setId(1);
        purchasedListing.setBusiness(business);
        purchasedListing.setPurchaser(user);
        purchasedListing.setListingDate(LocalDate.now());
        purchasedListing.setClosingDate(LocalDate.now().atTime(23,59));
        purchasedListing.setPrice(1.0);
        purchasedListing.setNumberOfLikes(1);
        purchasedListing.setQuantity(1);
        purchasedListing.setSaleDate(LocalDate.now());

        Mockito
                .when(purchasedListingService.findPurchasedListingById(anyInt()))
                .thenReturn(purchasedListing);

        var newUser = new User();
        newUser.setId(2);
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(newUser);

        Mockito
                .when(businessService.checkUserAdminOfBusinessOrGAA(any(Business.class), any(User.class)))
                .thenReturn(false);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/purchase/1"))
                .andExpect(status().isForbidden())
                .andReturn();
        Assertions.assertEquals("You are not allowed to make this request", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToPurchaseListings_andPurchaseListingsIdExists_andLoginUserIsPurchaser_andNotUserAdminOfBusinessOrGAA_then200Response() throws Exception {

        PurchasedListing purchasedListing  = new PurchasedListing();
        purchasedListing.setId(1);
        purchasedListing.setBusiness(business);
        user.setId(1);
        purchasedListing.setPurchaser(user);
        purchasedListing.setListingDate(LocalDate.now());
        purchasedListing.setClosingDate(LocalDate.now().atTime(23,59));
        purchasedListing.setPrice(1.0);
        purchasedListing.setNumberOfLikes(1);
        purchasedListing.setQuantity(1);
        purchasedListing.setSaleDate(LocalDate.now());

        Mockito
                .when(purchasedListingService.findPurchasedListingById(anyInt()))
                .thenReturn(purchasedListing);

        var newUser = new User();
        newUser.setId(0);
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(newUser);

        Mockito
                .when(businessService.checkUserAdminOfBusinessOrGAA(any(Business.class), any(User.class)))
                .thenReturn(false);


        mockMvc.perform(MockMvcRequestBuilders.get("/purchase/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void whenGetRequestToPurchaseListings_andPurchaseListingsIdExists_andLoginUserIsNotPurchaser_butIsUserAdminOfBusinessOrGAA_then200Response() throws Exception {

        PurchasedListing purchasedListing  = new PurchasedListing();
        purchasedListing.setId(1);
        purchasedListing.setBusiness(business);
        purchasedListing.setPurchaser(user);
        purchasedListing.setListingDate(LocalDate.now());
        purchasedListing.setClosingDate(LocalDate.now().atTime(23,59));
        purchasedListing.setPrice(1.0);
        purchasedListing.setNumberOfLikes(1);
        purchasedListing.setQuantity(1);
        purchasedListing.setSaleDate(LocalDate.now());

        Mockito
                .when(purchasedListingService.findPurchasedListingById(anyInt()))
                .thenReturn(purchasedListing);

        var newUser = new User();
        newUser.setId(1);
        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(newUser);

        Mockito
                .when(businessService.checkUserAdminOfBusinessOrGAA(any(Business.class), any(User.class)))
                .thenReturn(true);


        mockMvc.perform(MockMvcRequestBuilders.get("/purchase/1"))
                .andExpect(status().isOk());
    }

}

