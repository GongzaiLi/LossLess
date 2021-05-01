package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.BusinessAdministered;
import com.seng302.wasteless.model.UserRoles;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.minidev.json.JSONObject;

import java.util.List;


/**
 * Data transfer object for GetUser endpoint, used to return the correct data in the correct format.
 * User entities are transformed into GetUserDto via the GetUserDtoMapper
 */

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class GetUserDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickName;
    private String bio;
    private String email;                      //change later
    private JSONObject homeAddress;
    private String created;
}
