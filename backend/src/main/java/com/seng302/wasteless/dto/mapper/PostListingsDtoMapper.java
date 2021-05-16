package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.dto.PostListingsDto;
import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Listings;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.service.InventoryService;
import com.seng302.wasteless.service.ProductService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Component
public class PostListingsDtoMapper {

    private static InventoryService inventoryService;

    @Autowired
    public PostListingsDtoMapper(InventoryService inventoryService) {
        PostListingsDtoMapper.inventoryService = inventoryService;
    }

    public static Listings postListingsDto(PostListingsDto listingsDto) {

        Inventory inventory = inventoryService.findInventoryById(listingsDto.getInventoryItemId());


        Listings listings = new Listings();
        listings.setInventoryItemId(inventory.getId());
        listings.setQuantity(listingsDto.getQuantity());
        listings.setPrice(listingsDto.getPrice());
        listings.setMoreInfo(listingsDto.getMoreInfo());
        listings.setCreated(listingsDto.getCreated());
        listings.setCloses(listingsDto.getCloses());

        ;

        return listings;
    }


}
