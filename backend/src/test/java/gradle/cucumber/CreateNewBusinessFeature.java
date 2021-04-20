package gradle.cucumber;

import com.seng302.wasteless.Main;
import com.seng302.wasteless.MainApplicationRunner;
import com.seng302.wasteless.controller.BusinessController;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.testconfigs.MockBusinessServiceConfig;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Cucumber.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Remove security
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset JPA between test
public class CreateNewBusinessFeature {
    private String name;
    private String description;
    private String type;
    private String streetNumber;
    private String streetName;
    private String city;
    private String country;
    private String postcode;

    @Autowired
    private MockMvc mockMvc;
/*
    @Autowired
    private BusinessService businessService;
*/
    @Given("There is no business with name {string}")
    public void there_is_no_business_with_name(String name) {
        // Write code here that turns the phrase above into concrete actions
        //Assertions.assertNull(businessService.findBusinessByName(name));
    }

    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    @When("I create a business with name {string}, description {string}, type of business {string}, street number {string}, street name {string}, city {string}, country {string}, postcode {string}")
    public void i_create_a_business_with_name_description_street_number_type_of_business_street_name_city_country_postcode(String name, String description, String businessType, String streetNumber, String streetName, String city, String country, String postcode) throws Exception {
        String business = String.format("{\"name\": \"%s\", \"address\" : {\"%s\"}, \"businessType\": \"%s\", \"description\": \"%s\"}", name, "AAAAAAAAA", businessType, description);

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.name = name;
        this.description = description;
        this.type = businessType;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
    }

    @Then("The event is created with the correct name, description, address and type")
    public void the_event_is_created_with_the_correct_name_description_address_and_type() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
