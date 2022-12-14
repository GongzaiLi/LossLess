package com.seng302.wasteless.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.dto.GetListingDto;
import com.seng302.wasteless.dto.GetListingsDto;
import com.seng302.wasteless.dto.GetPurchasedListingDto;
import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.dto.mapper.PostListingsDtoMapper;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.*;
import com.seng302.wasteless.view.PurchasedListingView;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
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
    private final NotificationService notificationService;
    private final PurchasedListingService purchasedListingService;


    @Autowired
    public ListingController(BusinessService businessService,
                             UserService userService,
                             InventoryService inventoryService,
                             ListingsService listingsService,
                             PurchasedListingService purchasedListingService,
                             NotificationService notificationService) {
        this.businessService = businessService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.listingsService = listingsService;
        this.notificationService = notificationService;
        this.purchasedListingService = purchasedListingService;
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
        logger.info("Post request to create business LISTING, business id: {}", businessId);
        User user = userService.getCurrentlyLoggedInUser();

        logger.info("Retrieving business with id: {}", businessId);
        Business possibleBusiness = businessService.findBusinessById(businessId);

        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);

        Inventory possibleInventoryItem = inventoryService.findInventoryById(listingsDtoRequest.getInventoryItemId());

        if (possibleInventoryItem.getExpires().isBefore(LocalDate.now())) {
            logger.warn("Cannot create LISTING. Inventory item expiry: {} is in the past.", possibleInventoryItem.getExpires());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory item expiry is in the past.");
        }

        if (possibleInventoryItem.getQuantityUnlisted() == null) {
            possibleInventoryItem.setQuantityUnlisted(possibleInventoryItem.getQuantity());
        }

        Integer availableQuantity = possibleInventoryItem.getQuantityUnlisted();
        Integer listingQuantity = listingsDtoRequest.getQuantity();

        if (availableQuantity < listingQuantity) {
            logger.warn("Cannot create LISTING. Listing quantity: {} greater than available inventory quantity: {}.", listingQuantity, availableQuantity);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing quantity greater than available inventory quantity.");
        }

        Integer quantityRemaining = possibleInventoryItem.getQuantityUnlisted() - listingQuantity;

        Listing listing = PostListingsDtoMapper.postListingsDto(listingsDtoRequest);

        listing.setBusiness(possibleBusiness);
        listing.setCreated(LocalDate.now());
        listing.setUsersLiked(0);

        listing = listingsService.createListing(listing);

        inventoryService.updateInventoryItemQuantity(quantityRemaining, possibleInventoryItem.getId());

        logger.info("Created new Listing with Id {}", listing.getId());

        JSONObject responseBody = new JSONObject();
        responseBody.put("listingId", listing.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    /**
     * Handle get request to /businesses/{id}/listings endpoint for retrieving all listings for a business
     *
     * @param businessId The id of the business to get
     * @param pageable   pagination and sorting params
     * @return Http Status 200 and list of listings if valid, 401 is unauthorised, 403 if forbidden, 406 if invalid id
     */
    @GetMapping("/businesses/{id}/listings")
    public ResponseEntity<Object> getListingsOfBusiness(@PathVariable("id") Integer businessId, Pageable pageable) {
        logger.info("Get request to GET business LISTING, business id: {}", businessId);

        businessService.findBusinessById(businessId);

        List<Listing> listings = listingsService.findBusinessListingsWithPageable(businessId, pageable);

        Long totalItems = listingsService.getCountOfAllListingsOfBusiness(businessId);
        User user = userService.getCurrentlyLoggedInUser();
        GetListingsDto getListingsDto = new GetListingsDto(listings, totalItems, user);

        return ResponseEntity.status(HttpStatus.OK).body(getListingsDto);
    }


    /**
     * Handle get request to /listings/{id} endpoint for retrieving the listing with given id
     *
     * @param listingId The id of the listing to get
     * @return Http Status 200 and the listing if valid, 401 if user is not logged in and 406 if invalid listing id
     */
    @GetMapping("/listings/{id}")
    public ResponseEntity<Object> getListingWithId(@PathVariable("id") Integer listingId) {
        logger.info("Get request to GET a LISTING with id: {}", listingId);
        User user = userService.getCurrentlyLoggedInUser();
        Listing listing = listingsService.findFirstById(listingId);

        GetListingDto dtoListing = new GetListingDto(listing, user.checkUserLikesListing(listing));

        logger.info("Retrieved listing with ID: {}", dtoListing);

        return ResponseEntity.status(HttpStatus.OK).body(dtoListing);
    }


    /**
     * Handles endpoint to search for listings
     *
     * @param searchQuery      The query string to search listings with. Should be set to value of query param 'searchQuery' via Spring magic.
     * @param priceLower       Lower inclusive bound for listing prices
     * @param priceUpper       Upper inclusive bound for listing prices
     * @param businessName     Business name to match against listings
     * @param businessTypes    List of business types to match against listings
     * @param closingDateStart A date time string to filter listings with. This sets the start range to filter listings by closing date. String is parse through a formatter to get a local date time object.
     * @param closingDateEnd   A date time string to filter listings with. This sets the end range to filter listings by closing date. String is parse through a formatter to get a local date time object.
     * @param address          Address to match against suburb, city, and country of lister of listing
     * @param pageable         pagination and sorting params
     * @return Http Status 200 if valid query, 400 if bad request i.e closing dates not of type date, 401 if unauthorised
     */
    @GetMapping("/listings/search")
    public ResponseEntity<Object> getListingsOfBusiness(
            @RequestParam Optional<String> searchQuery,
            @RequestParam Optional<Double> priceLower,
            @RequestParam Optional<Double> priceUpper,
            @RequestParam Optional<String> businessName,
            @RequestParam Optional<List<String>> businessTypes,
            @RequestParam Optional<String> closingDateStart,
            @RequestParam Optional<String> closingDateEnd,
            @RequestParam Optional<String> address,
            Pageable pageable) {

        Optional<LocalDateTime> closingDateTimeStart = Optional.empty();
        Optional<LocalDateTime> closingDateTimeEnd =  Optional.empty();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(closingDateStart.isPresent() && !closingDateStart.get().equals("")){
            try {
                closingDateTimeStart = Optional.of(LocalDateTime.parse(closingDateStart.get(), formatter));
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Closing Date Start not of type Date.");
            }
        }
        if(closingDateEnd.isPresent()&& !closingDateEnd.get().equals("")) {
            try {
                closingDateTimeEnd =  Optional.of(LocalDateTime.parse(closingDateEnd.get(), formatter));
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Closing Date End not of type Date.");
            }

        }
        logger.info("Get request to search LISTING, query param: {}, price lower: {}, price upper: {}, business name: {}, business types: {}, closingDateStart: {} closingDateEnd: {}, address: {}",
                searchQuery, priceLower, priceUpper, businessName, businessTypes, closingDateTimeStart, closingDateTimeEnd, address);


        Page<Listing> listings = listingsService.searchListings(searchQuery, priceLower, priceUpper, businessName, businessTypes, address, closingDateTimeStart, closingDateTimeEnd, pageable);
        User user = userService.getCurrentlyLoggedInUser();
        GetListingsDto getListingsDto = new GetListingsDto(listings.getContent(), listings.getTotalElements(), user);
        logger.info(getListingsDto);
        return ResponseEntity.status(HttpStatus.OK).body(getListingsDto);
    }


    /**
     * Handle PUT request to /listings/{listingId}/like endpoint for adding or removing like(s) on a listing
     * <p>
     * Checks user is logged in
     * Checks listing with id exists
     * Checks if user already has liked listing:
     * if unliked then listing becomes liked
     * if liked listing becomes unliked
     *
     * @param listingId id of listing to be liked or unliked
     * @return Json object with boolean "liked" true = liked, false = unliked
     * Http Status 200 if valid query, 401 if unauthorised, 406 if invalid listing id
     */
    @PutMapping("/listings/{listingId}/like")
    public ResponseEntity<Object> addLikeToListing(@PathVariable Integer listingId) {
        User user = userService.getCurrentlyLoggedInUser();
        Listing listing = listingsService.findFirstById(listingId);
        logger.info("Retrieved listing with ID: {}", listingId);
        Boolean likeStatus = user.toggleListingLike(listing);
        listingsService.updateListing(listing);
        userService.saveUserChanges(user);
        Notification likedStatusNotification;
        if (Boolean.TRUE.equals(likeStatus)) {
            likedStatusNotification = NotificationService.createNotification(user.getId(), listing.getId(), NotificationType.LIKEDLISTING, String.format("You have liked listing: %s. This listing closes at %tF", listing.getInventoryItem().getProduct().getName(), listing.getCloses()));
        } else {
            likedStatusNotification = NotificationService.createNotification(user.getId(), listing.getId(), NotificationType.UNLIKEDLISTING, String.format("You have unliked listing: %s", listing.getInventoryItem().getProduct().getName()));

        }
        notificationService.saveNotification(likedStatusNotification);
        JSONObject responseBody = new JSONObject();
        responseBody.put("liked", likeStatus);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    /**
     * Handles requests to the endpoint to purchase a listing
     *
     * Notifies user they have purchased this listing
     * Notifies all other users who had liked this listing that it has been purchased by someone else
     *
     *
     * @param listingId The id of the listing being purchased
     * @return A 200 OK status if the listing is successfully purchased.
     * A 406 status if no listing exists with the given id
     */
    @PostMapping("/listings/{id}/purchase")
    public ResponseEntity<Object> purchaseListing(@PathVariable("id") Integer listingId) {
        logger.info("Request to purchase listing with ID: {}", listingId);

        var listing = listingsService.findFirstById(listingId);

        List<Integer> usersWhoLiked = userService.findUserIdsByLikedListing(listing);

        var purchasedListing = listingsService.purchase(listing, userService.getCurrentlyLoggedInUser());

        var purchaseNotification = NotificationService.createNotification(userService.getCurrentlyLoggedInUser().getId(),
                purchasedListing.getId(), NotificationType.PURCHASED_LISTING, String.format("You have purchased %s of the product %s",
                purchasedListing.getQuantity(), purchasedListing.getProduct().getName()));

        notificationService.saveNotification(purchaseNotification);

        usersWhoLiked.remove(userService.getCurrentlyLoggedInUser().getId());

        notificationService.notifyAllUsers(usersWhoLiked, purchasedListing.getId(), NotificationType.LIKEDLISTING_PURCHASED,  String.format("The listing you liked of the product %s has been purchased by someone else", purchasedListing.getProduct().getName()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Handles requests to the endpoint to get a purchased listing
     *
     * @param purchaseId    The id of the listing being purchased
     * @return              A 200 OK status if the listing is successfully purchased and a purchased entity
     *                      A 400 status if purchase Id not exist
     *                      A 403 status if login user is not a purchaser or UserAdminOfBusiness Or GAA
     */
    @GetMapping("/purchase/{id}")
    @JsonView(PurchasedListingView.GetPurchasedListingView.class)
    public ResponseEntity<Object> getPurchaseListing(@PathVariable("id") Integer purchaseId) {
        var purchasedListing = purchasedListingService.findPurchasedListingById(purchaseId);

        logger.info("Retrieved Purchase Listing with ID: {}", purchaseId);
        if (purchasedListing == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("purchase Id does not exist.");
        }

        var loginUser = userService.getCurrentlyLoggedInUser();

        if (!purchasedListing.getPurchaser().getId().equals(loginUser.getId()) && Boolean.FALSE.equals(businessService.checkUserAdminOfBusinessOrGAA(purchasedListing.getBusiness(), loginUser))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to make this request");
        }

        GetPurchasedListingDto getPurchasedListingDto = new GetPurchasedListingDto(purchasedListing);

        return ResponseEntity.status(HttpStatus.OK).body(getPurchasedListingDto);
    }
}