package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.BusinessViews;
import com.seng302.wasteless.view.ProductViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Product {

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
    @Column(name = "recommended_retail_price")
    private Double recommendedRetailPrice;

    @JsonView({ProductViews.PostProductRequestView.class})
    @Column(name = "created")
    private LocalDate created;

    @Column(name = "business_id")
    private Integer businessId;

//    @Column(name = "images")
//    private List images;

}
