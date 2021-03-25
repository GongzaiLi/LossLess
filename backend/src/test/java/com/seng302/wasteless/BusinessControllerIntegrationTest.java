package com.seng302.wasteless;

import com.seng302.wasteless.Business.BusinessRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Reset JPA stuff between test
public class BusinessControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessRepository businessRepository;

    @Test
    public void whenGetRequestToBusinessAndBusinessExists_thenCorrectBusiness() throws Exception {
        Cookie accessCookie = createUserForAccessCookie();
        createOneBusiness(accessCookie, "Business", "Location", "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/1")
                .cookie(accessCookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Business")))
                .andExpect(jsonPath("description", is("I am a business")));
    }

    @Test
    public void whenGetRequestToBusinessAndBusinessNotExists_then406Response() throws Exception {
        Cookie accessCookie = createUserForAccessCookie();
        createOneBusiness(accessCookie,"Business", "Location", "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/2")
                .cookie(accessCookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void whenGetRequestToBusinessAndMultipleBusinessExists_thenCorrectBusiness() throws Exception {
        Cookie accessCookie = createUserForAccessCookie();
        createOneBusiness(accessCookie,"Business", "Location", "Accommodation and Food Services", "I am a business");
        createOneBusiness(accessCookie,"Business2", "Location2", "Non-profit organisation", "I am a business 2");

        mockMvc.perform(get("/businesses/2")
                .cookie(accessCookie)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Business2")))
                .andExpect(jsonPath("description", is("I am a business 2")));
    }

    @Test
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToIllegalBusinessType_then400Response() throws Exception {
        Cookie accessCookie = createUserForAccessCookie();
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : \"Peanut Lane\", \"businessType\": \"Oil Company\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .cookie(accessCookie)
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private Cookie createUserForAccessCookie() {
        String user = "{\"firstName\": \"TestUser\", \"lastName\" : \"Test\", \"address\": \"Test\",  \"email\": \"123\", \"dateOfBirth\": \"2000-10-27\", \"homeAddress\": \"Somewhere\", \"password\": \"ssss\"}";

        try {
            MvcResult result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/users")
                            .content(user)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            return result.getResponse().getCookie("JSESSIONID");

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }


    private void createOneBusiness(Cookie cookie, String name, String address, String businessType, String description) {

        String business = String.format("{\"name\": \"%s\", \"address\" : \"%s\", \"businessType\": \"%s\", \"description\": \"%s\"}", name, address, businessType, description);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                    .cookie(cookie)
                    .content(business)
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isCreated());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Request", e);
        }
    }


}
