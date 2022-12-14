package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.SalesReportDto;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.SalesReportSinglePeriod;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.PurchasedListingService;
import com.seng302.wasteless.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;


/**
 * SalesReportController is used for mapping all Restful API requests relating to sales report data.
 */
@RestController
public class SalesReportController {
    private static final Logger logger = LogManager.getLogger(SalesReportController.class.getName());
    private static final DayOfWeek START_OF_WEEK = DayOfWeek.MONDAY;

    private final BusinessService businessService;
    private final UserService userService;
    private final PurchasedListingService purchasedListingService;


    @Autowired
    public SalesReportController(BusinessService businessService,
                                 UserService userService,
                                 PurchasedListingService purchasedListingService) {
        this.businessService = businessService;
        this.userService = userService;
        this.purchasedListingService = purchasedListingService;
    }

    /**
     * Gets the total number of purchases for a business
     *
     * Takes a date range in form of startDate and endDate. Either both or neither must be supplied
     *
     * @param businessId    The id of the business to get purchases for
     * @param startDate     The start date for the date range. Format yyyy-MM-dd
     * @param endDate       The end date for the date range. Format yyyy-MM-dd
     * @param period        The selected period for the data.
     * @return              The total purchases for a business
     */
    @GetMapping("/businesses/{id}/salesReport/totalPurchases")
    public ResponseEntity<Object> getPurchaseDataOfBusiness(@PathVariable("id") Integer businessId,
                                                            @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                            @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                                            @RequestParam(value = "period", required = false) String period) {
        User user = userService.getCurrentlyLoggedInUser();
        Business possibleBusiness = businessService.findBusinessById(businessId);
        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness,user);

        if (Boolean.FALSE.equals(validateDate(startDate, endDate))) {
            startDate = possibleBusiness.getCreated();
            endDate = LocalDate.now();
        }

        if (period == null) {
            List<SalesReportSinglePeriod> reportData = purchasedListingService.getSalesReportDataNoPeriod(businessId, startDate, endDate);
            return ResponseEntity.status(HttpStatus.OK).body(new SalesReportDto(reportData));
        }

        Period periodOfData;
        LocalDate firstPeriodStart = startDate;
        LocalDate lastPeriodEnd = endDate;

        switch (period) {
            case "day":
                periodOfData = Period.ofDays(1);
                break;
            case "week":
                periodOfData = Period.ofDays(7);
                firstPeriodStart = firstPeriodStart.with(START_OF_WEEK);
                lastPeriodEnd = lastPeriodEnd.with(START_OF_WEEK);
                break;
            case "month":
                periodOfData = Period.ofMonths(1);
                firstPeriodStart = startDate.withDayOfMonth(1);
                lastPeriodEnd = endDate.withDayOfMonth(endDate.lengthOfMonth());
                break;
            case "year":
                periodOfData = Period.ofYears(1);
                firstPeriodStart = startDate.withDayOfYear(1);
                lastPeriodEnd = endDate.withDayOfYear(endDate.lengthOfYear());
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have not specified a correct period.");
        }

