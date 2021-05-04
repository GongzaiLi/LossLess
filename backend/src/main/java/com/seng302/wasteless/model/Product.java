package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.ProductViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * An implementation of Product model.
 * This class creates a Product JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Product {

    @JsonView({ProductViews.PostProductRequestView.class})
    @Id // this field (attribute) is the table primary key
    @Column(name = "code")
    private String id;

    @JsonView({ProductViews.PostProductRequestView.class})
    @Column(name = "name")
    @NotBlank(message = "product name is mandatory")
    private String name;

    @JsonView({ProductViews.PostProductRequestView.class})
    @Column(name = "description")
    private String description;

    @JsonView({ProductViews.PostProductRequestView.class})
    @Column(name = "manufacturer")
    private String manufacturer;

    @JsonView({ProductViews.PostProductRequestView.class})
    @Column(name = "recommended_retail_price")
    private Double recommendedRetailPrice;

    @JsonView({ProductViews.PostProductRequestView.class})
    @Column(name = "created")
    private LocalDate created;

    @Column(name = "business_id")
    private Integer businessId;

//    @Column(name = "images")
//    private List images;

    /**
     * Formats a code by taking the BusinessId and the Product Object's ID
     * setting it to uppercase and replaces spaces with "-" and replaces
     * and removes alphanumeric
     *
     * @param businessId
     * @return String that is formatted as the Product Code
     */
    public String createCode(Integer businessId) {
        return businessId + "-" + this.getId().toUpperCase().replaceAll("\\P{Alnum}+$", "")
                .replaceAll(" ", "-");
    }

}
