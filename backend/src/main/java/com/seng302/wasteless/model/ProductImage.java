package com.seng302.wasteless.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * An implementation of ProductImage model.
 * This class creates a ProductImage JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method, excluded to prevent recursive problems
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Accessors(chain = true) //Allows chaining of getters and setters
public class ProductImage {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @NotNull(message = "Must have database ID")
    @ManyToOne
    @JoinColumn(name = "database_id")
    private Product product;

    @NotNull(message = "Must have a filename")
    @Column(name = "fileName")
    private String fileName;

    @NotNull(message = "Must have a thumbnail filename")
    @Column(name = "thumbnailFilename")
    private String thumbnailFilename;
}
