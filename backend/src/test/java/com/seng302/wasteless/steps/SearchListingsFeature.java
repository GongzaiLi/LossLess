package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hamcrest.Matchers;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(ListingController.class)
@AutoConfigureWebMvc
public class SearchListingsFeature {

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

    private ResultActions responseResult;

    private static final List<String> createdListings = new ArrayList<>();

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

    @Given("We are logged in as the individual user with email {string}")
    public void weAreLoggedInAsTheIndividualUserWithEmail(String email) {
        Address throwawayAddress = new Address();
        throwawayAddress.setCountry("NZ");
        throwawayAddress.setSuburb("Riccarton");
        throwawayAddress.setCity("Christchurch");
        throwawayAddress.setStreetNumber("1");
        throwawayAddress.setStreetName("Ilam Rd");
        throwawayAddress.setPostcode("8041");
        addressService.createAddress(throwawayAddress);

        User currentUser = userService.findUserByEmail(email);

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

    @And("The following listings exist:")
    public void theFollowingListingsExist(List<String> listings) {
        for (var name : listings) {
            if (!createdListings.contains(name)) {  // Make sure we don't create the listing more than once
                System.out.println("Created Name " + name);
                var product = new Product();
                product.setName(name);
                productService.createProduct(product);

                var inventory = new Inventory();
                inventory.setProduct(product);
                inventory.setExpires(LocalDate.MAX);
                inventory.setBusinessId(0);
                inventoryService.createInventory(inventory);

                var newListing = new Listing();
                newListing.setInventoryItem(inventory);
                newListing.setQuantity(69);
                newListing.setBusinessId(0);
                listingsService.createListing(newListing);

                createdListings.add(name);
            }
        }
    }

    @When("I search for listings with no filtering")
    public void iSearchForListingsWithNoFiltering() throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @When("I search for listings by product name {string}")
    public void iSearchForListingsByProductName(String name) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get("/listings/search")
                .queryParam("searchQuery", name)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The results contain the following products:")
    public void theResultsContainTheFollowingProducts(List<String> listings) throws Exception {
        responseResult.andExpect(
                jsonPath("$..inventoryItem.product.name",
                        hasItems(listings.toArray())   // We have to let the compiler know this is an array so it works with varargs. See https://stackoverflow.com/questions/1092981/why-doesnt-this-code-attempting-to-use-hamcrests-hasitems-compile
                )
        );
    }

    @Then("The results contain exclusively the following products:")
    public void theResultsContainExclusivelyTheFollowingProducts(List<String> listings) throws Exception {
        responseResult.andExpect(
                jsonPath("$..inventoryItem.product.name",   // Gets list of product names
                        containsInAnyOrder(listings.stream().map(Matchers::equalTo).collect(Collectors.toList()))   // Convert list of strings (listings) into list of matchers that match the strings
                )
        );
    }

    @Then("No results are given")
    public void noResultsAreGiven() throws Exception {
        responseResult.andExpect(jsonPath("listings", hasSize(0)));
    }
}