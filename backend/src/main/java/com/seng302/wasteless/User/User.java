package com.seng302.wasteless.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class User {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @JsonView({UserViews.GetUserView.class, UserViews.SearchUserView.class})
    private Integer id;

    @Column(name = "first_name") // map camelcase name (java) to snake case (SQL)
    @NotBlank(message = "firstName is mandatory")
    @JsonView({UserViews.GetUserView.class, UserViews.SearchUserView.class})
    private String firstName;

    @NotBlank(message = "lastName is mandatory")
    @Column(name = "last_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class, UserViews.SearchUserView.class})
    private String lastName;

    @Column(name = "middle_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class, UserViews.SearchUserView.class})
    private String middleName;

    @Column(name = "nick_name") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class, UserViews.SearchUserView.class})
    private String nickname;

    @Column(name = "bio") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class, UserViews.SearchUserView.class})
    private String bio;

    @NotBlank(message = "email is mandatory")
    @Column(name = "email") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class})
    private String email;

    @NotNull(message = "dateOfBirth is mandatory")
    @Column(name = "date_of_birth") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class})
    //Date format validated by spring. See spring.mvc.format.date=yyyy-MM-dd in application.properties
    private LocalDate dateOfBirth;

    @Column(name = "phone_number") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class})
    private String phoneNumber;

    @NotBlank(message = "homeAddress is mandatory")
    @Column(name = "home_address") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class, UserViews.SearchUserView.class})
    private String homeAddress;

    @NotBlank(message = "password is mandatory")
    @Column(name = "password") // map camelcase name (java) to snake case (SQL)
    private String password;

    @Column(name = "salt") // map camelcase name (java) to snake case (SQL)
    private String salt;

    @Column(name = "created") // map camelcase name (java) to snake case (SQL)
    @JsonView({UserViews.GetUserView.class})
    private LocalDate created;

    //Omitted fields. Role: added in u4, business administered: added in u6

    /**
     * Convenience constructor (it's discouraged to expose the JPA id field)
     *
     * @param firstName student first name
     * @param lastName  student last name
     */
    public User(String firstName, String lastName, String email, LocalDate dateOfBirth, String homeAddress, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.password = password;
    }

    /**
     * Check this objects date is within the expected maximum and minimum date ranges
     */
    public boolean checkDateOfBirthValid() {
        //Todo minimum age to allow
        LocalDate today = LocalDate.now();

        LocalDate minimumDOB = today.minusYears(0);
        LocalDate maximumDOB = today.minusYears(120);

        return (this.dateOfBirth.isBefore(minimumDOB) && this.dateOfBirth.isAfter(maximumDOB));
    }

}

