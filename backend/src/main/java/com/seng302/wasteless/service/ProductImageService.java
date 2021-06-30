package com.seng302.wasteless.service;

import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ProductImageService applies product image logic over the ProductImage JPA repository.
 */
@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Autowired
    public ProductImageService(ProductImageRepository productImageRepository) { this.productImageRepository = productImageRepository; }

    /**
     * Creates a ProductImage by saving the productImage object and persisting it in the database
     * @param productImage The ProductImage object to be created.
     * @return The created ProductImage object.
     */
    public ProductImage createProductImage(ProductImage productImage) {return productImageRepository.save(productImage); }
}
