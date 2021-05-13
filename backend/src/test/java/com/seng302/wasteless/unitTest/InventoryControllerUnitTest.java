package com.seng302.wasteless.unitTest;

import com.seng302.wasteless.controller.InventoryController;
import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.dto.mapper.PostInventoryDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.InventoryService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false) //Disable spring security for the unit tests
public class InventoryControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private ProductService productService;

    @MockBean
    private InventoryService inventoryService;

    private Business business;

    private User user;

    @BeforeAll
    static void beforeAll() {
        //This line is important, do not remove
        mockStatic(PostInventoryDtoMapper.class);
    }

    @BeforeEach
    void setUp() {

        Product productForInventory = new Product();
        productForInventory.setId("Clown-Shows");
        productForInventory.setBusinessId(1);
        productForInventory.setName("Clown Shows");

        LocalDate today = LocalDate.now();

        Inventory inventoryItem = new Inventory();
        inventoryItem.setExpires(today.plusMonths(2))
                .setTotalPrice(50)
                .setPricePerItem(10)
                .setSellBy(today.plusMonths(1))
                .setQuantity(5)
                .setManufactured(today.minusMonths(1))
                .setBestBefore(today.plusMonths(1))
                .setProduct(productForInventory);

        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);

        business = mock(Business.class);
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1);
        business.setAdministrators(new ArrayList<>());
        business.setName("Jimmy's clown store");

        Product product = new Product();
        product.setId("Clown-Shows");
        product.setBusinessId(1);
        product.setName("Clown Shows");

        Mockito
                .when(authentication.getName())
                .thenReturn("james@gmail.com");

        Mockito
                .when(inventoryService.createInventory(any(Inventory.class)))
                .thenReturn(inventoryItem.setId(2L));

        Mockito
                .when(userService.findUserByEmail(anyString()))
                .thenReturn(user);

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(business);

        doReturn(product).when(productService).findProductById(anyString());

        //Request passed to controller is empty, could not tell you why, so the product id field is null.
        doReturn(product).when(productService).findProductById(null);

        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);

        doReturn(true).when(business).checkUserIsAdministrator(user);

        doReturn(true).when(user).checkUserGlobalAdmin();

        Mockito
                .when(PostInventoryDtoMapper.postInventoryDtoToEntityMapper(any(PostInventoryDto.class)))
                .thenReturn(inventoryItem);
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateInventory_andValidRequest_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"productId\": \"WATT-420-BEANS\", \"quantity\": 4, \"pricePerItem\": 6.5, \"totalPrice\": 21.99, \"manufactured\": \"2021-05-12\", \"sellBy\": \"2021-05-12\",\"bestBefore\": \"2021-05-12\",\"expires\": \"2021-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("inventoryItemId", is(2)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateInventory_andInvalidRequest_BecauseNoUserMakingRequest_the401Response() throws Exception {

        Mockito
                .when(userService.findUserByEmail(anyString()))
                .thenReturn(null);

        String jsonInStringForRequest = "{\"productId\": \"WATT-420-BEANS\", \"quantity\": 4, \"pricePerItem\": 6.5, \"totalPrice\": 21.99, \"manufactured\": \"2021-05-12\", \"sellBy\": \"2021-05-12\",\"bestBefore\": \"2021-05-12\",\"expires\": \"2021-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateInventory_andInvalidRequest_BecauseNoMatchingBusiness_the406Response() throws Exception {

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(null);

        String jsonInStringForRequest = "{\"productId\": \"WATT-420-BEANS\", \"quantity\": 4, \"pricePerItem\": 6.5, \"totalPrice\": 21.99, \"manufactured\": \"2021-05-12\", \"sellBy\": \"2021-05-12\",\"bestBefore\": \"2021-05-12\",\"expires\": \"2021-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateInventory_andValidRequest_UserBusinessAdminButNotDGAA_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"productId\": \"WATT-420-BEANS\", \"quantity\": 4, \"pricePerItem\": 6.5, \"totalPrice\": 21.99, \"manufactured\": \"2021-05-12\", \"sellBy\": \"2021-05-12\",\"bestBefore\": \"2021-05-12\",\"expires\": \"2021-05-12\"}";

        doReturn(false).when(user).checkUserGlobalAdmin();

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("inventoryItemId", is(2)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateInventory_andValidRequest_UserDGAAButNotBusinessAdmin_then201Response() throws Exception {
        String jsonInStringForRequest = "{\"productId\": \"WATT-420-BEANS\", \"quantity\": 4, \"pricePerItem\": 6.5, \"totalPrice\": 21.99, \"manufactured\": \"2021-05-12\", \"sellBy\": \"2021-05-12\",\"bestBefore\": \"2021-05-12\",\"expires\": \"2021-05-12\"}";

        doReturn(false).when(business).checkUserIsPrimaryAdministrator(user);

        doReturn(false).when(business).checkUserIsAdministrator(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("inventoryItemId", is(2)));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateInventory_andInvalidRequest_BecauseUserIsNotAdminOrDGAA_the403Response() throws Exception {

        doReturn(false).when(business).checkUserIsPrimaryAdministrator(user);

        doReturn(false).when(business).checkUserIsAdministrator(user);

        doReturn(false).when(user).checkUserGlobalAdmin();

        String jsonInStringForRequest = "{\"productId\": \"WATT-420-BEANS\", \"quantity\": 4, \"pricePerItem\": 6.5, \"totalPrice\": 21.99, \"manufactured\": \"2021-05-12\", \"sellBy\": \"2021-05-12\",\"bestBefore\": \"2021-05-12\",\"expires\": \"2021-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    public void whenPostRequestToCreateInventory_andInvalidRequest_BecauseNoMatchingProduct_the400Response() throws Exception {

        doReturn(null).when(productService).findProductById(anyString());

        //Request passed to controller is empty, could not tell you why, so the product id field is null.
        doReturn(null).when(productService).findProductById(null);

        String jsonInStringForRequest = "{\"productId\": \"WATT-420-BEANS\", \"quantity\": 4, \"pricePerItem\": 6.5, \"totalPrice\": 21.99, \"manufactured\": \"2021-05-12\", \"sellBy\": \"2021-05-12\",\"bestBefore\": \"2021-05-12\",\"expires\": \"2021-05-12\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/businesses/1/inventory")
                .content(jsonInStringForRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
