package com.seng302.wasteless.unitTest;


import com.seng302.wasteless.controller.ImageController;
import com.seng302.wasteless.controller.InventoryController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductImageService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false) //Disable spring security for the unit tests
class ImageControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductImageService productImageService;

    private Business business;

    private User user;

    private Product productForImage;

    private ProductImage productImage;


    @BeforeEach
    void setUp() {

        productForImage = new Product();
        productForImage.setId("1-test-product");
        productForImage.setBusinessId(1);
        productForImage.setName("test-product");


        productImage = new ProductImage();
        productImage.setFileName("test");
        productImage.setThumbnailFilename("test");
        productImage.setId(1);




        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);

        business = mock(Business.class);
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1);
        business.setAdministrators(new ArrayList<>());
        business.setName("Jimmy's clown store");



        Mockito
                .when(authentication.getName())
                .thenReturn("james@gmail.com");

        Mockito
                .when(userService.getCurrentlyLoggedInUser())
                .thenReturn(user);

        Mockito
                .when(businessService.findBusinessById(anyInt()))
                .thenReturn(business);

        Mockito
                .when(productImageService.storeImage(anyString(), any(MultipartFile.class)))
                .thenReturn(true);


        Mockito
                .when(productImageService.createProductImage(any(ProductImage.class)))
                .thenReturn(productImage);




        doReturn(productForImage).when(productService).findProductById(anyString());

        //Request passed to controller is empty, could not tell you why, so the product id field is null.
        doReturn(productForImage).when(productService).findProductById(null);

        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);

        doReturn(true).when(business).checkUserIsAdministrator(user);

        doReturn(true).when(user).checkUserGlobalAdmin();

        doReturn(productImage).when(productImageService).createImageFileName(any(ProductImage.class), anyString());



    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andValidRequest_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/png" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andInvalidFileType_then400Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.txt", "text/plain" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andEmptyFile_then400Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/png" , (byte[]) null);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andEmptyContentType_then400Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", null ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isBadRequest());
    }

}
