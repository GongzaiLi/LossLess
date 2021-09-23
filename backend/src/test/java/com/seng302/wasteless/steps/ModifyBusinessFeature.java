package com.seng302.wasteless.steps;

import com.seng302.wasteless.controller.ListingController;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static com.seng302.wasteless.TestUtils.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(ListingController.class)
@AutoConfigureWebMvc
public class ModifyBusinessFeature {

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

    private ResultActions responseResult;

    private User currentUser;

    private Business currentBusiness;

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

    @Given("A user is logged on with the email {string}")
    public void aUserIsLoggedOnWithTheEmail(String email) {
        currentUser = userService.findUserByEmail(email);

        if (currentUser == null) {
            currentUser = newUserWithEmail(email);
            addressService.createAddress(currentUser.getHomeAddress());
            userService.createUser(currentUser);
        }

        currentUserDetails = new CustomUserDetails(currentUser);
    }

    @And("The user is an administrator for a business with name {string}")
    public void theUserIsAnAdministratorForABusinessWithName(String name) {
        if (currentBusiness == null) {
            currentBusiness = (newBusinessWithName(name));
            addressService.createAddress(currentBusiness.getAddress());
            currentBusiness.setAdministrators(Collections.singletonList(currentUser));
            currentBusiness.setPrimaryAdministrator(currentUser);
            businessService.createBusiness(currentBusiness);
        }

        if (!currentBusiness.checkUserIsAdministrator(currentUser)) {
            currentUser.addPrimaryBusiness(currentBusiness);
            currentBusiness.getAdministrators().add(currentUser);
        }
    }

    @When("The User modifies his business with name: {string}, description: {string}, business type: {string}, address with the details, country {string},  streetNumber {string},  streetName {string},  suburb {string}, city {string},  region {string},  postcode {string}")
    public void theUserModifiesHisBusinessWithNameDescriptionBusinessTypeAddressWithTheDetailsCountryStreetNumberStreetNameSuburbCityRegionPostcode(String name, String description, String type, String country, String streetNumber, String streetName, String suburb, String city, String region, String postcode) throws Exception {
        String jsonInStringForRequest = String.format("{\"name\": \"%s\", \"description\": \"%s\", \"businessType\": \"%s\", " +
                        "\"address\": {\n \"country\": \"%s\", \"streetNumber\": \"%s\", \"streetName\": \"%s\", \"suburb\": \"%s\", \"city\": \"%s\", \"region\": \"%s\", \"postcode\": \"%s\"}}",
                name, description, type,  country, streetNumber, streetName, suburb, city, region, postcode);
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/businesses/" + currentBusiness.getId())
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The business is modified with an ok result")
    public void theBusinessIsModifiedWithAnOkResult() throws Exception {
        responseResult.andExpect(status().isOk());
    }

    @When("The User modifies the business who he is not an admin for with name: {string}")
    public void theUserModifiesTheBusinessWhoHeIsNotAnAdminForWithName(String name) throws Exception {
        String jsonInStringForRequest = String.format("{\"name\": \"%s\", \"description\": \"description\", \"businessType\": \"Charitable organisation\", " +
                        "\"address\": {\n \"country\": \"country\", \"streetNumber\": \"streetNumber\", \"streetName\": \"streetName\", \"suburb\": \"suburb\", \"city\": \"city\", \"region\": \"region\", \"postcode\": \"postcode\"}}",
                name);
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/businesses/" + currentBusiness.getId())
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The User who is modifying will receive a forbidden error")
    public void theUserWhoIsModifyingWillReceiveAForbiddenError() throws Exception {
        responseResult.andExpect(status().isForbidden());
    }

    @When("The User modifies his business with an invalid business type: {string}")
    public void theUserModifiesHisBusinessWithAnInvalidBusinessType(String businessType) throws Exception {
        String jsonInStringForRequest = String.format("{\"name\": \"name\", \"description\": \"description\", \"businessType\": \"%s\", " +
                        "\"address\": {\n \"country\": \"country\", \"streetNumber\": \"streetNumber\", \"streetName\": \"streetName\", \"suburb\": \"suburb\", \"city\": \"city\", \"region\": \"region\", \"postcode\": \"postcode\"}}",
                businessType);
        responseResult = mockMvc.perform(MockMvcRequestBuilders.put("/businesses/" + currentBusiness.getId())
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON)
                .with(user(currentUserDetails))
                .with(csrf()));
    }

    @Then("The User who is modifying the business will receive an error")
    public void theUserWhoIsModifyingTheBusinessWillReceiveAnError() throws Exception {
        responseResult.andExpect(status().isBadRequest());
    }

}
