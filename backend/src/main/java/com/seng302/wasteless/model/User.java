package com.seng302.wasteless.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.seng302.wasteless.view.BusinessViews;
import com.seng302.wasteless.view.UserViews;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * An implementation of User model.
 * This class creates a User JPA entity that is mapped to an SQL table.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class User {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @Column(name = "first_name") // map camelcase name (java) to snake case (SQL)
    @NotBlank(message = "firstName is mandatory")
    @JsonView(UserViews.PostUserRequestView.class)
    private String firstName;

    @NotBlank(message = "lastName is mandatory")
    @Column(name = "last_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    private String lastName;

    @Column(name = "middle_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    private String middleName;

    @Column(name = "nick_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    private String nickname;

    @Column(name = "bio") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    private String bio;

    @NotBlank(message = "email is mandatory")
    @Column(name = "email") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    private String email;

    @NotNull(message = "dateOfBirth is mandatory")
    @Column(name = "date_of_birth") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    //Date format validated by spring. See spring.mvc.format.date=yyyy-MM-dd in application.properties
    private LocalDate dateOfBirth;

    @Column(name = "phone_number") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.PostUserRequestView.class})
    private String phoneNumber;

    @JsonView({UserViews.PostUserRequestView.class})
    @NotNull
    @OneToOne
    @JoinColumn(name = "home_address") // map camelcase name (java) to snake case (SQL)
    private Address homeAddress;

    @NotBlank(message = "password is mandatory")
    @JsonView({UserViews.PostUserRequestView.class})
    @Column(name = "password") // map camelcase name (java) to snake case (SQL)
    private String password;

    @Column(name = "businesses_primarily_administered")
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Business> businessesPrimarilyAdministered;

    @Column(name = "created") // map camelcase name (java) to snake case (SQL)
    private LocalDate created;

    @Column(name = "role")
    private UserRoles role;

    /**
     * Check this objects date is within the expected maximum and minimum date ranges
     */
    public boolean checkDateOfBirthValid() {

        LocalDate today = LocalDate.now();

        LocalDate minimumDOB = today.minusYears(13).plusDays(1);
        LocalDate maximumDOB = today.minusYears(120);

        return (this.dateOfBirth.isBefore(minimumDOB) && this.dateOfBirth.isAfter(maximumDOB));
    }
    /**
     * Check this objects date is within the expected maximum and minimum date ranges
     */
    public boolean checkIsOverSixteen() {

        LocalDate today = LocalDate.now();

        LocalDate minimumDOB = today.minusYears(16).plusDays(1);
        LocalDate maximumDOB = today.minusYears(120);

        return (this.dateOfBirth.isBefore(minimumDOB) && this.dateOfBirth.isAfter(maximumDOB));
    }
    /**
     * Add a business to the list of businessesPrimarilyAdministered.
     *
     * Never call this directly, only call it from user service.
     *
     * @param business  The business to add to the list of businesses primarily administered
     */
    public void addPrimaryBusiness(Business business) {
        this.businessesPrimarilyAdministered.add(business);
    }
}

