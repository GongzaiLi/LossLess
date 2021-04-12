package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetUserDto;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.BusinessAdministered;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetUserDtoMapper {

    private static BusinessService businessService;

    @Autowired
    public GetUserDtoMapper(BusinessService businessService) {
        GetUserDtoMapper.businessService = businessService;
    }

    public static GetUserDto toGetUserDto(User user) {

        List<Business> businesses = businessService.findBusinessesByUserId(user.getId());

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

        return new GetUserDto()
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
                .setCreated(user.getCreated().toString())
                .setRole(user.getRole())
                .setBusinessesAdministered(businessesAdministered);

    }
}
