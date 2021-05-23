package com.seng302.wasteless.steps;

import com.jayway.jsonpath.JsonPath;
import com.seng302.wasteless.controller.UserController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.UserRepository;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.testconfigs.MockitoUserServiceConfig;
import com.seng302.wasteless.testconfigs.WithMockCustomUserSecurityContextFactory;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import com.seng302.wasteless.steps.LoginFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import com.seng302.wasteless.testconfigs.WithMockCustomUserSecurityContextFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
public class CreateInventoryFeature {

    private MockMvc mockMvc;

    private CustomUserDetails currentUserDetails;

    private ResultActions result;

    private Long userBusinessId;
    
    private Address throwawayAddress;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private BusinessService businessService;

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
     * Creates the user with the given email, then creates sets the currentUserDetails object
     * from that user. The currentUserDetails object should be used when making mockmvc requests, so that
     * you are 'logged in' as that user.
     * See https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html#test-mockmvc-securitycontextholder-rpp
     * @param email Email of the user
     */
    @Given("We are logged in as the user {string}")
    public void weAreLoggedInAsTheUser(String email) {
        User currentUser = new User();
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
        currentUserDetails = new CustomUserDetails(currentUser);
    }

    /**
     * Creates a business with the given id, then makes the user with the given email
     * a admin of that business. The user should exist first.
     */
    @And("The user {string} is an administrator for business {int}")
    public void theUserIsAnAdministratorForBusiness(String email, int id) {
        User user = userService.findUserByEmail(email);

        Business business = new Business();
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(id);
        business.setAdministrators(Collections.singletonList(user));
        business.setName("Jimmy's clown store");
        business.setAddress(throwawayAddress);

        businessService.createBusiness(business);
        user.addPrimaryBusiness(business);
    }

    @And("The user {string} is not an administrator for business {int}")
    public void theUserIsNotAnAdministratorForBusiness(String email, int id) {
        Business business = businessService.findBusinessById(id);
        User user = userService.findUserByEmail(email);
        Assertions.assertFalse(business.checkUserIsAdministrator(user));
    }

    @When("The user accesses the inventory for business {int}")
    public void theUserAccessesTheInventoryForBusiness(int id) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/businesses/%d/inventory", id))
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails)));
    }

    @Then("The user will be able to see the inventory")
    public void the_user_will_be_able_to_see_the_inventory() throws Exception {
        result.andExpect(status().isOk());
    }

    @Then("The user cannot see the inventory")
    public void the_user_cannot_see_the_inventory() throws Exception {
        result.andExpect(status().isForbidden());
    }
}
