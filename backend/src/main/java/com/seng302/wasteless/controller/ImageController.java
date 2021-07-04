package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.*;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {

    private static final Logger logger = LogManager.getLogger(ImageController.class.getName());


    private final BusinessService businessService;
    private final ProductImageService productImageService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ImageController(BusinessService businessService, ProductService productService, ProductImageService productImageService, UserService userService) {
        this.businessService = businessService;
        this.productService = productService;
        this.productImageService = productImageService;
        this.userService = userService;
    }

    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    public ResponseEntity<Object> postProductImage(@PathVariable("businessId") Integer businessId, @PathVariable("productId") String productId, @RequestParam("filename") MultipartFile file) {

        logger.info("Request to Create product: {} for business ID: {}", productId, businessId);
        logger.info("file: {}", file);

        ProductImage newImage = new ProductImage();
        newImage.setFileName("/images/test.png");
        newImage.setThumbnailFilename("/images/test_thumbnail.png");
        newImage = productImageService.createProductImage(newImage);
        Product product = productService.findProductById(productId);

        logger.info("Request to product: {}", product);

        productService.addImageToProduct(product, newImage.getId());
        productService.updateProduct(product);

//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/downloadFile/")
//                .path(newImage.getFileName())
//                .toUriString();

        JSONObject responseBody = new JSONObject();
        responseBody.put("imageId", newImage.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);

    }
    @DeleteMapping("/businesses/{businessId}/products/{productId}/images/{imageId}")
    public ResponseEntity<Object> postProductImage(@PathVariable("businessId") Integer businessId, @PathVariable("productId") String productId, @PathVariable("imageId") Integer imageId) {

        User user = userService.getCurrentlyLoggedInUser();
        logger.info("Got User {}", user);

        logger.debug("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (!possibleBusiness.checkUserIsAdministrator(user) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot delete productImage. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);

        ProductImage image = productImageService.findImageById(imageId);
        Product product = productService.findProductById(productId);
        productImageService.deleteImage(image);
        productService.removeImageFromProduct(product, imageId);
        productService.updateProduct(product);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
