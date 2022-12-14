package com.seng302.wasteless.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetProductDTO;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.GetProductSortTypes;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.ProductService;
import com.seng302.wasteless.service.PurchasedListingService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.ProductViews;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ProductController is used for mapping all Restful API requests starting with the address
 * "/businesses/{id}/products" and /businesses/{businessId}/products/{productId}.
 */
@RestController
public class CatalogueController {

    private static final Logger logger = LogManager.getLogger(CatalogueController.class.getName());

    private final BusinessService businessService;
    private final UserService userService;
    private final ProductService productService;
    private final PurchasedListingService purchasedListingService;

    @Autowired
    public CatalogueController(BusinessService businessService, UserService userService, ProductService productService, PurchasedListingService purchasedListingService) {
        this.businessService = businessService;
        this.userService = userService;
        this.productService = productService;
        this.purchasedListingService = purchasedListingService;

    }
    /**
     * Handle post request to /businesses/{id}/products endpoint for creating products
     *
     * @param businessId      the id of the business that is creating the product
     * @param possibleProduct the product that is trying to be added to the catalogue
     * @param generateSalesData optional boolean, if true will create and save fake purchase history (used by data gen)
     * @return Http Response:  200 if created, 400 for a bad request, 401 if unauthorised or 403 if forbidden
     */
    @PostMapping("/businesses/{id}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBusinessProduct(@PathVariable("id") Integer businessId, @Valid @RequestBody @JsonView(ProductViews.PostProductRequestView.class) Product possibleProduct, @RequestParam Optional<Boolean> generateSalesData) {

        logger.debug("Request to Create product: {} for business ID: {}", possibleProduct, businessId);

        User user = userService.getCurrentlyLoggedInUser();

        if (!possibleProduct.getId().matches("^[a-zA-Z0-9-_]*$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your product ID must be alphanumeric with dashes or underscores allowed.");
        }

        Business possibleBusiness = businessService.findBusinessById(businessId);

        logger.info("Successfully retrieved business with ID: {}.", businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness,user);

        logger.debug("Generating product ID");
        String productId = possibleProduct.createCode(businessId);

        productService.checkIfProductIdNotInUse(productId);

        if (possibleProduct.getRecommendedRetailPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product RRP can't be negative.");
        }
        LocalDate dateCreated = LocalDate.now();

        logger.debug("Setting product data.");
        possibleProduct.setId(productId);
        possibleProduct.setBusinessId(businessId);
        possibleProduct.setCreated(dateCreated);

        //Save product
        logger.debug("Trying to create Product Entity");
        possibleProduct = productService.createProduct(possibleProduct);

        JSONObject responseBody = new JSONObject();
        responseBody.put("productId", possibleProduct.getId());

        logger.info("Successfully created Product Entity");

        if (generateSalesData.isPresent() && Boolean.TRUE.equals(generateSalesData.get())) {
            purchasedListingService.generatePurchasesForProduct(possibleProduct, user, possibleBusiness);
            businessService.saveBusinessChanges(possibleBusiness);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }


    /**
     * Handle get request to /businesses/{id}/products endpoint for retrieving all products in a business's catalogue
     *
     * Takes a count and offset as params to return only count results starting at offset offset.
     * Sorts by sortBy and sortDirection
     *
     * @param businessId The id of the business to get
     * @param offset value of the offset from the start of the results query. Used for pagination
     * @param count number of results to be returned
     * @param sortBy the column to sort by
     * @param sortDirection the direction to sort
     * @param searchQuery The query to search a product by
     * @return Http Status 200 and list of products if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @GetMapping("/businesses/{id}/products")
    public ResponseEntity<Object> getBusinessesProducts(@PathVariable("id") Integer businessId,
                                                        @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                        @RequestParam(required = false, defaultValue = "10") Integer count,
                                                        @RequestParam(value = "sortBy", required = false, defaultValue = "ID") String sortBy,
                                                        @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
                                                        String searchQuery
    ) {

        if (searchQuery == null) searchQuery = "";
        logger.debug("Request to search product with query: {}", searchQuery);

        logger.debug("Request to get business products");
        logger.info("Request with params count:{} offset:{} sortBy:{} sortDirection: {}", count, offset, sortBy, sortDirection);
        GetProductSortTypes sortType;

        if (count < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Count must be >= 1 if provided");
        } else if (offset < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Offset must be >= 0  if provided.");
        }

        try {
            sortType = GetProductSortTypes.valueOf(sortBy);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid value for sortBy. Value was {}", sortBy);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid value for sortBy. Acceptable values are: ID, NAME, DESCRIPTION, MANUFACTURER, RRP, CREATED");
        }

        if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")) {
            logger.warn("Invalid value for sortDirection. Value was {}", sortDirection);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid value for sortDirection. Acceptable values are: ASC, DESC");
        }

        User user = userService.getCurrentlyLoggedInUser();

        logger.debug("Request to get business with ID: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);
        List<Product> productList = productService.searchCountProductsByBusinessIdFromOffset(businessId, offset, count, sortType, sortDirection, searchQuery);

        Integer totalItems = productService.getTotalProductsCountByBusinessId(businessId, searchQuery);

        logger.debug("Successfully retrieved {} products for business with ID: {}", totalItems, businessId);

        GetProductDTO getProductDTO = new GetProductDTO()
                .setProducts(productList)
                .setTotalItems(totalItems);

        return ResponseEntity.status(HttpStatus.OK).body(getProductDTO);
    }




    /**
     * Handle put request to /businesses/{businessId}/products/{productId} endpoint for updating a product
     * in the product catalogue
     *
     * @param businessId    The business containing the product
     * @param productId     The product that needs to be updated
     * @param editedProduct A product object that holds the edited values
     * @return  Http Status 200 and 403 if user not admin, 400 if product doesn't exist and if new product ID already exists.
     */
    @PutMapping("/businesses/{businessId}/products/{productId}")
    public ResponseEntity<Object> editBusinessProduct(@PathVariable("businessId") Integer businessId, @PathVariable("productId") String productId, @Valid @RequestBody Product editedProduct) {
        logger.debug("Request to update product with data: {} for business ID: {}", editedProduct, businessId);

        User user = userService.getCurrentlyLoggedInUser();

        Business possibleBusiness = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);

        logger.debug("Trying to find product with ID: {} in the catalogue", productId);
        Product oldProduct = productService.findProductById(productId);

        if (!oldProduct.getId().matches("^[a-zA-Z0-9-_]*$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your product ID must be alphanumeric with dashes or underscores allowed.");
        }
        logger.debug("Generating new product ID");
        String newProductId = editedProduct.createCode(businessId);
        if (!oldProduct.getId().equals(newProductId)) {
            productService.checkIfProductIdNotInUse(newProductId);
        }
        logger.info("Product ID: {} generated for old product: {}", newProductId, productId);

        if (editedProduct.getRecommendedRetailPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product RRP can't be negative.");
        }

        logger.debug("Trying to update product: {} for business: {} with new data: {}", oldProduct, businessId, editedProduct);

        oldProduct.setId(newProductId);
        oldProduct.setName(editedProduct.getName());
        oldProduct.setDescription(editedProduct.getDescription());
        oldProduct.setManufacturer(editedProduct.getManufacturer());
        oldProduct.setRecommendedRetailPrice(editedProduct.getRecommendedRetailPrice());

        productService.updateProduct(oldProduct);

        logger.info("Successfully updated old product with data: {}", oldProduct);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
