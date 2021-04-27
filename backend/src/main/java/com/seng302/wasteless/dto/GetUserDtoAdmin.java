package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.BusinessAdministered;
import com.seng302.wasteless.model.UserRoles;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * Data transfer object for GetUser endpoint, used to return the correct data in the correct format,
 * for application admins and when user get their own data.
 * User entities are transformed into GetUserDto via the GetUserDtoMapper
 */

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class GetUserDtoAdmin extends GetUserDto {
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
    private List<BusinessAdministered> businessesAdministered;
}
