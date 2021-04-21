package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Type for Address object for users and buisnesses used by DTOs to return the correct (and correctly formatted) data.
 */

@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Address {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @NotBlank(message = "street_number is mandatory")
    @Column(name = "street_number")
    private String streetNumber;

    @NotBlank(message = "street_name is mandatory")
    @Column(name = "street_name")
    private String streetName;

    @NotBlank(message = "city is mandatory")
    @Column(name = "city")
    private String city;

    @Column(name = "region")
    private String region;

    @NotBlank(message = "country is mandatory")
    @Column(name = "country")
    private String country;

    @NotBlank(message = "postcode is mandatory")
    @Column(name = "postcode")
    private String postcode;
}