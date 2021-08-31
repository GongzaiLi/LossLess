package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Address;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;


/**
 * Data transfer object for PutUser endpoint, used to return the correct data in the correct format.
 */
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class PutUserDto {

    @Column(name = "first_name") // map camelcase name (java) to snake case (SQL)
    @NotBlank(message = "firstName is mandatory")
    @Size(min = 0, max = 50)
    private String firstName;

    @NotBlank(message = "lastName is mandatory")
    @Column(name = "last_name") // map camelcase name (java) to snake case (SQL)
    @Size(min = 0, max = 50)
    private String lastName;

    @Column(name = "middle_name") // map camelcase name (java) to snake case (SQL)
    @Size(min = 0, max = 50)
    private String middleName;

    @Column(name = "nick_name") // map camelcase name (java) to snake case (SQL)
    @Size(min = 0, max = 50)
    private String nickname;

    @Column(name = "bio") // map camelcase name (java) to snake case (SQL)
    @Size(min = 0, max = 250)
    private String bio;

    @Email
    @NotBlank(message = "email is mandatory")
    @Column(name = "email") // map camelcase name (java) to snake case (SQL)
    @Size(min = 0, max = 50)
    private String email;

    @Past
    @NotNull(message = "dateOfBirth is mandatory")
    @Column(name = "date_of_birth") // map camelcase name (java) to snake case (SQL)
    private LocalDate dateOfBirth;

    @Column(name = "phone_number") // map camelcase name (java) to snake case (SQL)
    @Size(min = 0, max = 50)
    private String phoneNumber;

    @NotNull(message = "homeAddress is mandatory")
    @OneToOne
    @JoinColumn(name = "home_address") // map camelcase name (java) to snake case (SQL)
    private Address homeAddress;

    @NotBlank(message = "password is mandatory")
    @Column(name = "password") // map camelcase name (java) to snake case (SQL)
    @Size(min = 0, max = 100)
    private String password;

    @Column(name = "newPassword")
    @Size(min = 0, max = 100)
    private String newPassword;
}

