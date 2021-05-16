package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.dto.mapper.PostInventoryDtoMapper;
import com.seng302.wasteless.dto.mapper.PostListingsDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * ListingsController is used for mapping all Restful API requests starting with the address "/businesses/{id}/listings".
 */
@RestController
public class ListingsController {
    private static final Logger logger = LogManager.getLogger(InventoryController.class.getName());


    private final BusinessService businessService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final ListingsService listingsService;


    @Autowired
    public ListingsController(BusinessService businessService, UserService userService, InventoryService inventoryService, ListingsService listingsService) {
        this.businessService = businessService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.listingsService = listingsService;


    }

    /**
     * Handle POST request to /businesses/{id}/listings endpoint for creating new listing for business
     *
     * Checks business with id from path exists
     * Checks user making request exists, and has privileges (admin of business, or (D)GAA)
     * Checks inventory with id exists
     * Other validation of listing handled by the listings entity class
     *
     *
     * @param businessId    The id of the business to create inventory item for
     * @param listingsDtoRequest     Dto containing information needed to create an listing
     * @return Error code detailing error or 201 create with listingId
     */
    @PostMapping("/businesses/{id}/listings")
    public ResponseEntity<Object> postBusinessListings(@PathVariable("id") Integer businessId, @Valid @RequestBody PostListingsDto listingsDtoRequest) {
        logger.info("Post request to create business LISTING, business id: {}, PostListingsDto {}", businessId, listingsDtoRequest);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();

        logger.debug("Validating user with Email: {}", currentPrincipalEmail);
        User user = userService.findUserByEmail(currentPrincipalEmail);

        if (user == null) {
            logger.warn("Cannot create LISTING. Access token invalid for user with Email: {}", currentPrincipalEmail);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Access token is invalid");
        }
        logger.info("Validated token for user: {} with Email: {}.", user, currentPrincipalEmail);

        logger.debug("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        if (possibleBusiness == null) {
            logger.warn("Cannot create LISTING. Business ID: {} does not exist.", businessId);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Business does not exist");
        }
        logger.info("Successfully retrieved business: {} with ID: {}.", possibleBusiness, businessId);

        if (!possibleBusiness.checkUserIsAdministrator(user) && !user.checkUserGlobalAdmin()) {
            logger.warn("Cannot create LISTING. User: {} is not global admin or business admin: {}", user, possibleBusiness);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not an admin of the application or this business");
        }
        logger.info("User: {} validated as global admin or admin of business: {}.", user, possibleBusiness);


        logger.info("Retrieving inventory with id ` {} ` from business with id ` {} ` ", listingsDtoRequest.getInventoryItemId(), businessId);
        Inventory possibleInventoryItem = inventoryService.findInventoryById(listingsDtoRequest.getInventoryItemId());

        if (possibleInventoryItem == null) {
            logger.warn("Cannot create LISTING for inventory that does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Inventory with given id does not exist");
        }

        Listings listing = PostListingsDtoMapper.postListingsDto(listingsDtoRequest);

        listing.setBusinessId(businessId);

        listing = listingsService.createListing(listing);

        logger.info("Created new Listing {}", listing);

        JSONObject responseBody = new JSONObject();
        responseBody.put("listingId", listing.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
}
