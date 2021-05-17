package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.model.Listing;
import com.seng302.wasteless.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostListingsDtoMapper {

    private static InventoryService inventoryService;

    @Autowired
    public PostListingsDtoMapper(InventoryService inventoryService) {
        PostListingsDtoMapper.inventoryService = inventoryService;
    }

    public static Listing postListingsDto(PostListingsDto listingsDto) {

        Inventory inventory = inventoryService.findInventoryById(listingsDto.getInventoryItemId());


        Listing listing = new Listing();
        listing.setInventory(inventory);
        listing.setQuantity(listingsDto.getQuantity());
        listing.setPrice(listingsDto.getPrice());
        listing.setMoreInfo(listingsDto.getMoreInfo());
        listing.setCreated(listingsDto.getCreated());
        listing.setCloses(listingsDto.getCloses());

        ;

        return listing;
    }


}
