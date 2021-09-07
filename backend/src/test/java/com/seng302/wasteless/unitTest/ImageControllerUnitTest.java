package com.seng302.wasteless.unitTest;


import com.seng302.wasteless.controller.ImageController;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ImageService;
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
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    private ImageService imageService;

    private Business business;

    private User user;

    private Product productForImage;
    private Product productForImageLimit;
    private Product productForImageOneSpace;

    private Image image;
    private Image imageTwo;


    @BeforeEach
    void setUp() {

        productForImage = new Product();
        productForImage.setId("1-test-product");
        productForImage.setBusinessId(1);
        productForImage.setName("test-product");
        productForImage.setImages(new HashSet<>());

        image = new Image();
        image.setFileName("test");
        image.setThumbnailFilename("test_thumbnail");
        image.setId(1);

        imageTwo = new Image();
        imageTwo.setFileName("test2");
        imageTwo.setThumbnailFilename("test2_thumbnail");
        imageTwo.setId(2);

        Image imageThree = new Image();
        imageThree.setFileName("test3");
        imageThree.setThumbnailFilename("test3_thumbnail");
        imageThree.setId(3);

        Image imageFour = new Image();
        imageFour.setFileName("test4");
        imageFour.setThumbnailFilename("test4_thumbnail");
        imageFour.setId(4);

        Image imageFive = new Image();
        imageFive.setFileName("test5");
        imageFive.setThumbnailFilename("test5_thumbnail");
        imageFive.setId(5);

        productForImageLimit = new Product();
        productForImageLimit.setId("1-test-product-2");
        productForImageLimit.setBusinessId(1);
        productForImageLimit.setName("test-product-2");

        productForImageLimit.setPrimaryImage(image);

        Set<Image> imagesLimit = new HashSet<>();
        imagesLimit.add(image);
        imagesLimit.add(imageTwo);
        imagesLimit.add(imageThree);
        imagesLimit.add(imageFour);
        imagesLimit.add(imageFive);

        productForImageLimit.setImages(imagesLimit);

        productForImageOneSpace = new Product();
        productForImageOneSpace.setId("1-test-product-3");
        productForImageOneSpace.setBusinessId(1);
        productForImageOneSpace.setName("test-product-3");

        productForImageOneSpace.setPrimaryImage(image);

        Set<Image> imagesOneSpace = new HashSet<>();
        imagesOneSpace.add(image);
        imagesOneSpace.add(imageTwo);
        imagesOneSpace.add(imageThree);
        imagesOneSpace.add(imageFour);

        productForImageOneSpace.setImages(imagesOneSpace);


        user = mock(User.class);
        user.setId(1);
        user.setEmail("james@gmail.com");
        user.setRole(UserRoles.USER);

        Mockito.when(user.getProfileImage()).thenReturn(Mockito.mock(Image.class));

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
                .when(imageService.resizeImage(any(Image.class)))
                .thenReturn(target);

        Mockito
                .when(imageService.createImage(any(Image.class)))
                .thenReturn(image);

        Mockito
                .when(imageService.findImageById(2))
                .thenReturn(imageTwo);

        Mockito
                .when(userService.findUserById(1))
                .thenReturn(user);

        Mockito
                .when(imageService.saveImageWithThumbnail(any(MultipartFile.class)))
                .thenReturn(image);

        User requestedUser = Mockito.mock(User.class);
        Mockito.when(requestedUser.getId()).thenReturn(2);

        Mockito
                .when(userService.findUserById(2))
                .thenReturn(requestedUser);

        doCallRealMethod().when(productService).deleteImageRecordFromProductInDB(productForImage, imageTwo);


        doReturn(productForImage).when(productService).findProductById(productForImage.getId());
        doReturn(productForImageLimit).when(productService).findProductById(productForImageLimit.getId());
        doReturn(productForImageOneSpace).when(productService).findProductById(productForImageOneSpace.getId());


        //Request passed to controller is empty, could not tell you why, so the product id field is null.
        doReturn(productForImage).when(productService).findProductById(null);
        doReturn(productForImageLimit).when(productService).findProductById(null);
        doReturn(productForImageOneSpace).when(productService).findProductById(null);

        doReturn(true).when(business).checkUserIsPrimaryAdministrator(user);

        doReturn(true).when(business).checkUserIsAdministrator(user);

        doReturn(true).when(user).checkUserGlobalAdmin();

        doReturn(image).when(imageService).createImageFileName(any(Image.class), anyString());

        Mockito
                .when(userService.getUserToModify(anyInt()))
                .thenReturn(user);

    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andValidRequest_withTypeIsPNG_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/png" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andValidRequest_withTypeIsJPEG_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/jpeg" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andValidRequest_withTypeIsJPG_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/jpg" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andValidRequest_withTypeIsGIF_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/gif" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andInvalidFileType_then400Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.txt", "text/plain" ,"image example".getBytes());

        Mockito
                .when(imageService.saveImageWithThumbnail(any(MultipartFile.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Image type"));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andEmptyFile_then400Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/png" , (byte[]) null);

        Mockito
                .when(imageService.saveImageWithThumbnail(any(MultipartFile.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Image Received"));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andEmptyContentType_then400Response() throws Exception {

        Mockito
                .when(imageService.saveImageWithThumbnail(any(MultipartFile.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error with image type is null"));

        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", null ,"image example".getBytes());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product/images")
                .file(image))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToAddProductPrimaryImage_andValidRequest_then200Response() throws Exception {
        Set<Image> images = new HashSet<>();
        images.add(image);
        images.add(imageTwo);
        productForImage.setImages(images);
        productForImage.setPrimaryImage(image);

        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), image.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-test-product/images/2/makeprimary")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertNotEquals(productForImage.getPrimaryImage().getId(), image.getId());
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), imageTwo.getId());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToAddProductPrimaryImage_businessesIdNotFind_then400Response() throws Exception {
        Set<Image> images = new HashSet<>();
        images.add(image);
        images.add(imageTwo);
        productForImage.setImages(images);
        productForImage.setPrimaryImage(image);

        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), image.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/2/products/1-test-product/images/2/makeprimary")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), image.getId());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToAddProductPrimaryImage_productCodeNotFind_then400Response() throws Exception {
        Set<Image> images = new HashSet<>();
        images.add(image);
        images.add(imageTwo);
        productForImage.setImages(images);
        productForImage.setPrimaryImage(image);

        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), image.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/99-test-product/images/2/makeprimary")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), image.getId());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPutRequestToAddProductPrimaryImage_productImageIdNotFind_then406Response() throws Exception {
        Set<Image> images = new HashSet<>();
        images.add(image);
        images.add(imageTwo);
        productForImage.setImages(images);
        productForImage.setPrimaryImage(image);

        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), image.getId());
        mockMvc.perform(MockMvcRequestBuilders.put("/businesses/1/products/1-test-product/images/999/makeprimary")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
        Assertions.assertEquals(productForImage.getPrimaryImage().getId(), image.getId());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenDeleteRequestToDeleteProductImage_andValidRequest_then200Response() throws Exception {
        Set<Image> images = new HashSet<>();
        images.add(image);
        images.add(imageTwo);
        productForImage.setImages(images);
        productForImage.setPrimaryImage(image);

        Assertions.assertTrue(productForImage.getImages().stream().anyMatch(image -> image.getId().equals(imageTwo.getId())));
        mockMvc.perform(MockMvcRequestBuilders.delete("/businesses/1/products/1-test-product/images/2")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertFalse(productForImage.getImages().stream().anyMatch(image -> image.getId().equals(imageTwo.getId())));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenDeleteRequestToDeleteProductImage_businessesIdNotFind_then400Response() throws Exception {
        Set<Image> images = new HashSet<>();
        images.add(image);
        images.add(imageTwo);
        productForImage.setImages(images);
        productForImage.setPrimaryImage(image);

        Assertions.assertTrue(productForImage.getImages().stream().anyMatch(image -> image.getId().equals(imageTwo.getId())));
        mockMvc.perform(MockMvcRequestBuilders.delete("/businesses/2/products/1-test-product/images/2")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        Assertions.assertTrue(productForImage.getImages().stream().anyMatch(image -> image.getId().equals(imageTwo.getId())));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenDeleteRequestToDeleteProductImage_productImageIdNotFind_then406Response() throws Exception {
        Set<Image> images = new HashSet<>();
        images.add(image);
        images.add(imageTwo);
        productForImage.setImages(images);
        productForImage.setPrimaryImage(image);

        Assertions.assertTrue(productForImage.getImages().stream().anyMatch(image -> image.getId().equals(imageTwo.getId())));
        mockMvc.perform(MockMvcRequestBuilders.delete("/businesses/1/products/1-test-product/images/99")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
        Assertions.assertTrue(productForImage.getImages().stream().anyMatch(image -> image.getId().equals(imageTwo.getId())));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andLimitAlreadyMet_then400Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/png" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product-2/images")
                .file(image))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddProductImage_andOneImageCapacityLeft_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/png" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/businesses/1/products/1-test-product-3/images")
                .file(image))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddUserImage_andValidRequest_withTypeIsPNG_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/png" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/1/image")
                        .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddUserImage_andValidRequest_withTypeIsJPEG_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/jpeg" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/1/image")
                        .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddUserImage_andValidRequest_withTypeIsJPG_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/jpg" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/1/image")
                        .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddUserImage_andValidRequest_withTypeIsGIF_then201Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/gif" ,"image example".getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/1/image")
                        .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddUserImage_andInvalidFileType_then400Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.txt", "text/plain" ,"image example".getBytes());

        Mockito
                .when(imageService.saveImageWithThumbnail(any(MultipartFile.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Image type"));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/1/image")
                        .file(image))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddUserImage_andEmptyFile_then400Response() throws Exception {
        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", "image/png" , (byte[]) null);

        Mockito
                .when(imageService.saveImageWithThumbnail(any(MultipartFile.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Image Received"));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/1/image")
                        .file(image))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER") //Get past authentication being null
    void whenPostRequestToAddUserImage_andEmptyContentType_then400Response() throws Exception {

        Mockito
                .when(imageService.saveImageWithThumbnail(any(MultipartFile.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error with image type is null"));

        MockMultipartFile image = new MockMultipartFile("filename", "testImage.png", null ,"image example".getBytes());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/1/image")
                        .file(image))
                .andExpect(status().isBadRequest());
    }


    @Test
    void whenDeleteRequestToUserImage_andUserNotSelfOrAdmin_then403Response() throws Exception {
        Mockito.when(userService.getUserToModify(anyInt()))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to make change for this user"));


        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/2/image"))
                .andExpect(status().isForbidden());
    }


    @Test
    void whenDeleteRequestToUserImage_andUserHasNoImage_then404Response() throws Exception {
        Mockito.when(user.getProfileImage()).thenReturn(null);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/image"))
                .andExpect(status().isNotFound());
    }


    @Test
    void whenDeleteRequestToUserImage_andUserIsSelf_then200Response() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/image"))
                .andExpect(status().isOk());
    }


    @Test
    void whenDeleteRequestToUserImage_andUserIsGAA_then200Response() throws Exception {
        Mockito.when(user.checkUserGlobalAdmin()).thenReturn(true);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/image"))
                .andExpect(status().isOk());
    }
}
