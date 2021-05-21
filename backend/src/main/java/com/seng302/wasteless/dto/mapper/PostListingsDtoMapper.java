package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PostListingsDtoMapper {

    private static InventoryService inventoryService;

    @Autowired
    private PostListingsDtoMapper(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public static Listing postListingsDto(PostListingsDto listingsDto) {

        Inventory inventory = inventoryService.findInventoryById(listingsDto.getInventoryItemId());


        LocalDate closeDate = listingsDto.getCloses();
        if (closeDate == null) {
            closeDate = inventory.getExpires();
        }

        Integer availableQuantity = inventory.getQuantity();
        Integer listingQuantity = listingsDto.getQuantity();

        inventory.setQuantity(availableQuantity-listingQuantity);



       return new Listing()
        .setInventory(inventory)
        .setQuantity(listingsDto.getQuantity())
        .setPrice(listingsDto.getPrice())
        .setMoreInfo(listingsDto.getMoreInfo())
        .setCloses(closeDate);


    }


}
