package com.seng302.wasteless.service;

import com.seng302.wasteless.dto.SalesReportDto;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.PurchasedListing;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.repository.PurchasedListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * PurchasedListing service applies product logic over the Product JPA repository.
 */
@Service
public class PurchasedListingService {

    private final PurchasedListingRepository purchasedListingRepository;

    @Autowired
    public PurchasedListingService(PurchasedListingRepository purchasedListingRepository) {
        this.purchasedListingRepository = purchasedListingRepository;
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
     * Returns the total number of purchased listings for a business.
     * @param businessId Id of the business
     * @return The count of purchased listings
     */
    public Integer countPurchasedListingForBusiness(Integer businessId) {return purchasedListingRepository.countAllByBusiness_Id(businessId);}

    /**
     * Returns the total number of purchased listings for a business
     * in a specified date range
     * @param businessId Id of the business
     * @param startDate start date of range
     * @param endDate end date of range
     * @return The count of purchased listings
     */
    public Integer countPurchasedListingForBusinessInDateRange(Integer businessId, LocalDate startDate, LocalDate endDate) {
        return purchasedListingRepository.countAllByBusiness_IdAndSaleDateBetween(businessId, startDate, endDate);}

    /**
     * Returns the total value of purchased listings for a business.
     * @param businessId Id of the business
     * @return The count of purchased listings
     */
    public Integer totalPurchasedListingValueForBusiness(Integer businessId) {
        return purchasedListingRepository.sumPriceByBusiness_Id(businessId);}

    /**
     * Returns the total value of purchased listings for a business.
     * @param businessId    Id of the business
     * @param startDate     The start date for the date range. Format yyyy-MM-dd
     * @param endDate       The end date for the date range. Format yyyy-MM-dd
     * @return The count of purchased listings
     */
    public Double totalPurchasedListingValueForBusinessInDateRange(Integer businessId, LocalDate startDate, LocalDate endDate) {
        return purchasedListingRepository.sumPriceByBusiness_IdAndSaleDateBetween(businessId, startDate, endDate);}

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
    public List<SalesReportDto> getSalesReportDataWithPeriod(Integer businessId, LocalDate startDate, LocalDate endDate, LocalDate firstPeriodStart, LocalDate lastPeriodEnd, Period periodOfData) {
        List<SalesReportDto> responseBody = new ArrayList<>();

        LocalDate searchStart;
        LocalDate searchEnd;
        for (LocalDate date = firstPeriodStart; date.isBefore(lastPeriodEnd.plusDays(1)); date = date.plus(periodOfData)) {
            searchStart = date;
            searchEnd = searchStart.plus(periodOfData).minusDays(1);
            if (searchStart.isBefore(startDate)) {
                searchStart = startDate;
            }
            if (searchEnd.isAfter(endDate)) {
                searchEnd = endDate;
            }
            Integer totalPurchases = this.countPurchasedListingForBusinessInDateRange(businessId, searchStart, searchEnd);
            Double totalValue = this.totalPurchasedListingValueForBusinessInDateRange(businessId, searchStart, searchEnd);

            if (totalValue == null) {
                totalValue = 0.0;
            }

            SalesReportDto reportDto = new SalesReportDto(searchStart, searchEnd, totalPurchases, totalValue);
            responseBody.add(reportDto);
        }
        return responseBody;
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
    public List<SalesReportDto> getSalesReportDataNoPeriod(Integer businessId, LocalDate startDate, LocalDate endDate) {

        Integer totalPurchases = this.countPurchasedListingForBusinessInDateRange(businessId, startDate, endDate);
        Double totalValue = this.totalPurchasedListingValueForBusinessInDateRange(businessId, startDate, endDate);

        if (totalValue == null) {
            totalValue = 0.0;
        }

        SalesReportDto reportDto = new SalesReportDto(startDate, endDate, totalPurchases, totalValue);
        List<SalesReportDto> responseBody = new ArrayList<>();
        responseBody.add(reportDto);

        return responseBody;
    }


    /**
     * Takes a product of a business and saves a purchase listing record on a random day within the last 3 years
     * this happens a set amount of time fro each prodct, teh values of cost, quantity, closing date and likes are all
     * randomized
     * @param product product to be purchased
     * @param user user that is purchasing the products (user not used in analysis so doesnt matter)
     * @param business business product belongs to
     */
    public void purchaseGeneratedProduct(Product product, User user, Business business) {
        Random generator = ThreadLocalRandom.current();
        int amountOfPurchases = generator.nextInt(20)+1;
        List<PurchasedListing> fakePurchases = new ArrayList<>();
        for (int i=0; i< amountOfPurchases; i++) {
            PurchasedListing fakeListing = new PurchasedListing();
            fakeListing.setBusiness(business);
            fakeListing.setPurchaser(user);
            fakeListing.setSaleDate(LocalDate.now().minusDays(generator.nextInt(365*3)));
            fakeListing.setListingDate(fakeListing.getSaleDate().minusDays(generator.nextInt(7)));
            fakeListing.setClosingDate(fakeListing.getSaleDate().plusDays(generator.nextInt(7)));
            fakeListing.setProduct(product);
            fakeListing.setQuantity(generator.nextInt(5) + 1);
            double price = Math.round(generator.nextDouble()*30);
            fakeListing.setPrice(price);
            fakeListing.setNumberOfLikes(generator.nextInt(50));

            fakePurchases.add(fakeListing);
        }
        purchasedListingRepository.saveAll(fakePurchases);
    }

}
