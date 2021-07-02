package com.seng302.wasteless.controller;

import com.seng302.wasteless.dto.PostInventoryDto;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.service.*;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class ImageController {

    private static final Logger logger = LogManager.getLogger(ImageController.class.getName());


    private final BusinessService businessService;
    private final ProductImageService productImageService;
    private final ProductService productService;

    @Autowired
    public ImageController(BusinessService businessService, ProductService productService, ProductImageService productImageService) {
        this.businessService = businessService;
        this.productService = productService;
        this.productImageService = productImageService;
    }

    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    public ResponseEntity<Object> postProductImage(@PathVariable("businessId") Integer businessId, @PathVariable("productId") String productId, @RequestParam("filename") MultipartFile file) {

        logger.debug("Request to Create product: {} for business ID: {}", productId, businessId);
        logger.debug("file: {}", file);

        ProductImage newImage = new ProductImage();
        newImage.setFileName("test.png");
        newImage.setThumbnailFilename("test.png");
        newImage = productImageService.createProductImage(newImage);
        Product product = productService.findProductById(productId);
        productService.addImageToProduct(product, newImage.getId());
        productService.updateProduct(product);

//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/downloadFile/")
//                .path(newImage.getFileName())
//                .toUriString();

        JSONObject responseBody = new JSONObject();
        responseBody.put("imageId", newImage.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);

    }


}
