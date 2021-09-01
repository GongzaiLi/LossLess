package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.Image;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ImageService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


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
    private final ImageService imageService;
    private final ProductService productService;



    @Autowired
    public ImageController(BusinessService businessService, ProductService productService, ImageService imageService, UserService userService) {
        this.userService = userService;
        this.businessService = businessService;
        this.productService = productService;
        this.imageService = imageService;
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

        logger.info("Request to create product image: {} for business ID: {}", productId, businessId);

        User user = userService.getCurrentlyLoggedInUser();

        Business possibleBusiness = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);

        Product possibleProduct = productService.findProductById(productId);

        productService.checkProductBelongsToBusiness(possibleProduct, businessId);

        if (file.isEmpty()) {
            logger.warn("Cannot post product image, no image received");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Image Received");
        }

        if (possibleProduct.getImages().size() >= 5) {
            logger.warn("Cannot post product image, limit reached for this product.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot upload product image, limit reached for this product.");
        }

        Image newImage = new Image();
        String imageType;

        String fileContentType = file.getContentType();
        if (fileContentType != null && fileContentType.contains("/")) {
            imageType = fileContentType.split("/")[1];
        } else {
            logger.debug("Error with image type is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error with image type is null");
        }

        if (!Arrays.asList("png", "jpeg", "jpg", "gif").contains(imageType)) {
            logger.warn("Cannot post product image, invalid image type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Image type");
        }

        newImage = imageService.createImageFileName(newImage, imageType);

        imageService.storeImage(newImage.getFileName(), file);


        BufferedImage thumbnail = imageService.resizeImage(newImage);
        if (thumbnail == null) {
            logger.debug("Error resizing image");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resizing file");
        }

        imageService.storeThumbnailImage(newImage.getThumbnailFilename(), imageType, thumbnail);
        newImage = imageService.createImage(newImage);
        logger.debug("Created new image entity with filename {}", newImage.getFileName());


        Product product = productService.findProductById(productId);
        logger.info("Retrieved product with ID: {}", product.getId());


        productService.addImageToProduct(product, newImage);


        if (product.getPrimaryImage() == null) {
            logger.info("No primary image found for product with ID: {} in th database", product.getId());
            product.setPrimaryImage(newImage);
            logger.info("Set image with ID: {} to product with ID: {} in the database", newImage.getId(), product.getId());
        }

        productService.updateProduct(product);
        logger.info("Saved image with ID: {} to product with ID: {} in the database", newImage.getId(), product.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(newImage);

    }

    /**
     * Delete image from product
     * Removes the referenced image and image thumbnail from the database, from the product, changes the primary image if removed image is
     * the primary image of the product and deletes the image file from the server
     * Returns:
     * NOT_ACCEPTABLE 406 If image doesnt exist
     * FORBIDDEN 403 If user not allowed to make request (not global admin or business admin)
     * 400 BAD_REQUEST If invalid image type, product doesnt exist, product doesnt belong to business, or no image
     * 200 Ok if successfully deleted
     * 500 If server error deleting image
     *
     * @param businessId id of business that own the product
     * @param productId id of the product that is having an image deleted from
     * @param imageId id of the image that is being deleted
     * @return Status code dependent on success. 406, 403, 400, 500 errors. 200 OK if deletion was successful.
     */
    @DeleteMapping("/businesses/{businessId}/products/{productId}/images/{imageId}")
    public ResponseEntity<Object> deleteProductImage(@PathVariable("businessId") Integer businessId, @PathVariable("productId") String productId, @PathVariable("imageId") Integer imageId) {

        User user = userService.getCurrentlyLoggedInUser();

        logger.debug("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (!possibleBusiness.checkUserIsAdministrator(user) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot delete productImage. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not an admin of the application or this business");
        }


        Product product = productService.findProductById(productId);

        if (product==null){
            logger.warn("Cannot delete productImage. Product is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product no longer exists");
        }
        if (!product.getBusinessId().equals(businessId)) {
            logger.warn("Cannot post product image for product that does not belong to current business");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id does not exist for Current Business");
        }

        Image image = imageService.findImageById(imageId);

        if (image==null){
            logger.warn("Cannot delete productImage. image: {}", image);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Image no longer exists");
        }

        if (product.getImages().stream().noneMatch(possibleImage -> possibleImage.getId().equals(image.getId()))) {
            logger.warn("Cannot post product image for product image that does not belong to current product");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product image id does not exist for Current Product");
        }

        productService.deleteImageRecordFromProductInDB (product, image);
        productService.updatePrimaryImage(product, image);
        productService.updateProduct(product);
        imageService.deleteImageRecordFromDB (image);
        imageService.deleteImageFile(image);
        return ResponseEntity.status(HttpStatus.OK).body("Image deleted successfully");
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
    @GetMapping(value = "/images", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@RequestParam String filename) throws IOException {
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

    /**
     * Sets primary image of a product to the chosen imageId by changing the database reference
     *
     * @param businessId The Id of the business that has the product to change primary image of
     * @param productId The id of the product  to change primary image of
     * @param imagedId id of the image that is being set as primary image
     * @return  Status code dependent on success. 406, 403, 400, 500 errors. 200 returned if successfully set as new primary Id
     */
    @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
    public ResponseEntity<Object> makeProductPrimaryImage(@PathVariable("businessId") Integer businessId, @PathVariable("productId") String productId, @PathVariable("imageId") Integer imagedId) {

        User user = userService.getCurrentlyLoggedInUser();

        logger.info("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Cannot post product image. Business ID: {} does not exist.", businessId);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business does not exist");
        }
        logger.info("Successfully retrieved business with ID: {}.", businessId);

        if (!possibleBusiness.checkUserIsAdministrator(user) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot post product image. User: {} is not global admin or business admin: {}", user.getId(), businessId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user.getId(), businessId);

        logger.info("Check if product with id ` {} ` exists on for business with id ` {} ` ", productId, businessId);
        Product possibleProduct = productService.findProductById(productId);

        if (possibleProduct == null) {
            logger.warn("Cannot post product image for product that does not exist");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with given id does not exist");
        }
        if (!possibleProduct.getBusinessId().equals(businessId)) {
            logger.warn("Cannot post product image for product that does not belong to current business");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id does not exist for Current Business");
        }

        Image possibleImage = imageService.findImageById(imagedId);

        if (possibleImage == null) {
            logger.warn("Cannot post product image for product image id that does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Product image with given id does not exist");
        }

        if (possibleProduct.getImages().stream().noneMatch(image -> image.getId().equals(imagedId))) {
            logger.warn("Cannot post product image for product image that does not belong to current product");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product image id does not exist for Current Product");
        }

        possibleProduct.setPrimaryImage(possibleImage);
        productService.updateProduct(possibleProduct);

        return ResponseEntity.status(HttpStatus.OK).body("Primary image successfully updated");
    }


    @PostMapping("/users/{userId}/image")
    public ResponseEntity<Object> postUserImage(@PathVariable("userId") Integer userId, @RequestParam("filename") MultipartFile file) {
        logger.info("Request to upload user image for user: {}", userId);

        User loggedInUser = userService.getCurrentlyLoggedInUser();

        //If we are not uploading the image to ourselves, and we are not a global admin, return error
        if (!loggedInUser.getId().equals(userId) && !loggedInUser.checkUserGlobalAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allow to make change for this user");
        }

        User userForImage;

        //If the request is made for not the currently logged in user, get their information
        if (loggedInUser.getId().equals(userId)) {
            userForImage = loggedInUser;
        } else {
            userForImage = userService.findUserById(userId);
        }

        if (file.isEmpty()) {
            logger.warn("Cannot post user image, no image received");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Image Received");
        }

        Image newImage = new Image();
        String imageType;

        String fileContentType = file.getContentType();
        if (fileContentType != null && fileContentType.contains("/")) {
            imageType = fileContentType.split("/")[1];
        } else {
            logger.debug("Error with image type is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error with image type is null");
        }

        if (!Arrays.asList("png", "jpeg", "jpg", "gif").contains(imageType)) {
            logger.warn("Cannot post product image, invalid image type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Image type");
        }

        newImage = imageService.createImageFileName(newImage, imageType);

        imageService.storeImage(newImage.getFileName(), file);

        BufferedImage thumbnail = imageService.resizeImage(newImage);
        if (thumbnail == null) {
            logger.debug("Error resizing image");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resizing file");
        }

        imageService.storeThumbnailImage(newImage.getThumbnailFilename(), imageType, thumbnail);
        newImage = imageService.createImage(newImage);
        logger.debug("Created new image entity with filename {}", newImage.getFileName());

        userService.addImageToUser(userForImage, newImage);

        userService.saveUserChanges(userForImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(newImage);

    }
}
