package com.seng302.wasteless;

import com.seng302.wasteless.User.UserRoles;
import com.seng302.wasteless.testconfigs.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Remove security
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Reset JPA between test
public class BusinessControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndBusinessExists_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Business")))
                .andExpect(jsonPath("description", is("I am a business")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndBusinessNotExists_then406Response() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenGetRequestToBusinessAndMultipleBusinessExists_thenCorrectBusiness() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneBusiness("Business2", "Location2", "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")));
    }

    @Test
    @WithMockCustomUser(email = "user@test.com", role = UserRoles.USER)
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToIllegalBusinessType_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : \"Peanut Lane\", \"businessType\": \"Oil Company\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private void createOneBusiness(String name, String address, String businessType, String description) {

        String business = String.format("{\"name\": \"%s\", \"address\" : \"%s\", \"businessType\": \"%s\", \"description\": \"%s\"}", name, address, businessType, description);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                    .content(business)
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }


}
