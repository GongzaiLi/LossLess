package com.seng302.wasteless;


import com.seng302.wasteless.Business.BusinessController;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class BusinessControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessController businessController;

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


}