        SalesReportDto responseBody = new SalesReportDto(
            purchasedListingService.getSalesReportDataWithPeriod(businessId, startDate, endDate, firstPeriodStart, lastPeriodEnd, periodOfData)
        );
        responseBody.setStartTruncated(!firstPeriodStart.equals(startDate));
        responseBody.setEndTruncated(!lastPeriodEnd.equals(endDate));

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    /**
     * Get the total quantity, value, likes of all sales, grouped by either product or manufacturer, of a business in the given period.
     *
     * Note that we don't use a Pageable here as we want to have our own custom sort attributes that use
     * difference validation from the default Pageable. For example, we throw a custom error message if the
     * sortBy parameter is not one of a few allowed values.
     *
     * @param businessId    The id of the business to get purchases for
     * @param reportType The type of report to get ("productsPurchasedTotals" for products or "manufacturerPurchasedTotals" for manufacturers).
     * @param startDate  The start date for the date range.
     * @param endDate    The end date for the date range.
     * @param sortBy        The value to sort the products by
     * @param order         The order to sort the products in
     * @param size The size of each page to return
     * @param page The number of the page to return (0 for first page, 1 for second page etc).
     * @return              The total quantity, value, likes of all purchases for each product of a business
     */
    @GetMapping("/businesses/{id}/salesReport/{reportType}")
    public ResponseEntity<Object> getGroupedPurchaseDataOfBusiness(@PathVariable("id") Integer businessId,
                                                                   @PathVariable("reportType") String reportType,
                                                                   @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                                   @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                                                   @RequestParam(value = "sortBy", required = false) String sortBy,
                                                                   @RequestParam(value = "order", required = false) Sort.Direction order,
                                                                   @RequestParam(required = false, defaultValue = "100000") Integer size,
                                                                   @RequestParam(required = false, defaultValue = "0") Integer page) {
        User user = userService.getCurrentlyLoggedInUser();
        Business possibleBusiness = businessService.findBusinessById(businessId);
        businessService.checkUserAdminOfBusinessOrGAA(possibleBusiness, user);

        if (Boolean.FALSE.equals(validateDate(startDate, endDate))) {
            startDate = possibleBusiness.getCreated();
            endDate = LocalDate.now();
        }

        Pageable sortedPageable = getSortedPageable(page, size, sortBy, order);

        switch (reportType) {
            case "productsPurchasedTotals":
                return ResponseEntity.status(HttpStatus.OK).body(
                        purchasedListingService.getProductsPurchasedTotals(businessId, startDate, endDate, sortedPageable));
            case "manufacturersPurchasedTotals":
                return ResponseEntity.status(HttpStatus.OK).body(
                        purchasedListingService.getManufacturersPurchasedTotals(businessId, startDate, endDate, sortedPageable));
            default:
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint that gets the number of sales listings, grouped by the duration between the
     * listings??? purchase and closing dates.
     *
     * @param businessId  The id of the business to get purchases for
     * @param startDate   The start date for the date range. Format yyyy-MM-dd
     * @param endDate     The end date for the date range. Format yyyy-MM-dd
     * @param granularity The granularity of the groupings of listings, i.e. the duration of a single group (in days).
     *                    Defaults to 1, and should not be 0 or less.
     * @return JSON object where the keys are the durations in days between the
     * listings??? purchase and closing dates, and the values are the number of sales listings
     * @throws ResponseStatusException If granularity is less than or equal to 0
     */
    @GetMapping("/businesses/{id}/salesReport/listingDurations")
    public ResponseEntity<Object> getListingsGroupedByDuration(@PathVariable("id") Integer businessId,
                                                               @RequestParam(value = "startDate") LocalDate startDate,
                                                               @RequestParam(value = "endDate") LocalDate endDate,
                                                               @RequestParam(defaultValue = "1") Integer granularity) {
        User user = userService.getCurrentlyLoggedInUser();
        Business business = businessService.findBusinessById(businessId);
        businessService.checkUserAdminOfBusinessOrGAA(business, user);

        return ResponseEntity.status(HttpStatus.OK).body(
                purchasedListingService.countSalesByDurationBetweenSaleAndClose(business.getId(), startDate, endDate, granularity)
        );
    }

    /**
     * Create and return a CSV file containing all sales report information (all purchased listings) for a given business
     *
     * Returns
     * 200 and CSV file if okay
     * 401 if unauthorised
     * 403 if forbidden from accessing business
     *
     * @param businessId    The id of the business to get sales report information for
     * @return              CSV file of all sales report information
     */
    @GetMapping("/businesses/{id}/salesReport/csv")
    public ResponseEntity<Object> getSalesReportCSV(@PathVariable("id") Integer businessId) {
        User user = userService.getCurrentlyLoggedInUser();
        Business business = businessService.findBusinessById(businessId);
        businessService.checkUserAdminOfBusinessOrGAA(business, user);

        InputStreamResource fileInputStream = new InputStreamResource(purchasedListingService.getSalesReportCSVByteSteam(business.getId()));

        String csvFileName = "salesReport.csv";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(
                fileInputStream,
                headers,
                HttpStatus.OK
        );
    }

    /**
     * Checks if given date ranges are valid for the request to be proceeded further.
     * Throws Bad Http Request Exception if date ranges are invalid.
     *
     * @param startDate The start date provided.
     * @param endDate The end date provided.
     */
    public boolean validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            logger.info("No date range specified. Getting report from business creation up to now.");
            return false;
        } else if (startDate == null || endDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must specify a start date and an end date, or neither.");
        } else if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date.");
        }
        return true;
    }

    /**
     * Creates a Pageable object given pagination information, and sorting information specific to sales reports.
     * Also carries out validation for the sorting information.
     * @param sortBy The key to sort by (one of "value", "quantity" or "likes")
     * @param order  The order to sort (asc or desc)
     * @param size   The size of each page to return
     * @param page   The number of the page to return (0 for first page, 1 for second page etc).
     * @return A Pageable object containing given pagination and sorting information
     * @throws ResponseStatusException 400 if order is not null but sortBy is, or if sortBy is not one of the valid values
     */
    public Pageable getSortedPageable(Integer page, Integer size, String sortBy, Sort.Direction order) {
        if (sortBy == null && order != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't have an order without specifying sort.");
        } else if (sortBy != null && !sortBy.equals("value") && !sortBy.equals("quantity") && !sortBy.equals("likes")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have not specified a correct value to sort by.");
        } else {
            if (order == null) {
                order = Sort.Direction.ASC;
            }
            Sort sort = (sortBy == null) ? Sort.unsorted() : Sort.by(order, sortBy);
            return PageRequest.of(page, size, sort);
        }
    }
}
