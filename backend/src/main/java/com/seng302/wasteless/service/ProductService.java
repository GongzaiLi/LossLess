package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.repository.BusinessRepository;
import com.seng302.wasteless.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Product service applies product logic over the Product JPA repository.
 */

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Create a new product in Database
     *
     * @param product       The product object to create
     * @return              The created business
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }


}

