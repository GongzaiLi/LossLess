package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.*;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
public class CreateListingFeature {

    private MockMvc mockMvc;

    private CustomUserDetails currentUserDetails;

    private ResultActions result;

    private Address throwawayAddress;

    private int businessId;

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

    /**
     * Creates a throwaway address so we can use it for other step definitions.
     * For example, we can reuse this address when creating a new user.
     */
    @Before
    public void setupAddress() {
        throwawayAddress = new Address();
        throwawayAddress.setCountry("NZ");
        throwawayAddress.setSuburb("Riccarton");
        throwawayAddress.setCity("Christchurch");
        throwawayAddress.setStreetNumber("1");
        throwawayAddress.setStreetName("Ilam Rd");
        throwawayAddress.setPostcode("8041");
        addressService.createAddress(throwawayAddress);
    }



    public void theProductWithIdExistsInTheCatalogueForBusiness(int businessId, String productId) {
        if (productService.findProductById(productId) == null) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
            Product product = new Product();
            product.setId(productId);
            product.setBusinessId(businessId);
            product.setName("Blah");
            productService.createProduct(product);
        }
    }

    @Given("User: {string} is logged in")
    public void userIsLoggedIn(String email) {
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

    @And("User: {string} is an administrator for business {int}")
    public void userIsAnAdministratorForBusiness(String email, int businessId) {
        this.businessId = businessId;
        User user = userService.findUserByEmail(email);

        Business business = businessService.findBusinessById(businessId);
        if (business == null) {
            business= new Business();
            business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
            business.setId(businessId);
            business.setAdministrators(Collections.singletonList(user));
            business.setName("Jimmy's clown store");
            business.setAddress(throwawayAddress);
            businessService.createBusiness(business);
        }

        if (!business.checkUserIsAdministrator(user)) {
            user.addPrimaryBusiness(business);
            business.getAdministrators().add(user);
        }
    }


    @And("There is an inventory item with an inventory id {int}, productId {string}")
    public void thereIsAnInventoryItemWithAnInventoryIdProductId(int inventoryId, String productId) {
        this.theProductWithIdExistsInTheCatalogueForBusiness(businessId, productId);
        if (inventoryService.findInventoryById(inventoryId) == null) {
            Inventory inventory = new Inventory();
            inventory.setId(inventoryId);
            inventory.setBusinessId(businessId);
            inventory.setProduct(productService.findProductById(productId));
            inventory.setQuantity(5);
            inventory.setPricePerItem(1.5);
            inventory.setTotalPrice(15);
            inventory.setExpires(LocalDate.parse("2022-05-23"));
            inventoryService.createInventory(inventory);
        }
    }


    @When("Create A Listing with full detail: inventory item Id {int}, quantity {int}, price {double}, moreInfo {string}, closes {string}")
    public void createAListingWithFullDetailInventoryItemIdQuantityPriceMoreInfoCloses(int inventoryItemId, int quantity, double price, String moreInfo, String closes) throws Exception {

        String jsonInStringForRequestBody = String.format(
                "{\"inventoryItemId\": %d, \"quantity\": %d, \"price\": %f, \"moreInfo\": \"%s\", \"closes\": \"%s\"}", inventoryItemId, quantity, price, moreInfo, closes);


        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/businesses/%d/listings", this.businessId))
                .content(jsonInStringForRequestBody)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));


    }

    @Then("The user will be able to see the new Listing")
    public void theUserWillBeAbleToSeeTheNewListing() throws Exception {
        result.andExpect(status().isCreated());
    }



    @When("Create A Listing without inventory item Id: quantity {int}, price {double}, moreInfo {string}, closes {string}")
    public void createAListingWithoutInventoryItemIdQuantityPriceMoreInfoCloses(int quantity, double price, String moreInfo, String closes) throws Exception {
        String jsonInStringForRequestBody = String.format(
                "{\"quantity\": %d, \"price\": %f, \"moreInfo\": \"%s\", \"closes\": \"%s\"}", quantity, price, moreInfo, closes);

        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/businesses/%d/listings", this.businessId))
                .content(jsonInStringForRequestBody)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The user will be able to see the bad request")
    public void theUserWillBeAbleToSeeTheBadRequest() throws Exception {
        result.andExpect(status().is4xxClientError());
    }



    @When("Create A Listing with not exist inventory item Id: inventory item Id {int}, quantity {int}, price {double}, moreInfo {string}, closes {string}")
    public void createAListingWithNotExistInventoryItemIdInventoryItemIdQuantityPriceMoreInfoCloses(int inventoryItemId, int quantity, double price, String moreInfo, String closes) throws Exception {

        String jsonInStringForRequestBody = String.format(
                "{\"inventoryItemId\": %d, \"quantity\": %d, \"price\": %f, \"moreInfo\": \"%s\", \"closes\": \"%s\"}", inventoryItemId, quantity, price, moreInfo, closes);


        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/businesses/%d/listings", this.businessId))
                .content(jsonInStringForRequestBody)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The user will receive will receive an error message of {string}")
    public void theUserWillReceiveWillReceiveAnErrorMessageOf(String message) throws Exception {
        result.andExpect(status().is4xxClientError());
        result.andExpect(content().string(message));
    }

    @When("Create A Listing without quantity: inventory item Id {int}, price {double}, moreInfo {string}, closes {string}")
    public void createAListingWithoutQuantityInventoryItemIdPriceMoreInfoCloses(int inventoryItemId, double price, String moreInfo, String closes) throws Exception {
        String jsonInStringForRequestBody = String.format(
                "{\"inventoryItemId\": %d, \"price\": %f, \"moreInfo\": \"%s\", \"closes\": \"%s\"}", inventoryItemId, price, moreInfo, closes);

        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/businesses/%d/listings", this.businessId))
                .content(jsonInStringForRequestBody)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }




    @When("Create A Listing with quantity over the Inventory had: inventory item Id {int}, quantity {int}, price {double}, moreInfo {string}, closes {string}")
    public void createAListingWithQuantityOverTheInventoryHadInventoryItemIdQuantityPriceMoreInfoCloses(int inventoryItemId, int quantity, double price, String moreInfo, String closes) throws Exception {

        String jsonInStringForRequestBody = String.format(
                "{\"inventoryItemId\": %d, \"quantity\": %d, \"price\": %f, \"moreInfo\": \"%s\", \"closes\": \"%s\"}", inventoryItemId, quantity, price, moreInfo, closes);


        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/businesses/%d/listings", this.businessId))
                .content(jsonInStringForRequestBody)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The user will receive an Listing created successfully message and The Listing ID {string}")
    public void theUserWillReceiveAnListingCreatedSuccessfullyMessageAndTheListingID(String message) throws Exception {

        result.andExpect(status().is4xxClientError());
        result.andExpect(content().string(message));

    }


    @When("Create A Listing without closes: inventory item Id {int}, quantity {int}, price {double}, moreInfo {string}")
    public void createAListingWithoutClosesInventoryItemIdQuantityPriceMoreInfo(int inventoryItemId, int quantity, double price, String moreInfo) throws Exception {

        String jsonInStringForRequestBody = String.format(
                "{\"inventoryItemId\": %d, \"quantity\": %d, \"price\": %f, \"moreInfo\": \"%s\"}", inventoryItemId, quantity, price, moreInfo);

        result = mockMvc.perform(MockMvcRequestBuilders.post(String.format("/businesses/%d/listings", this.businessId))
                .content(jsonInStringForRequestBody)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }



}
