package com.seng302.wasteless.model;
/**
 * Simply an interface for JPA projections. In this case it is used as a return value for a custom
 * GROUP BY query. See PurchasedListingRepository.getPurchasesGroupedByManufacturer
 * and https://stackoverflow.com/questions/36328063/how-to-return-a-custom-object-from-a-spring-data-jpa-group-by-query
 */
public interface ManufacturerSummary {
    String getManufacturer();

    Integer getQuantity();

    Double getValue();

    Integer getLikes();
}
