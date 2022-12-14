package com.seng302.wasteless.service;

import com.seng302.wasteless.dto.SalesReportManufacturerTotalsDto;
import com.seng302.wasteless.dto.SalesReportProductTotalsDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.repository.ProductRepository;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * PurchasedListing service applies product logic over the Product JPA repository.
 */
@Service
public class PurchasedListingService {

    private final PurchasedListingRepository purchasedListingRepository;
    private final ProductRepository productRepository;

    @Autowired
    public PurchasedListingService(PurchasedListingRepository purchasedListingRepository, ProductRepository productRepository) {
        this.purchasedListingRepository = purchasedListingRepository;
        this.productRepository = productRepository;
    }


    /**
     * Returns the PurchasedListing with the given ID.
     * @param id Id to find the listing of
     * @return The PurchasedListing with the given ID.
     */
    public PurchasedListing findPurchasedListingById(Integer id) {
        return purchasedListingRepository.findFirstById(id);
    }

    /**
     * Returns the total number of purchased listings for a business
     * in a specified date range
     * @param businessId Id of the business
     * @param startDate start date of range
     * @param endDate end date of range
     * @return The count of purchased listings
     */
    public Integer countPurchasedListingForBusinessInDateRange(Integer businessId, LocalDate startDate, LocalDate endDate) {
        return purchasedListingRepository.countAllByBusinessIdAndSaleDateBetween(businessId, startDate, endDate);}

    /**
     * Returns the total value of purchased listings for a business.
     * @param businessId    Id of the business
     * @param startDate     The start date for the date range. Format yyyy-MM-dd
     * @param endDate       The end date for the date range. Format yyyy-MM-dd
     * @return The count of purchased listings
     */
    public Double totalPurchasedListingValueForBusinessInDateRange(Integer businessId, LocalDate startDate, LocalDate endDate) {
        return purchasedListingRepository.sumPriceByBusinessIdAndSaleDateBetween(businessId, startDate, endDate);}

    /**
     * For loops through each specified period in a date range and returns the total number of purchases and the total
     * value of those purchases for each date range within a chosen business.
     *
     * @param businessId the id of the business
     * @param startDate the start date of the date range
     * @param endDate the end date of the date range
     * @param firstPeriodStart the start date of the firstPeriod
     * @param lastPeriodEnd the end date fo the last period
     * @param periodOfData the specified period for datta
     * @return a list of SalesReportDtos to be sent to the frontend
     */
    public List<SalesReportSinglePeriod> getSalesReportDataWithPeriod(Integer businessId, LocalDate startDate, LocalDate endDate, LocalDate firstPeriodStart, LocalDate lastPeriodEnd, Period periodOfData) {
        List<SalesReportSinglePeriod> salesReport = new ArrayList<>();
        List<PurchasedListing> purchases = purchasedListingRepository.findAllByBusinessIdAndSaleDateBetweenOrderBySaleDate(businessId, startDate, endDate);
        int curPurchaseIndex = 0;
        for (LocalDate curPeriodStart = firstPeriodStart; curPeriodStart.isBefore(lastPeriodEnd.plusDays(1)); curPeriodStart = curPeriodStart.plus(periodOfData)) {
            LocalDate curPeriodEnd = curPeriodStart.plus(periodOfData).minusDays(1);
            int totalPurchases = 0;
            double totalValue = 0.0;

            // Add up all purchases within current period
            while (curPurchaseIndex < purchases.size() && !purchases.get(curPurchaseIndex).getSaleDate().isAfter(curPeriodEnd)) {
                totalPurchases++;
                totalValue += purchases.get(curPurchaseIndex).getPrice();
                curPurchaseIndex++;
            }

            salesReport.add(new SalesReportSinglePeriod(
                    curPeriodStart.isBefore(startDate) ? startDate : curPeriodStart, // We still want to show original (filter) start date rather than the start of the period of the start date
                    curPeriodEnd.isAfter(endDate) ? endDate : curPeriodEnd,
                    totalPurchases, totalValue));
        }
        return salesReport;
    }

