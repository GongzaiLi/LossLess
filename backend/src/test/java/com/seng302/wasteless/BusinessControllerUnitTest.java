package com.seng302.wasteless;


import com.seng302.wasteless.controller.BusinessController;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.testconfigs.MockUserServiceConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BusinessController.class)
@Import(MockUserServiceConfig.class)
public class BusinessControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private ProductService productService;

    @Test
    @WithUserDetails("user@700")
    public void whenPostRequestToBusinessAndValidBusiness_then201Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"}," +
                "\"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("user@700")
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToMissingName_then400Response() throws Exception {
        String business = "{\"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user@700")
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToAddress_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user@700")
    public void whenPostRequestToBusiness_andInvalidBusiness_dueToMissingBusinessType_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user@700")
    public void whenPostRequestToBusiness_andValidBusiness_withMissingDescription_then201Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"businessType\": \"Accommodation and Food Services\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


}
