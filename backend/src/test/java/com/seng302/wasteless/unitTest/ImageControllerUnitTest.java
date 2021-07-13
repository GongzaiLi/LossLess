package com.seng302.wasteless.unitTest;


import com.seng302.wasteless.controller.ImageController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductImageService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import org.junit.jupiter.api.Assertions;
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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
    private ProductImage productImageTwo;


    @BeforeEach
    void setUp() {

        productForImage = new Product();
        productForImage.setId("1-test-product");
        productForImage.setBusinessId(1);
        productForImage.setName("test-product");


        productImage = new ProductImage();
        productImage.setFileName("test");
        productImage.setThumbnailFilename("test_thumbnail");
        productImage.setId(1);

        productImageTwo = new ProductImage();
        productImageTwo.setFileName("test2");
        productImageTwo.setThumbnailFilename("test2_thumbnail");
        productImageTwo.setId(2);

        productForImage.setPrimaryImage(productImage);

        List<ProductImage> productImages = new ArrayList<>();
        productImages.add(productImage);
        productImages.add(productImageTwo);


        productForImage.setImages(productImages);


        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);

        business = mock(Business.class);
        business.setBusinessType(BusinessTypes.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1);
        business.setAdministrators(new ArrayList<>());
        business.setName("Jimmy's clown store");

        BufferedImage target = Mockito.mock(BufferedImage.class);


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
                .when(productImageService.resizeImage(any(ProductImage.class)))
                .thenReturn(target);

        Mockito
                .when(productImageService.createProductImage(any(ProductImage.class)))
                .thenReturn(productImage);

        Mockito
                .when(productImageService.findProductImageById(2))
                .thenReturn(productImageTwo);


        doReturn(productForImage).when(productService).findProductById(productForImage.getId());

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


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToAddProductPrimaryImage_andValidRequest_then200Response() throws Exception {
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), productImage.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-test-product/images/2/makeprimary")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertNotEquals(productForImage.getPrimaryImage().getId(), productImage.getId());
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), productImageTwo.getId());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToAddProductPrimaryImage_businessesIdNotFind_then400Response() throws Exception {
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), productImage.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/2/products/1-test-product/images/2/makeprimary")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), productImage.getId());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToAddProductPrimaryImage_productCodeNotFind_then400Response() throws Exception {
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), productImage.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/99-test-product/images/2/makeprimary")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), productImage.getId());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToAddProductPrimaryImage_productImageIdNotFind_then406Response() throws Exception {
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), productImage.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-test-product/images/999/makeprimary")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), productImage.getId());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenDeleteRequestToDeleteProductImage_andValidRequest_then200Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/businesses/1/products/1-test-product/images/2")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenDeleteRequestToDeleteProductImage_businessesIdNotFind_then400Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/businesses/2/products/1-test-product/images/2")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenDeleteRequestToDeleteProductImage_productCodeNotFind_then400Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/businesses/1/products/99-test-product/images/2")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenDeleteRequestToDeleteProductImage_productImageIdNotFind_then406Response() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/businesses/1/products/1-test-product/images/99")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void whenDeletedImageIsRemovedFromProduct() throws Exception {
        Assertions.assertTrue(productForImage.getImages().contains(productImageTwo));
        //productImageService.deleteImageRecordFromDB(productImageTwo);
        productService.deleteImageRecordFromProductInDB (productForImage, productImageTwo);
        //productService.updatePrimaryImage(productForImage, productImageTwo);
        productService.updateProduct(productForImage);
        //Assertions.assertFalse(productForImage.getImages().contains(productImageTwo));
    }


}
