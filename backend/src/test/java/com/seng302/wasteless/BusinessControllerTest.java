package com.seng302.wasteless;

import static org.hamcrest.CoreMatchers.is;

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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Reset JPA stuff between test
public class BusinessControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessRepository businessRepository;

    @Test
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
    public void whenGetRequestToBusinessAndBusinessNotExists_then406Response() throws Exception {
        createOneBusiness("Business", "Location", "Accommodation and Food Services", "I am a business");

        mockMvc.perform(get("/businesses/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
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
    public void whenPostRequestToBusinessAndValidBusiness_then201Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : \"Peanut Lane\", \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToMissingName_then400Response() throws Exception {
        String business = "{\"address\" : \"Peanut Lane\", \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToAddress_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToMissingBusinessType_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : \"Peanut Lane\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenPostRequestToBusiness_andValidBusiness_withMissingDescription_then201Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : \"Peanut Lane\", \"businessType\": \"Accommodation and Food Services\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
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
