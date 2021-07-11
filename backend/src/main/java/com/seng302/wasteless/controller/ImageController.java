package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductImageService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.validation.ConstraintViolationException;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for dealing with images
 */

@RestController
public class ImageController {

    private static final Logger logger = LogManager.getLogger(ImageController.class.getName());

    private final UserService userService;
    private final BusinessService businessService;
    private final ProductImageService productImageService;
    private final ProductService productService;

    private ServletContext servletContext;

    @Autowired
    public ImageController(BusinessService businessService, ProductService productService, ProductImageService productImageService, UserService userService) {
        this.userService = userService;
        this.businessService = businessService;
        this.productService = productService;
        this.productImageService = productImageService;
    }

    /**
     * Upload image
     * Takes given image and uploads it to given product.
     * <p>
     * Saves image to media/images folder in the backend
     * <p>
     * Returns:
     * NOT_ACCEPTABLE 406 If making request doesnt exist
     * FORBIDDEN 403 If user not allowed to make request (not global admin or business admin)
     * 400 BAD_REQUEST If invalid image type, product doesnt exist, product doesnt belong to business, or no image
     * 201 Created If successfully uploaded and saved image
     * 500 If server error saving image
     * <p>
     * If first image uploaded for a given product, sets image as products primary image.
     *
     * @param businessId The Id of the business that has the product to upload the image for
     * @param productId  The Id of the product to upload the image for
     * @param file       The image file to upload
     * @return Status code dependent on success. 406, 403, 400, 500 errors. 201 Created with image id if success.
     */
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

        productImageService.storeImage(newImage.getFileName(), file);


        BufferedImage thumbnail = productImageService.resizeImage(newImage);
        if (thumbnail == null) {
            logger.debug("Error resizing image");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resizing file");
        }

        productImageService.storeThumbnailImage(newImage.getThumbnailFilename(), imageType, thumbnail);
        newImage = productImageService.createProductImage(newImage);
        logger.debug("Created new image entity {}", newImage);


        Product product = productService.findProductById(productId);
        logger.info("Retrieved product with ID: {}", product.getId());


        productService.addImageToProduct(product, newImage);


        if (product.getPrimaryImageId() == null) {
            logger.info("No primary image found for product with ID: {} in th database", product.getId());
            product.setPrimaryImageId(newImage.getId());
            logger.info("Set image with ID: {} to product with ID: {} in the database", newImage.getId(), product.getId());
        }

        productService.updateProduct(product);
        logger.info("Saved image with ID: {} to product with ID: {} in the database", newImage.getId(), product.getId());


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


    /**
     * Get any image from the media/images file given its file
     *
     * Removing leading '/' from file path e.g. media/images/a.png
     * otherwise the image cannot be found
     *
     * @param filename  The name of the file to get
     * @return          The image
     */
    @ResponseBody
    @RequestMapping(value = "/images", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@RequestBody String filename) throws IOException {
        InputStream is = new FileInputStream(filename);
        return IOUtils.toByteArray(is);
    }

    /**
     * Returns Json message detailing IOException error, used for bad image filenames
     *
     * @param exception The exception thrown by Spring when it detects IOException
     * @return Map of field name that had the error and a message describing the error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IOException.class)
    public Map<String, String> handleValidationExceptions(
            IOException exception) {

        Map<String, String> errors = new HashMap<>();

        String errorMsg = exception.getMessage();

        errors.put("Error", errorMsg);
        return errors;
    }
}
