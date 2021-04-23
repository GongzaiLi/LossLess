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

        //UserRoles currentUserRole = userService.findUserByEmail(login.getEmail()).getRole(); //get the role of Currently logged in user

        User tempUser = new User();    //just for testing. Implement this later ...
        tempUser.setRole(UserRoles.USER); //changing this should change the data that is returned
        tempUser.setEmail("admin@700");
        tempUser.setPassword("password");

        UserRoles currentUserRole = tempUser.getRole();




//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalEmail = authentication.getName();
//
//        User loggedInUser = userService.findUserByEmail(currentPrincipalEmail);
//        UserRoles currentUserRole = loggedInUser.getRole();


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
                .setEmail(user.getEmail())
                .setDateOfBirth(user.getDateOfBirth().toString())
                .setPhoneNumber(user.getPhoneNumber())
                .setHomeAddress(user.getHomeAddress())
                .setCreated(user.getCreated().toString());

        if (currentUserRole.equals(UserRoles.GLOBAL_APPLICATION_ADMIN) ||
                currentUserRole.equals(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)) {
            getUserDto
                    .setRole(user.getRole());

        }
        getUserDto
                .setBusinessesAdministered(businessesAdministered);

        return getUserDto;

    }
}
