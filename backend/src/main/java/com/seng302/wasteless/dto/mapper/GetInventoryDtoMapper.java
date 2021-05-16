package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetBusinessesDto;
import com.seng302.wasteless.dto.GetBusinessesDtoAdmin;
import com.seng302.wasteless.dto.GetInventoryDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.InventoryService;
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
public class GetInventoryDtoMapper {

    private static BusinessService businessService;
    private static InventoryService inventoryService;
    private static UserService userService;


    @Autowired
    public GetInventoryDtoMapper(BusinessService businessService, InventoryService inventoryService, UserService userService) {
        GetInventoryDtoMapper.businessService = businessService;
        GetInventoryDtoMapper.inventoryService = inventoryService;
        GetInventoryDtoMapper.userService = userService;

    }

    public static GetInventoryDto toGetBusinessesInventoryDto(Inventory inventory) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalEmail = authentication.getName();
//        User loggedInUser = userService.findUserByEmail(currentPrincipalEmail);
//        UserRoles currentUserRole = loggedInUser.getRole();                     //get the role of Currently logged in user

//        List<User> businessAdministrators = business.getAdministrators();


//        if (currentUserRole.equals(UserRoles.GLOBAL_APPLICATION_ADMIN) ||
//                currentUserRole.equals(UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN)
//                || businessAdministrators.contains(loggedInUser)) {


            return new GetInventoryDto()
                    .setId(inventory.getId())
                    .setProduct(inventory.getProduct())
                    .setQuantity(inventory.getQuantity())
                    .setPricePerItem(inventory.getPricePerItem())
                    .setTotalPrice(inventory.getTotalPrice())
                    .setManufactured(inventory.getManufactured())
                    .setSellBy(inventory.getSellBy())
                    .setBestBefore(inventory.getBestBefore())
                    .setExpires(inventory.getExpires());


//        }
    }
}
