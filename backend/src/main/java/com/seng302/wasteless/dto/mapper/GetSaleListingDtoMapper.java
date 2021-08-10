package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetSaleListingDto;
import com.seng302.wasteless.model.*;
import org.springframework.stereotype.Component;



/**
 * GetSaleListingsDtoMapper is used to transform the Listing entity into a GetSaleListings object.
 * This includes getting the information about the business of the listing.
 */
@Component
public class GetSaleListingDtoMapper {


    public static GetSaleListingDto toGetSaleListingDto(Listing listing) {

        Business business = listing.getBusiness();

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
