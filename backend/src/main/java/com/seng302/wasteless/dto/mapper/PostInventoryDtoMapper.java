package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostInventoryDtoMapper {

    public static Inventory postInventoryDtoToEntityMapper(PostInventoryDto inventoryDto) {

        return new Inventory()
        .setExpires(inventoryDto.getExpires())
        .setBestBefore(inventoryDto.getBestBefore())
        .setManufactured(inventoryDto.getManufactured())
        .setQuantity(inventoryDto.getQuantity())
        .setPricePerItem(inventoryDto.getPricePerItem())
        .setSellBy(inventoryDto.getSellBy())
        .setTotalPrice(inventoryDto.getTotalPrice());
    }

}
