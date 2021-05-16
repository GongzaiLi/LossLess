package com.seng302.wasteless.model;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data()
public class Listings {        //mock class needs an entity
    @NotNull
    @Column
    private Integer businessId;

    private Integer id;
    private Inventory inventoryItem;
    private Integer quantity;
    private Double price;
    private String moreInfo;
    private LocalDate created;
    private LocalDate closes;


}
