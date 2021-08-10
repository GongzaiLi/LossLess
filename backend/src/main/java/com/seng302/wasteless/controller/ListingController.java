package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetListingDto;
import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.dto.mapper.PostListingsDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.InventoryService;
import com.seng302.wasteless.service.ListingsService;
import com.seng302.wasteless.service.UserService;
import com.seng302.wasteless.view.ListingViews;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ListingsController is used for mapping all Restful API requests starting with the address "/businesses/{id}/listings".
 */
@RestController
public class ListingController {
    private static final Logger logger = LogManager.getLogger(ListingController.class.getName());


    private final BusinessService businessService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final ListingsService listingsService;


    @Autowired
    public ListingController(BusinessService businessService, UserService userService, InventoryService inventoryService, ListingsService listingsService) {
        this.businessService = businessService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.listingsService = listingsService;


    }

    /**
     * Handle POST request to /businesses/{id}/listings endpoint for creating new listing for business
     * <p>
     * Checks business with id from path exists
     * Checks user making request exists, and has privileges (admin of business, or (D)GAA)
     * Checks inventory with id exists
     * Other validation of listing handled by the listings entity class
     *
     * @param businessId         The id of the business to create inventory item for
     * @param listingsDtoRequest Dto containing information needed to create an listing
     * @return Error code detailing error or 201 create with listingId
     */
    @PostMapping("/businesses/{id}/listings")
    public ResponseEntity<Object> postBusinessListings(@PathVariable("id") Integer businessId, @Valid @RequestBody PostListingsDto listingsDtoRequest) {
        logger.info("Post request to create business LISTING, business id: {}, PostListingsDto {}", businessId, listingsDtoRequest);

        User user = userService.getCurrentlyLoggedInUser();

        logger.info("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);


        logger.info("Retrieving inventory with id `{}` from business with id `{}` ", listingsDtoRequest, possibleBusiness);
        logger.info("Retrievrf `{}` ", listingsDtoRequest.getInventoryItemId());
        Inventory possibleInventoryItem = inventoryService.findInventoryById(listingsDtoRequest.getInventoryItemId());


        if (possibleInventoryItem.getExpires().isBefore(LocalDate.now())) {
            logger.warn("Cannot create LISTING. Inventory item expiry: {} is in the past.", possibleInventoryItem.getExpires());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory item expiry is in the past.");
        }

        Integer availableQuantity = possibleInventoryItem.getQuantity();
        Integer listingQuantity = listingsDtoRequest.getQuantity();

        if (availableQuantity < listingQuantity) {
            logger.warn("Cannot create LISTING. Listing quantity: {} greater than available inventory quantity: {}.",listingQuantity, availableQuantity);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing quantity greater than available inventory quantity.");
        }

        Integer remainingQuantity = availableQuantity - listingQuantity;


        Listing listing = PostListingsDtoMapper.postListingsDto(listingsDtoRequest);

        listing.setBusiness(possibleBusiness);
        listing.setCreated(LocalDate.now());

        listing = listingsService.createListing(listing);

        Integer updateQuantityResult = inventoryService.updateInventoryItemQuantity(remainingQuantity, possibleInventoryItem.getId());

        if (updateQuantityResult == 0) {
            logger.error("No inventory item quantity value was updated when this listing was created.");
        } else if (updateQuantityResult > 1) {
            logger.error("More than one inventory item quantity value was updated when this listing was created.");
        } else if (updateQuantityResult == 1) {
            logger.info("Inventory item quantity value was updated when this listing was created.");
        }


        logger.info("Created new Listing {}", listing);

        JSONObject responseBody = new JSONObject();
        responseBody.put("listingId", listing.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    /**
     * Handle get request to /businesses/{id}/listings endpoint for retrieving all listings for a business
     *
     * @param businessId The id of the business to get
     * @param pageable pagination and sorting params
     * @return Http Status 200 and list of listings if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @GetMapping("/businesses/{id}/listings")
    @JsonView(ListingViews.GetListingView.class)
    public ResponseEntity<Object> getListingsOfBusiness(@PathVariable("id") Integer businessId, Pageable pageable) {
        logger.info("Get request to GET business LISTING, business id: {}", businessId);

        logger.debug("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);

        List<Listing> listings = listingsService.findBusinessListingsWithPageable(businessId, pageable);

        Long totalItems = listingsService.getCountOfAllListingsOfBusiness(businessId);

        GetListingDto getListingDto = new GetListingDto()
                .setListings(listings)
                .setTotalItems(totalItems);

        logger.info("{}", getListingDto);
        return ResponseEntity.status(HttpStatus.OK).body(getListingDto);
    }

    /**
     * Handles endpoint to search for listings
     *
     * @param searchQuery       The query string to search listings with. Should be set to value of query param 'searchQuery' via Spring magic.
     * @param priceLower  Lower inclusive bound for listing prices
     * @param priceUpper  Upper inclusive bound for listing prices
     * @param address           Address to match against suburb, city, and country of lister of listing
     * @param pageable          pagination and sorting params
     * @return Http Status 200 if valid query, 401 if unauthorised
     */
    @GetMapping("/listings/search")
    @JsonView(ListingViews.GetListingView.class)
    public ResponseEntity<Object> getListingsOfBusiness(
            @RequestParam Optional<String> searchQuery,
            @RequestParam Optional<Double> priceLower,
            @RequestParam Optional<Double> priceUpper,
            @RequestParam Optional<String> address,
            Pageable pageable) {
        logger.info("Get request to search LISTING, query param: {}, price lower: {}, price upper: {}, address: {}", searchQuery, priceLower, priceUpper, address);

        Page<Listing> listings = listingsService.searchListings(searchQuery, priceLower, priceUpper, address, pageable);

        GetListingDto getListingDto = new GetListingDto()
                .setListings(listings.getContent())
                .setTotalItems(listings.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(getListingDto);
    }

    /**
     * Returns a json object of bad field found in the request
     *
     * @param exception The exception thrown by Spring when it detects invalid data
     * @return Map of field name that had the error and a message describing the error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
//            logger.error(errorMessage); doesnt work. I am not sure why.
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Returns a json object of bad field found in the request
     *
     * @param exception The exception thrown by Spring when it detects invalid data
     * @return Map of field name that had the error and a message describing the error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleValidationExceptions(
            ConstraintViolationException exception) {

        Map<String, String> errors = new HashMap<>();

        String constraintName = exception.getConstraintViolations().toString();
        String errorMsg = exception.getMessage();

        errors.put(constraintName, errorMsg);
        return errors;
    }

}