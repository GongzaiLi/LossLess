package com.seng302.wasteless.model;

/**
 * Simply an interface for JPA projections. In this case it is used as a return value for a custom
 * GROUP BY query. See https://stackoverflow.com/questions/36328063/how-to-return-a-custom-object-from-a-spring-data-jpa-group-by-query
 */
public interface ProductSummary {
     Long getProductId();

     Integer getQuantity();

     Double getValue();

     Integer getLikes();
}
