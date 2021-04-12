package com.seng302.wasteless.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class Administrator {
    private Integer id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickName;
    private String bio;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private String homeAddress;
    private String created;
    private UserRoles role;
    private List<String> businessesAdministered;
}
