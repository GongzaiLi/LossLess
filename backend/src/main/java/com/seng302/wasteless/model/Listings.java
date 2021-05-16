package com.seng302.wasteless.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data()
@Accessors(chain = true) //Allows chaining of getters and setters
public class Listings {        //mock class needs an entity
    @NotNull
    @Column
    private Integer businessId;
    private Integer id;
    private Long inventoryItemId;     //this is string on the spec
    private Integer quantity;
    private Double price;
    private String moreInfo;
    private LocalDate created;
    private LocalDate closes;


}
