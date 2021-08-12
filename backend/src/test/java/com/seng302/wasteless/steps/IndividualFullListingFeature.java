package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.*;
import com.seng302.wasteless.unitTest.ServiceTests.ListingsServiceTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(ListingController.class)
@AutoConfigureWebMvc
public class IndividualFullListingFeature {

    private MockMvc mockMvc;

    private CustomUserDetails currentUserDetails;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ListingsService listingsService;

    private User currentUser;

    private static final List<List<String>> createdListings = new ArrayList<>();

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

    @Given("We are logged in as a user with email {string}")
    public void weAreLoggedInAsAUserWithEmail(String email) {
        Address throwawayAddress = new Address();
        throwawayAddress.setCountry("NZ");
        throwawayAddress.setSuburb("Riccarton");
        throwawayAddress.setCity("Christchurch");
        throwawayAddress.setStreetNumber("1");
        throwawayAddress.setStreetName("Ilam Rd");
        throwawayAddress.setPostcode("8041");
        addressService.createAddress(throwawayAddress);

        currentUser = userService.findUserByEmail(email);

        if (currentUser == null) {
            currentUser = new User();
            currentUser.setRole(UserRoles.USER);
            currentUser.setEmail(email);
            currentUser.setPassword(new BCryptPasswordEncoder().encode("a"));
            currentUser.setDateOfBirth(LocalDate.now().minusYears(17));
            currentUser.setBio("Bio");
            currentUser.setFirstName("FirstName");
            currentUser.setLastName("LastName");
            currentUser.setHomeAddress(throwawayAddress);
            currentUser.setCreated(LocalDate.now());
            userService.createUser(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
    }

    @And("There exists the following listings:")
    public void thereExistsTheFollowingListings(List<List<String>> listings) {
        for (var listingInfo : listings) {
            if (!createdListings.contains(listingInfo)) {  // Make sure we don't create the listing more than once
                ListingsServiceTest.createListingWithNameAndPrice(productService, inventoryService, listingsService,
                        businessService, addressService,listingInfo.get(0), Double.parseDouble(listingInfo.get(1)),
                        "NZ", "Christchurch", "Riccarton", "Wonka Stuff",
                        BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES, LocalDate.of(2099, Month.JANUARY, 1));

                createdListings.add(listingInfo);
            }
        }
    }

    @Given("I have not liked the listing with id {string}")
    public void i_have_not_liked_the_listing_with_id(String id) {
        currentUser.setListingsLiked(new HashSet<>());
        userService.updateUser(currentUser);
        Assertions.assertEquals(0, currentUser.getListingsLiked().size());
    }

    @When("I like a listing with id {string}")
    public void i_like_a_listing_with_id(String string) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(String.format("/listings/%s/like", string))
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The listing with id {string} is added to the list of my liked listings and total likes on the listing are increased")
    public void the_listing_with_id_is_added_to_the_list_of_my_liked_listings_and_total_likes_on_the_listing_are_increased(String string) {
        Listing listing = listingsService.findFirstById(Integer.parseInt(string));
        currentUser = userService.findUserById(currentUser.getId());
        Assertions.assertEquals(1, listing.getUsersLiked());
        Assertions.assertEquals(listing.getId(), currentUser.getListingsLiked().iterator().next().getId());
    }

    @Given("I have liked the listing  with id {string}")
    public void i_have_liked_the_listing_with_id(String string) {
        Listing listing = listingsService.findFirstById(Integer.parseInt(string));
        currentUser.setListingsLiked(new HashSet<>());
        currentUser.addLikedListing(listing);
        userService.updateUser(currentUser);
        listing.setUsersLiked(1);
        listingsService.updateListing(listing);
        Assertions.assertEquals(1, listing.getUsersLiked());
        Assertions.assertEquals(listing.getId(), currentUser.getListingsLiked().iterator().next().getId());

    }

    @Then("The listing with id {string} is no longer in the list of my liked listings")
    public void the_listing_with_id_is_no_longer_in_the_list_of_my_liked_listings(String string) {
        Listing listing = listingsService.findFirstById(Integer.parseInt(string));
        currentUser = userService.findUserById(currentUser.getId());
        Assertions.assertEquals(0, currentUser.getListingsLiked().size());
    }

    @Then("The listing with id {string} is no longer in the list of my liked listings and total likes on the listing are decreased")
    public void the_listing_with_id_is_no_longer_in_the_list_of_my_liked_listings_and_total_likes_on_the_listing_are_decreased(String string) {
        Listing listing = listingsService.findFirstById(Integer.parseInt(string));
        currentUser = userService.findUserById(currentUser.getId());
        Assertions.assertEquals(0, listing.getUsersLiked());
        Assertions.assertEquals(0, currentUser.getListingsLiked().size());
    }



}
