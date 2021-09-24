package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.BusinessTypes;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Data transfer object for Put Business endpoint, used to return the correct data in the correct format.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class PutBusinessDto {

    @NotBlank(message = "name is mandatory")
    @Column(name = "name")
    @Size(max = 50)
    private String name;

    @Column(name = "description")
    @Size(max = 250)
    private String description;

    @NotNull
    @OneToOne
    @JoinColumn(name = "address") // map camelcase name (java) to snake case (SQL)
    private Address address;

    @NotNull(message = "businessType is mandatory")
    @Column(name = "business_type")
    private BusinessTypes businessType;
}
