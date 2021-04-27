package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetBusinessesDto;
import com.seng302.wasteless.dto.GetBusinessesDtoAdmin;
import com.seng302.wasteless.model.Administrator;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * GetBusinessesDtoMapper is used to transform the Business entity into a GetBusinessesDto object.
 * This includes getting users (administrators) associated with the business,
 * and the information associated with those users.
 */

@Component
public class GetBusinessesDtoMapper {

    private static BusinessService businessService;
    private static UserService userService;


    @Autowired
    public GetBusinessesDtoMapper(BusinessService businessService, UserService userService) {
        GetBusinessesDtoMapper.businessService = businessService;
        GetBusinessesDtoMapper.userService = userService;

    }

    public static GetBusinessesDto toGetBusinessesDto(Business business) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalEmail = authentication.getName();
        User loggedInUser = userService.findUserByEmail(currentPrincipalEmail);
        UserRoles currentUserRole = loggedInUser.getRole();                     //get the role of Currently logged in user
        Integer currentUserId = loggedInUser.getId();



        if (currentUserRole.equals(UserRoles.GLOBAL_APPLICATION_ADMIN) ||
                currentUserRole.equals(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
                //|| currentUserId == user.getId()
        ) {

            List<User> businessAdministrators = business.getAdministrators();

            List<Administrator> formattedAdministrators = new ArrayList<>();

            for (User businessAdministrator : businessAdministrators) {

                List<Business> businessesAdministered = businessService.findBusinessesByUserId(businessAdministrator.getId());

                List<String> businessesAdministeredIds = new ArrayList<>();

                for (Business businessAdministered : businessesAdministered) {
                    businessesAdministeredIds.add(businessAdministered.getId().toString());
                }

                formattedAdministrators.add(
                        new Administrator().setId(businessAdministrator.getId())
                                .setFirstName(businessAdministrator.getFirstName())
                                .setLastName(businessAdministrator.getLastName())
                                .setMiddleName(businessAdministrator.getMiddleName())
                                .setNickName(businessAdministrator.getNickname())
                                .setBio(businessAdministrator.getBio())
                                .setEmail(businessAdministrator.getEmail())
                                .setDateOfBirth(businessAdministrator.getDateOfBirth().toString())
                                .setPhoneNumber(businessAdministrator.getPhoneNumber())
                                .setHomeAddress(businessAdministrator.getHomeAddress())
                                .setCreated(businessAdministrator.getCreated().toString())
                                .setRole(businessAdministrator.getRole())
                                .setBusinessesAdministered(businessesAdministeredIds)
                );
            }

            return new GetBusinessesDtoAdmin()
                    .setId(business.getId())
                    .setAdministrators(formattedAdministrators)
                    .setPrimaryAdministratorId(business.getPrimaryAdministrator().getId())
                    .setName(business.getName())
                    .setDescription(business.getDescription())
                    .setAddress(business.getAddress())
                    .setBusinessType(business.getBusinessType())
                    .setCreated(business.getCreated().toString());

        } else {

            return new GetBusinessesDto()
                    .setId(business.getId())
                    .setName(business.getName())
                    .setDescription(business.getDescription())
                    .setAddress(business.getAddress())
                    .setBusinessType(business.getBusinessType())
                    .setCreated(business.getCreated().toString());

        }
    }
}
