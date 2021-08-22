package com.seng302.wasteless.unitTest;


import com.seng302.wasteless.controller.BusinessController;
import com.seng302.wasteless.service.AddressService;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.testconfigs.MockBusinessServiceConfig;
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


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BusinessController.class)
@Import({MockUserServiceConfig.class, MockBusinessServiceConfig.class})
class BusinessControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessService businessService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private ProductService productService;


    @Test
    @WithUserDetails("user@700")
    void whenPostRequestToBusinessAndValidBusiness_then201Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"}," +
                "\"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("user@700")
    void whenPostRequestToBusiness_andInvalidBusiness_dueToMissingName_then400Response() throws Exception {
        String business = "{\"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user3@700")
    void whenPostRequestToBusiness_andUserUnder16_then400Response() throws Exception {

        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"}," +
                "\"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user@700")
    void whenPostRequestToBusiness_andInvalidBusiness_dueToAddress_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"businessType\": \"Accommodation and Food Services\", \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user@700")
    void whenPostRequestToBusiness_andInvalidBusiness_dueToMissingBusinessType_then400Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"description\": \"We sell peanuts\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user@700")
    void whenPostRequestToBusiness_andValidBusiness_withMissingDescription_then201Response() throws Exception {
        String business = "{\"name\": \"James's Peanut Store\", \"address\" : {\n" +
                "    \"streetNumber\": \"3/24\",\n" +
                "    \"streetName\": \"Ilam Road\",\n" +
                "    \"suburb\": \"Riccarton\",\n" +
                "    \"city\": \"Christchurch\",\n" +
                "    \"region\": \"Canterbury\",\n" +
                "    \"country\": \"New Zealand\",\n" +
                "    \"postcode\": \"90210\"\n" +
                "  }, \"businessType\": \"Accommodation and Food Services\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses")
                        .with(csrf())
                        .content(business)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Test
    @WithUserDetails("user@700")
    void whenPutRequestToBusinessMakeAdmin_andValidRequest_then200Response() throws Exception {
        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/0/makeAdministrator")
                        .with(csrf())
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("user2@700")
    void whenPutRequestToBusinessMakeAdmin_andUserNotAllowedToMakeRequest_then403Response() throws Exception {
        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/0/makeAdministrator")
                        .with(csrf())
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithUserDetails("user@700")
    void whenPutRequestToBusinessMakeAdmin_andBusinessDoesntExist_then406Response() throws Exception {
        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/3/makeAdministrator")
                        .with(csrf())
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    @WithUserDetails("admin@700")
    void whenPutRequestToBusinessMakeAdmin_andUserAllowedToMakeRequest_BecauseGlobalApplicationAdmin_then200Response() throws Exception {
        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/0/makeAdministrator")
                        .with(csrf())
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("defaultadmin@700")
    void whenPutRequestToBusinessMakeAdmin_andUserAllowedToMakeRequest_BecauseDefaultGlobalApplicationAdmin_then200Response() throws Exception {
        String request = "{\"userId\": \"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/0/makeAdministrator")
                        .with(csrf())
                        .content(request)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
