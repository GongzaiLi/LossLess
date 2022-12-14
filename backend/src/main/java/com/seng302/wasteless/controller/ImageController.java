package com.seng302.wasteless.controller;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Image;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ImageService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.UserService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

        if (possibleProduct.getImages().size() >= 5) {
            logger.warn("Cannot post product image, limit reached for this product.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot upload product image, limit reached for this product.");
        }

        Image newImage = imageService.saveImageWithThumbnail(file);

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


    /**
     * Handle request for uploading images for users
     * Allows for GAA/DGAA to upload an image for a user
     *
     * 401                      If not currently authenticated
     * 403 Forbidden            If attempting to make a request to change another users image and not DGAA or GAA
     * 400 Bad Request          No file content, Bad file type
     * 406 Not Acceptable       UserId not found
     *
     * @param userId    The id of the user to upload the image for (possibly different from currently logged in user if DGAA)
     * @param file      The image to upload
     * @return          The image after uploading, or one of the error codes detailed above.
     */
    @PostMapping("/users/{userId}/image")
    public ResponseEntity<Object> postUserImage(@PathVariable("userId") Integer userId, @RequestParam("filename") MultipartFile file) {
        logger.info("Request to upload user image for user: {}", userId);

        User userForImage = userService.getUserToModify(userId);

        //Delete old user image if they had one
        if (userForImage.getProfileImage() != null) {
            userService.deleteUserImage(userForImage);
        }

        Image newImage = imageService.saveImageWithThumbnail(file);

        userService.addImageToUser(userForImage, newImage);

        userService.saveUserChanges(userForImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(newImage);

    }

    /**
     * Handles requests to delete a user's image.
     * @param userId Id of user to delete image for
     * @return 200 OK response if deleted successfully
     * @throws ResponseStatusException 403 FORBIDDEN exception if the user is not allowed to modify the user with given id,
     * 406 NOT ACCEPTABLE if given user doesn't exist
     */
    @DeleteMapping("/users/{userId}/image")
    public ResponseEntity<Object> deleteUserImage(@PathVariable("userId") Integer userId) {
        User userForImage = userService.getUserToModify(userId);

        if (userForImage.getProfileImage() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given user does not have a profile image");
        }

        userService.deleteUserImage(userForImage);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Handle request for uploading images for businesses. If an image already exists
     * the image will be removed an a new one will be saved to the media folder and to
     * the database for the given business.
     * Allows for GAA/DGAA to upload an image for a business
     *
     * 401                      If not currently authenticated
     * 403 Forbidden            If attempting to make a request to change another businesses image and not DGAA, GAA or business admin
     * 400 Bad Request          No file content, Bad file type
     * 406 Not Acceptable       businessId not found
     *
     * @param businessId    The id of the business to upload the image for
     * @param file          The image to upload
     * @return              The image after uploading, or one of the error codes detailed above.
     */
    @PostMapping("/businesses/{businessId}/image")
    public ResponseEntity<Object> postBusinessImage(@PathVariable("businessId") Integer businessId, @RequestParam("filename") MultipartFile file) {
        logger.info("Request to upload business image for business: {}", businessId);

        User currentUser = userService.getCurrentlyLoggedInUser();
        Business businessForImage = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(businessForImage, currentUser);

        //Delete old business image if they had one
        if (businessForImage.getProfileImage() != null) {
            businessService.deleteBusinessImage(businessForImage);
        }

        Image newImage = imageService.saveImageWithThumbnail(file);

        businessService.addImageToBusiness(businessForImage, newImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(newImage);

    }

    /**
     * Handles requests to delete a business's image.
     * @param businessId Id of business to delete image for
     * @return 200 OK response if deleted successfully
     * @throws ResponseStatusException 403 FORBIDDEN exception if the user is not allowed to modify the business with given id,
     * 406 NOT ACCEPTABLE if given business doesn't exist
     */
    @DeleteMapping("/businesses/{businessId}/image")
    public ResponseEntity<Object> deleteBusinessImage(@PathVariable("businessId") Integer businessId) {
        User currentUser = userService.getCurrentlyLoggedInUser();
        Business businessForImage = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(businessForImage, currentUser);

        if (businessForImage.getProfileImage() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given business does not have a profile image");
        }

        businessService.deleteBusinessImage(businessForImage);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

