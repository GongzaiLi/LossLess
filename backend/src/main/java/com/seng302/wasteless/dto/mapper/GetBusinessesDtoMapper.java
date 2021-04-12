package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetBusinessesDto;
import com.seng302.wasteless.model.Administrator;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetBusinessesDtoMapper {

    private static BusinessService businessService;

    @Autowired
    public GetBusinessesDtoMapper(BusinessService businessService) {
        GetBusinessesDtoMapper.businessService = businessService;
    }

    public static GetBusinessesDto toGetBusinessesDto(Business business) {

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

        return new GetBusinessesDto()
                .setId(business.getId())
                .setAdministrators(formattedAdministrators)
                .setPrimaryAdministratorId(business.getPrimaryAdministrator().getId())
                .setName(business.getName())
                .setDescription(business.getDescription())
                .setAddress(business.getAddress())
                .setBusinessType(business.getBusinessType())
                .setCreated(business.getCreated().toString());
    }
}
