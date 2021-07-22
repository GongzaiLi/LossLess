package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetUserBusinessAdministeredDto;
import com.seng302.wasteless.dto.GetUserDto;
import com.seng302.wasteless.dto.GetUserDtoAdmin;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * GetUserDtoMapper is used to transform the User entity into a GetUserDto object.
 * This includes getting businesses associated with the user, and the information associated with those businesses.
 */
@Component
public class GetUserDtoMapper {

    private static BusinessService businessService;
    private static UserService userService;

    @Autowired
    public GetUserDtoMapper(BusinessService businessService, UserService userService) {
        GetUserDtoMapper.businessService = businessService;
        GetUserDtoMapper.userService = userService;
    }

    public static GetUserDto toGetUserDto(User user) {

        User loggedInUser = userService.getCurrentlyLoggedInUser();
        UserRoles currentUserRole = loggedInUser.getRole();                     //get the role of Currently logged in user
        Integer currentUserId = loggedInUser.getId();

        List<Business> businesses = businessService.findBusinessesByUserId(user.getId());
        List<GetUserBusinessAdministeredDto> businessesAdministered = new ArrayList<>();
        for (Business business : businesses) {  // Could do this with streams and maps but Java is too hecking verbose
            businessesAdministered.add(new GetUserBusinessAdministeredDto(business));
        }

        JSONObject address = new JSONObject();
        address.put("city", user.getHomeAddress().getCity());
        address.put("region", user.getHomeAddress().getRegion());
        address.put("country", user.getHomeAddress().getCountry());
        address.put("postcode", user.getHomeAddress().getPostcode());

        if (currentUserRole.equals(UserRoles.GLOBAL_APPLICATION_ADMIN) ||
                currentUserRole.equals(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
                || currentUserId.equals(user.getId())
        ) {
            // Admins and the user itself gets to see the full address
            address.put("streetNumber", user.getHomeAddress().getStreetNumber());
            address.put("streetName", user.getHomeAddress().getStreetName());
            address.put("suburb", user.getHomeAddress().getSuburb());

            return new GetUserDtoAdmin()
                    .setId(user.getId())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setMiddleName(user.getMiddleName())
                    .setNickName(user.getNickname())
                    .setBio(user.getBio())
                    .setEmail(user.getEmail())
                    .setDateOfBirth(user.getDateOfBirth().toString())
                    .setPhoneNumber(user.getPhoneNumber())
                    .setHomeAddress(address)
                    .setCreated(user.getCreated().toString())
                    .setRole(user.getRole())
                    .setBusinessesAdministered(businessesAdministered);
        } else {
            return new GetUserDto()
                    .setId(user.getId())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setMiddleName(user.getMiddleName())
                    .setNickName(user.getNickname())
                    .setBio(user.getBio())
                    .setEmail(user.getEmail())
                    .setHomeAddress(address)
                    .setCreated(user.getCreated().toString())
                    .setBusinessesAdministered(businessesAdministered);
        }
    }
}
