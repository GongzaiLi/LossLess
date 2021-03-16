package com.seng302.wasteless.Business;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.User.User;
import com.seng302.wasteless.User.UserViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;

@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Business {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @JsonView({UserViews.GetUserView.class, UserViews.SearchUserView.class})
    private Integer id;

    @Column(name = "administrators")
    private ArrayList<User> administrators;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "business_type")
    private String BusinessType;

    @Column(name = "created")
    private LocalDate created;



}
