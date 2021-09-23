package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.PurchasedListing;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.hamcrest.CoreMatchers.is;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.time.LocalDate;
import java.util.List;

import static com.seng302.wasteless.TestUtils.newUserWithEmail;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
public class SalesReportFeature {
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
    private BusinessService businessService;

    @Autowired
    private PurchasedListingRepository purchasedListingRepository;

    private ResultActions responseResult;
    private static Boolean initialised = Boolean.FALSE;
    private static Product productToPurchase = new Product();

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


    @And("the following purchases have been made:")
    public void theFollowingPurchasesHaveBeenMade(List<List<String>> purchases) {
        purchasedListingRepository.deleteAll();

        for (var purchaseInfo : purchases) {
            PurchasedListing purchaseRecord = new PurchasedListing();
            purchaseRecord.setBusiness(businessService.findBusinessById(Integer.parseInt(purchaseInfo.get(0))));
            purchaseRecord.setPurchaser(userService.findUserById(Integer.parseInt(purchaseInfo.get(1))));
            purchaseRecord.setSaleDate(LocalDate.parse(purchaseInfo.get(2)));
            purchaseRecord.setNumberOfLikes(Integer.parseInt(purchaseInfo.get(3)));
            purchaseRecord.setListingDate(LocalDate.parse(purchaseInfo.get(4)));
            purchaseRecord.setClosingDate(LocalDate.parse(purchaseInfo.get(5)).atTime(23,59));
            purchaseRecord.setProduct(productToPurchase);
            purchaseRecord.setManufacturer(productToPurchase.getManufacturer());
            purchaseRecord.setQuantity(Integer.parseInt(purchaseInfo.get(7)));
            purchaseRecord.setPrice(Double.parseDouble(purchaseInfo.get(8)));
            purchasedListingRepository.save(purchaseRecord);
        }
    }

    @When("I search for a sales report starting {string} and ending {string} with period {string}")
    public void iSearchForASalesReportStartingAndEndingWithPeriod(String startDate, String endDate, String period) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases")
                .with(user(currentUserDetails))
                .queryParam("startDate",startDate)
                .queryParam("endDate",endDate)
                .queryParam("period", period)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Then("{int} items are returned")
    public void itemsAreReturned(int numItems) throws Exception {
        responseResult.andExpect(jsonPath("$", hasSize(numItems)));

    }

    @When("I search for a sales report starting {string} and ending {string} with no period specified")
    public void iSearchForASalesReportStartingAndEndingWithNoPeriodSpecified(String startDate, String endDate) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/totalPurchases")
                .with(user(currentUserDetails))
                .queryParam("startDate",startDate)
                .queryParam("endDate",endDate)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Then("I get a response with:")
    public void iGetAResponseWith(List<List<String>> purchases) throws Exception {
        List<String> purchaseArray = purchases.get(0);
        responseResult
                .andExpect(jsonPath("[0].startDate", is(purchaseArray.get(0))))
                .andExpect(jsonPath("[0].endDate", is(purchaseArray.get(1))))
                .andExpect(jsonPath("[0].totalPurchases", is(Integer.parseInt(purchaseArray.get(2)))))
                .andExpect(jsonPath("[0].totalValue", is(Double.parseDouble(purchaseArray.get(3)))));
    }


    @And("The following product {string} exists")
    public void theFollowingProductExists(String product) {
        if (productToPurchase.getName()!=product) {
            Product newProduct = new Product();
            newProduct.setName(product);
            newProduct.setManufacturer("manufacturer");
            productToPurchase = productService.createProduct(newProduct);
        }
}

    @Given("We are logged in as the individual user with email  {string}")
    public void weAreLoggedInAsTheIndividualUserWithEmail(String email) {
        User currentUser = userService.findUserByEmail(email);

        if (currentUser == null) {
            currentUser = newUserWithEmail(email);
            addressService.createAddress(currentUser.getHomeAddress());
            userService.createUser(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
    }

    @Then("The following is returned:")
    public void theFollowingIsReturned(List<List<String>> purchases) throws Exception {
        int i = 0;
        for (List<String> purchaseArray:purchases) {
            responseResult
                    .andExpect(jsonPath("["+i+"].startDate", is(purchaseArray.get(0))))
                    .andExpect(jsonPath("["+i+"].endDate", is(purchaseArray.get(1))))
                    .andExpect(jsonPath("["+i+"].totalPurchases", is(Integer.parseInt(purchaseArray.get(2)))))
                    .andExpect(jsonPath("["+i+"].totalValue", is(Double.parseDouble(purchaseArray.get(3)))));
            i++;
        }
    }

    @When("I view the extended sales report starting {string} and ending {string}")
    public void iViewTheExtendedSalesReportStartingAndEnding(String startDate, String endDate) throws Exception {
        responseResult = mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/salesReport/listingDurations")
                .with(user(currentUserDetails))
                .queryParam("startDate",startDate)
                .queryParam("endDate",endDate)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Then("The counts of listings grouped by duration are:")
    public void theCountsOfListingsGroupedByDurationAre(List<List<String>> counts) throws Exception {
        for (List<String> count : counts) {
            responseResult.andExpect(jsonPath(count.get(0), is(Integer.parseInt(count.get(1)))));
        }
    }
}

