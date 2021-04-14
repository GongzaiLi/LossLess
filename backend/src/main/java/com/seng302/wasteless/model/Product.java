package com.seng302.wasteless.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.BusinessViews;
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

    @PrePersist
    public void prePersist() {
        if(id == null || id == "") //Set the default value if one is not given.
            id = name.toUpperCase().substring(0, 20).stripTrailing().replaceAll(" ", "-");
    }

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer productId;

    @Column(name = "name")
    @NotBlank(message = "product name is mandatory")
    private String name;

    @Column(name = "code")
    private String id;

    @Column(name = "description")
    private String description;

    @Column(name = "recommended_retail_price")
    private String recommendedRetailPrice;

    @Column(name = "created")
    private LocalDate created;

    @Column(name = "business_id")
    private Integer businessId;

//    @Column(name = "images")
//    private List images;


}
