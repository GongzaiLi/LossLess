package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetBusinessesDto;
import com.seng302.wasteless.dto.GetBusinessesDtoAdmin;
import com.seng302.wasteless.dto.GetSaleListingDto;
import com.seng302.wasteless.model.*;
import com.seng302.wasteless.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * GetSaleListingsDtoMapper is used to transform the Listing entity into a GetSaleListings object.
 * This includes getting the information about the business of the listing.
 */
@Component
public class GetSaleListingDtoMapper {

    private static BusinessService businessService;


    @Autowired
    public GetSaleListingDtoMapper(BusinessService businessService) { GetSaleListingDtoMapper.businessService = businessService; }

    public static GetSaleListingDto toGetSaleListingDto(Listing listing) {

        Business business = businessService.findBusinessById(listing.getBusinessId());

        return new GetSaleListingDto()
            .setId(listing.getId())
            .setInventoryItem(listing.getInventoryItem())
            .setQuantity(listing.getQuantity())
            .setPrice(listing.getPrice())
            .setMoreInfo(listing.getMoreInfo())
            .setCreated(listing.getCreated())
            .setCloses(listing.getCloses())
            .setBusinessName(business.getName())
            .setBusinessAddress(business.getAddress());
    }
}
