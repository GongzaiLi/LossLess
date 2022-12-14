package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.Administrator;
import com.seng302.wasteless.model.BusinessTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Data transfer object for GetBusiness endpoint, used to return the correct data in the correct format,
 * for business and application admins and when user get their own business data.
 * Business entities are transformed into GetBusinessesDtoAdmin via the GetBusinessesDtoMapper.
 *
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class GetBusinessesDtoAdmin extends GetBusinessesDto {
    private int id;
    private List<Administrator> administrators;
    private int primaryAdministratorId;
    private String name;
    private String description;
    private Address address;
    private BusinessTypes businessType;
    private String created;

}