    /**
     * Returns the total number of purchases and the total value of those purchases for a specified date range and
     * business.
     *
     * @param businessId the id of the business
     * @param startDate the start date of the date range
     * @param endDate the end date of the date range
     * @return a list with a single SalesReportDto to be sent to the frontend
     */
    public List<SalesReportSinglePeriod> getSalesReportDataNoPeriod(Integer businessId, LocalDate startDate, LocalDate endDate) {

        Integer totalPurchases = this.countPurchasedListingForBusinessInDateRange(businessId, startDate, endDate);
        Double totalValue = this.totalPurchasedListingValueForBusinessInDateRange(businessId, startDate, endDate);

        if (totalValue == null) {
            totalValue = 0.0;
        }

        SalesReportSinglePeriod reportDto = new SalesReportSinglePeriod(startDate, endDate, totalPurchases, totalValue);
        List<SalesReportSinglePeriod> salesReport = new ArrayList<>();
        salesReport.add(reportDto);

        return salesReport;
    }


    /**
     * Takes a product of a business and saves a purchase listing record on a random day within the last 3 years
     * this happens a set amount of time for each product, the values of cost, quantity, closing date and likes are all
     * randomized.
     * Additionally, after the method finishes the created date of the business will be guaranteed to be not later
     * than the earliest listing creation date
     * @param product product to be purchased
     * @param user user that is purchasing the products (user not used in analysis so doesnt matter)
     * @param business business product belongs to
     */
    public void generatePurchasesForProduct(Product product, User user, Business business) {
        Random generator = ThreadLocalRandom.current();
        int amountOfPurchases = generator.nextInt(20)+1;
        List<PurchasedListing> fakePurchases = new ArrayList<>();
        LocalDate earliestListingDate = LocalDate.now();

        for (int i=0; i < amountOfPurchases; i++) {
            PurchasedListing fakeListing = new PurchasedListing();
            fakeListing.setBusiness(business);
            fakeListing.setPurchaser(user);
            fakeListing.setSaleDate(LocalDate.now().minusDays(generator.nextInt(365*3)));
            fakeListing.setListingDate(fakeListing.getSaleDate().minusDays(generator.nextInt(7)));
            fakeListing.setClosingDate(fakeListing.getSaleDate().plusDays(generator.nextInt(7)+1L).atTime(LocalTime.now()));
            fakeListing.setProduct(product);
            fakeListing.setQuantity(generator.nextInt(5) + 1);
            fakeListing.setManufacturer(product.getManufacturer());
            double price = Math.round(generator.nextDouble()*30);
            fakeListing.setPrice(price);
            fakeListing.setNumberOfLikes(generator.nextInt(50));

            fakePurchases.add(fakeListing);

            if (fakeListing.getListingDate().isBefore(earliestListingDate)) {
                earliestListingDate = fakeListing.getListingDate();
            }
        }
        if (earliestListingDate.isBefore(business.getCreated())) {
            business.setCreated(earliestListingDate);
        }
        purchasedListingRepository.saveAll(fakePurchases);
    }


    /**
     * Gets the number of sales listings, grouped by the duration between the
     * listings??? purchase and closing dates.
     *
     * @param businessId  Business to get purchases for
     * @param startDate   The start date for the date range.
     * @param endDate     The end date for the date range.
     * @param granularity The granularity of the groupings of listings, i.e. the duration of a single group (in days).
     *                    Should not be 0 or less.
     * @return Map where the keys are the durations in days between the listings??? purchase and closing dates,
     * and the values are the number of sales listings
     * @throws ResponseStatusException If granularity is less than or equal to 0
     */
    public Map<Long, Integer> countSalesByDurationBetweenSaleAndClose(Integer businessId, LocalDate startDate, LocalDate endDate, Integer granularity) {
        if (granularity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Granularity must be greater than 0");
        }

        Map<Long, Integer> durationCounts = new HashMap<>();
        List<PurchasedListing> purchases = purchasedListingRepository.findAllByBusinessIdAndSaleDateBetweenOrderBySaleDate(businessId, startDate, endDate);

        for (PurchasedListing purchase : purchases) {
            long daysBetweenSaleAndClose = ChronoUnit.DAYS.between(purchase.getSaleDate(), purchase.getClosingDate());
            long granulatedDaysBetween = daysBetweenSaleAndClose - daysBetweenSaleAndClose % granularity;
            durationCounts.merge(granulatedDaysBetween, 1, Integer::sum);
        }

        return durationCounts;
    }

