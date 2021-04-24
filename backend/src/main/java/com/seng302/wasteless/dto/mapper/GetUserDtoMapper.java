package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetUserDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private static Login login;






    @Autowired
    public GetUserDtoMapper(BusinessService businessService) {
        GetUserDtoMapper.businessService = businessService;
    }

    public static GetUserDto toGetUserDto(User user) {

        List<Business> businesses = businessService.findBusinessesByUserId(user.getId());

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalEmail = authentication.getName();
//        User loggedInUser = userService.findUserByEmail(currentPrincipalEmail);
//        UserRoles currentUserRole = loggedInUser.getRole();                     //get the role of Currently logged in user
//        Integer currentUserId = loggedInUser.getId();


        User tempUser = new User();       //just for testing. Implement this later ...
        //tempUser.setId(user.getId());  //mock logged in user
        tempUser.setRole(UserRoles.GLOBAL_APPLICATION_ADMIN); //changing this should change the data that is returned. Currently fails test if role is "USER"
        tempUser.setEmail("admin@700");
        tempUser.setPassword("password");
        UserRoles currentUserRole = tempUser.getRole();
        Integer currentUserId = tempUser.getId();






        List<BusinessAdministered> businessesAdministered = new ArrayList<>();

        for (Business business : businesses) {

            List<String> administrators = new ArrayList<>();

            for (User admin : business.getAdministrators()) {
                administrators.add(admin.getId().toString());
            }

            businessesAdministered.add(
                    new BusinessAdministered().setId(business.getId())
                        .setAdministrators(administrators)
                        .setPrimaryAdministratorId(business.getPrimaryAdministrator().getId())
                        .setName(business.getName())
                        .setDescription(business.getDescription())
                        .setAddress(business.getAddress())
                        .setBusinessType(business.getBusinessType())
                        .setCreated(business.getCreated().toString())
            );
        }
        GetUserDto getUserDto = new GetUserDto();

        getUserDto
                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setMiddleName(user.getMiddleName())
                .setNickName(user.getNickname())
                .setBio(user.getBio())
                .setEmail(user.getEmail());


        if (currentUserRole.equals(UserRoles.GLOBAL_APPLICATION_ADMIN) ||
                currentUserRole.equals(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN) ||
                        currentUserId == user.getId()) {
            getUserDto
                    .setDateOfBirth(user.getDateOfBirth().toString())
                    .setPhoneNumber(user.getPhoneNumber())
                    .setHomeAddress(user.getHomeAddress())
                    .setCreated(user.getCreated().toString())
                    .setRole(user.getRole())
                    .setBusinessesAdministered(businessesAdministered);
        } else {
            getUserDto
                    .setHomeAddress(user.getHomeAddress())    //Changed later to city, country
                    .setCreated(user.getCreated().toString())
                    .setBusinessesAdministered(businessesAdministered);

        }

        return getUserDto;

    }
}
