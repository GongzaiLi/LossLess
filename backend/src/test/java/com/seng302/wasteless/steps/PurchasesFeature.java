package com.seng302.wasteless.steps;

import com.jayway.jsonpath.JsonPath;
import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import com.seng302.wasteless.repository.UserRepository;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;
import static com.seng302.wasteless.TestUtils.createListingWithNameAndPrice;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(ListingController.class)
@AutoConfigureWebMvc
public class PurchasesFeature {

    private MockMvc mockMvc;

    private CustomUserDetails currentUserDetails;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ListingsService listingsService;

    @Autowired
    private BusinessService businessService;


    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PurchasedListingRepository purchasedListingRepository;

    private Listing curListing;

    private String listingName;

    private List<User> userList;

    private ResultActions responseResult;

    /**
     * Sets up the mockMVC object by building with with webAppContextSetup.
     * We do this manually because @Autowired mockMvc doesn't work.
     */
    @Before
    public void setUpMockMvc() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity()) // This allows us to use .with(user(currentUserDetails)).
                // See https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html#test-mockmvc-securitycontextholder-rpp
                .build();
    }

    @Given("We are logged in as the user that has email {string}")
    public void weAreLoggedInAsTheIndividualUserWithEmail(String email) {
        User currentUser = userService.findUserByEmail(email);

        if (currentUser == null) {
            currentUser = newUserWithEmail(email);
            addressService.createAddress(currentUser.getHomeAddress());
            userService.createUser(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
    }

    @Given("A listing exists with quantity {int} and its inventory item has quantity {int}")
    public void aListingExistsWithQuantityAndItsInventoryItemHasQuantity(int listingQuantity, int inventoryQuantity) {
        curListing = createListingWithNameAndPrice(productService, inventoryService, listingsService, businessService, addressService, "Black Water No Sugar", 1.0, "NZ", "Christchurch", "Riccarton", "Wonka Water", BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES, LocalDate.of(2099, Month.JANUARY, 1),
                listingQuantity, inventoryQuantity);
    }


    @When("I purchase that listing")
    public void iPurchaseThatListing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/listings/%d/purchase", curListing.getId()))
                .with(user(currentUserDetails))
                .with(csrf()))
                .andExpect(status().isCreated());
    }


    @Then("The inventory item's quantity is {int}")
    public void theInventoryItemSQuantityIs(int quantity) {
        var inventoryItem = inventoryService.findInventoryById(curListing.getInventoryItem().getId());
        Assertions.assertEquals(quantity, inventoryItem.getQuantity());
    }

    @Given("A listing exists with name {string}, quantity {int} and its inventory item has quantity {int}")
    public void aListingExistsWithNameQuantityAndItsInventoryItemHasQuantity(String name, int listingQuantity, int inventoryQuantity) {
        curListing = createListingWithNameAndPrice(productService, inventoryService, listingsService, businessService, addressService, name, 1.0, "NZ", "Christchurch", "Riccarton", "Wonka Water", BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES, LocalDate.of(2099, Month.JANUARY, 1),
                listingQuantity, inventoryQuantity);
        listingName = name;
    }

    @Then("The sale listing does not appear when I search for it by name")
    public void theSaleListingDoesNotAppearWhenISearchForItByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("searchQuery", listingName)
                .with(user(currentUserDetails))
                .with(csrf()))
                .andExpect(jsonPath("listings").isEmpty());
    }

    @Then("Information about the sale \\(sale date, listing date, product, amount, number of likes) is recorded in a sales history for the seller’s business.")
    public void informationAboutTheSaleSaleDateListingDateProductAmountNumberOfLikesIsRecordedInASalesHistoryForTheSellerSBusiness() {
        List<PurchasedListing> purchases = purchasedListingRepository.findAllByBusinessId(curListing.getBusiness().getId());
        purchases.sort((a, b) -> b.getSaleDate().compareTo(a.getSaleDate()));  // Make sure we get the newest first
        var purchasedListing = purchases.get(0);

        Assertions.assertEquals(curListing.getCreated(), purchasedListing.getListingDate());
        Assertions.assertEquals(curListing.getInventoryItem().getProduct().getId(), purchasedListing.getProduct().getId());
        Assertions.assertEquals(curListing.getQuantity(), purchasedListing.getQuantity());
        Assertions.assertEquals(curListing.getPrice(), purchasedListing.getPrice());
        Assertions.assertEquals(curListing.getUsersLiked(), purchasedListing.getNumberOfLikes());
        Assertions.assertNotNull(purchasedListing.getSaleDate());
    }

    @Given("A listing exists with {int} user who have liked it")
    public void a_listing_exists_with_user_who_have_liked_it(Integer numUsers) throws Exception {
        curListing = createListingWithNameAndPrice(productService, inventoryService, listingsService, businessService, addressService, "Black Water No Sugar", 1.0, "NZ", "Christchurch", "Riccarton", "Wonka Water", BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES, LocalDate.of(2099, Month.JANUARY, 1),
                5, 5);
        userList = new ArrayList<>();
        for (int i = 0; i < numUsers; i++) {
            var user = newUserWithEmail(String.format("user%d@gmail.com", i));
            addressService.createAddress(user.getHomeAddress());
            user = userService.createUser(user);
            userList.add(user);
            mockMvc.perform(MockMvcRequestBuilders.put(String.format("/listings/%d/like", curListing.getId()))
                    .with(user(new CustomUserDetails(user)))
                    .with(csrf()))
                    .andExpect(status().isOk());
        }
    }

    @Then("Notifications are created for the {int} users who have liked the listing that was purchased")
    public void notifications_are_created_for_the_users_who_have_liked_the_listing_that_was_purchased(Integer numNotification) {
        Assertions.assertEquals(userList.size(), numNotification);
        for (User user : userList) {
            List<Notification> notificationList = notificationService.findAllNotificationsByUserId(user.getId());
            Assertions.assertTrue(notificationList.stream().anyMatch(notify -> notify.getType().equals(NotificationType.LIKEDLISTING_PURCHASED)));
        }
    }

    @Then("A notifications is created telling me I have purchased the listing")
    public void a_notifications_is_created_telling_me_i_have_purchased_the_listing() {
        List<Notification> notificationList = notificationService.findAllNotificationsByUserId(currentUserDetails.getId());
        Assertions.assertTrue(notificationList.stream().anyMatch(notify -> notify.getType().equals(NotificationType.PURCHASED_LISTING)));
    }

    @Given("A purchase exists with the name {string}")
    public void a_purchase_exists_with_the_name(String productName) throws Exception {

        curListing = createListingWithNameAndPrice(productService, inventoryService, listingsService, businessService, addressService, productName, 1.0, "NZ", "Christchurch", "Riccarton", "Wonka Water", BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES, LocalDate.of(2099, Month.JANUARY, 1),
                10, 20);

        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/listings/%d/purchase", curListing.getId()))
                .with(user(currentUserDetails))
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @When("I try and get information about the sale")
    public void i_try_and_get_information_about_the_sale() throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/purchase/%d", 1))
                .with(user(currentUserDetails))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Then("The information about the sale \\(sale date, listing date, product, amount, number of likes) is given from the sales history.")
    public void the_information_about_the_sale_sale_date_listing_date_product_amount_number_of_likes_is_given_from_the_sales_history() throws Exception {
        Assertions.assertEquals(JsonPath.read(responseResult.andReturn().getResponse().getContentAsString(), "$.business.name"), curListing.getBusiness().getName());
        Assertions.assertEquals(JsonPath.read(responseResult.andReturn().getResponse().getContentAsString(), "$.product.id"), curListing.getInventoryItem().getProduct().getId());
    }
}