    /**
     * For a given business, find all the products that have been sold any number of times (a PurchasedListing exists)
     * and return a list of SalesReportPurchaseTotalsDto. Each SalesReportPurchaseTotalsDto contains information about
     * a given sold product, including the number of the product sold, the total value all products sold for, and the
     * total number of likes.
     *
     * @param businessId The id of the business
     * @param startDate  The start date for the date range.
     * @param endDate    The end date for the date range.
     * @param pageable   An object specifying pagination and sorting data
     * @return List of SalesReportPurchaseTotalsDto populated with sale information for each product.
     */

    public List<SalesReportProductTotalsDto> getProductsPurchasedTotals(int businessId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        List<ProductSummary> allSoldProductsOfBusiness = purchasedListingRepository.getPurchasesGroupedByProduct(businessId, startDate, endDate, pageable);

        return allSoldProductsOfBusiness.stream().map(
                summary -> {
                    Product product = productRepository.findFirstByDatabaseId(summary.getProductId());
                    return new SalesReportProductTotalsDto(product, summary.getQuantity(), summary.getValue(), summary.getLikes());
                }
        ).collect(Collectors.toList());
    }

    /**
     * For a given business, and date range, find all the manufacturers whose products have been sold at least once
     * and return data for each. Data includes the number of sales of a manufacturer's products, the total value all
     * @param businessId The id of the business
     * @param startDate  The start date for the date range.
     * @param endDate    The end date for the date range.
     * @param pageable   An object specifying pagination and sorting data
     * @return List of SalesReportManufacturerTotalsDto populated with sale information for each manufacturer.
     */
    public List<SalesReportManufacturerTotalsDto> getManufacturersPurchasedTotals(int businessId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return purchasedListingRepository.getPurchasesGroupedByManufacturer(businessId, startDate, endDate, pageable)
                .stream().map(SalesReportManufacturerTotalsDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all purchased listings for a business.
     *
     * @param businessId    The id of the business
     * @return              all purchased listings of given business
     */
    public List<PurchasedListing> getAllPurchasedListingsForBusiness(Integer businessId) {
        return purchasedListingRepository.findAllByBusinessId(businessId);
    }


    /**
     * Get all the sales report data in a as a csv in a ByteArrayInputStream
     *
     * @param businessId  The id of the business
     * @return          ByteArrayInputStream of csv file of all the data.
     */
    public ByteArrayInputStream getSalesReportCSVByteSteam(Integer businessId) {
        List<PurchasedListing> allPurchasedListings = this.getAllPurchasedListingsForBusiness(businessId);

        String[] csvHeader = {
                "saleDate", "numberOfLikes", "listingDate", "closingDate", "productName", "productId", "quantity", "price", "manufacturer"
        };

        List<List<String>> csvBody = new ArrayList<>();
        for (PurchasedListing purchasedListing: allPurchasedListings) {
            csvBody.add(Arrays.asList(purchasedListing.getSaleDate().toString(), purchasedListing.getNumberOfLikes().toString(),
                    purchasedListing.getListingDate().toString(), purchasedListing.getClosingDate().toString(),
                    purchasedListing.getProduct().getName(), purchasedListing.getProduct().getId(), purchasedListing.getQuantity().toString(),
                    purchasedListing.getPrice().toString(), purchasedListing.getManufacturer()));
        }

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // defining the CSV printer
                CSVPrinter csvPrinter = new CSVPrinter(
                        new PrintWriter(out),
                        // withHeader is optional
                        CSVFormat.DEFAULT.withHeader(csvHeader)
                );
        ) {
            // populating the CSV content
            for (List<String> line : csvBody)
                csvPrinter.printRecord(line);

            csvPrinter.flush();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
