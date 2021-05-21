package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PostListingsDtoMapper {

    private static InventoryService inventoryService;

    @Autowired
    public PostListingsDtoMapper(InventoryService inventoryService) {
        PostListingsDtoMapper.inventoryService = inventoryService;
    }

    public static Listing postListingsDto(PostListingsDto listingsDto) {

        Inventory inventory = inventoryService.findInventoryById(listingsDto.getInventoryItemId());

        Double listingPrice = listingsDto.getPrice();
        Double itemPrice = inventory.getPricePerItem();
        if (itemPrice != null && listingsDto.getQuantity() < inventory.getQuantity()) {
            listingPrice = itemPrice * listingsDto.getQuantity();
        }

        LocalDate closeDate = listingsDto.getCloses();
        if (closeDate == null) {
            closeDate = inventory.getExpires();   //backlog says product expiry date. wtf do they want?
        }


        Listing listing = new Listing();
        listing.setInventoryItem(inventory);
        listing.setQuantity(listingsDto.getQuantity());
        listing.setPrice(listingPrice);
        listing.setMoreInfo(listingsDto.getMoreInfo());
        listing.setCloses(closeDate);


        return listing;
    }


}
