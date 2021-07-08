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

import java.util.Arrays;

@RestController
public class ImageController {

    private static final Logger logger = LogManager.getLogger(ImageController.class.getName());

    private final UserService userService;
    private final BusinessService businessService;
    private final ProductImageService productImageService;
    private final ProductService productService;

    @Autowired
    public ImageController(BusinessService businessService, ProductService productService, ProductImageService productImageService, UserService userService) {
        this.userService = userService;
        this.businessService = businessService;
        this.productService = productService;
        this.productImageService = productImageService;
    }

    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    public ResponseEntity<Object> postProductImage(@PathVariable("businessId") Integer businessId, @PathVariable("productId") String productId, @RequestParam("filename") MultipartFile file) {

        logger.info("Request to Create product: {} for business ID: {}", productId, businessId);

        User user = userService.getCurrentlyLoggedInUser();

        logger.info("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Cannot post product image. Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);

        if (!possibleBusiness.checkUserIsAdministrator(user) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot post product image. User: {} is not global admin or business admin: {}", user.getId(), businessId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user.getId(), businessId);

        logger.info("Check if product with id ` {} ` exists on for business with id ` {} ` ", productId, businessId);
        Product possibleProduct = productService.findProductById(productId);

        if (possibleProduct == null) {
            logger.warn("Cannot post product image for product that does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product with given id does not exist");
        }
        if (!possibleProduct.getBusinessId().equals(businessId)) {
            logger.warn("Cannot post product image for product that does not belong to current business");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product id does not exist for Current Business");
        }

        if (file.isEmpty()) {
            logger.warn("Cannot post product image, no image received");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Image Received");
        }

        ProductImage newImage = new ProductImage();
        String imageType;

        String fileContentType = file.getContentType();
        if (fileContentType != null && fileContentType.contains("/")) {
            imageType = fileContentType.split("/")[1];
        } else {
            logger.debug("Error with image type is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error with image type is null");
        }

        if (!Arrays.asList("png", "jpeg", "jpg", "gif").contains(imageType)) {
            logger.warn("Cannot post product image, invalid image type");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Image type");
        }

        newImage = productImageService.createImageFileName(newImage, imageType);

        if (Boolean.FALSE.equals(productImageService.storeImage(newImage.getFileName(), file))) {
            logger.debug("Error with creating directory or saving file {}", file);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error with creating directory");
        }

        newImage = productImageService.createProductImage(newImage);
        Product product = productService.findProductById(productId);

        productService.addImageToProduct(product, newImage);
        productService.updateProduct(product);

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

        if (product==null){
            logger.warn("Cannot delete productImage. Product is null");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Product no longer exists");
        }
        if (image==null){
            logger.warn("Cannot delete productImage. image: is null", image, product);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Image no longer exists");
        }

        productService.removeImageFromProduct(product, image);
        productService.updateProduct(product);
        productImageService.deleteImage(image);
        productImageService.deleteImageFile(image);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
