package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.BusinessTypes;
import com.seng302.wasteless.model.User;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 'Partial' DTO representing a business object in the 'businessesAdministered' field of the Get User DTO.
 * So far this is not used standalone but as part of the GetUserDto. Main difference between this DTO and the
 * business object is that the administrators is a list of ids, not User objects.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
@Accessors(chain = true) //Allows chaining of getters and setters
public class GetUserBusinessAdministeredDto {
    private int id;
    private int primaryAdministratorId;
    private String name;
    private String description;
    private Address address;
    private BusinessTypes businessType;
    private String created;

    /**
     * Creates an instance of the BusinessAdministered data object given a business, filling all fields with public
     * getters of the business. Administrators list is created as a list of all ids (converted to strings) of the
     * business's admins.
     * @param business The business to create this data object for.
     */
    public GetUserBusinessAdministeredDto(Business business) {
        List<String> administrators = new ArrayList<>();

        for (User admin : business.getAdministrators()) {
            administrators.add(admin.getId().toString());
        }

        this.setId(business.getId())
            .setAdministrators(administrators)
            .setPrimaryAdministratorId(business.getPrimaryAdministrator().getId())
            .setName(business.getName())
            .setDescription(business.getDescription())
            .setAddress(business.getAddress())
            .setBusinessType(business.getBusinessType())
            .setCreated(business.getCreated().toString());
    }
}
