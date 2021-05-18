package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostInventoryDtoMapper {

    private static ProductService productService;

    @Autowired
    public PostInventoryDtoMapper(ProductService productService) {
        PostInventoryDtoMapper.productService = productService;
    }

    public static Inventory postInventoryDtoToEntityMapper(PostInventoryDto inventoryDto) {

        Product product = productService.findProductById(inventoryDto.getProductId());

        return new Inventory().setProduct(product)
        .setExpires(inventoryDto.getExpires())
        .setBestBefore(inventoryDto.getBestBefore())
        .setManufactured(inventoryDto.getManufactured())
        .setQuantity(inventoryDto.getQuantity())
        .setPricePerItem(inventoryDto.getPricePerItem())
        .setSellBy(inventoryDto.getSellBy())
        .setTotalPrice(inventoryDto.getTotalPrice());
    }

}
