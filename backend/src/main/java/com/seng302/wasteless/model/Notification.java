package com.seng302.wasteless.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * An implementation of User model.
 * This class creates a User JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Notification {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @Column(name = "user_id")
    @NotNull(message = "User ID is mandatory")
    private Integer userId;

    @NotNull(message = "Type is mandatory")
    @Column(name = "type")
    @Size(min = 0, max = 50)
    private String type;

    @Column(name = "message")
    @Size(min = 0, max = 50)
    private String message;

    @Column(name = "subjectId")
    private Integer subjectId;
}

