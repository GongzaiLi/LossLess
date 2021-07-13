package com.seng302.wasteless.service;


import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Product service applies product logic over the Product JPA repository.
 */
@Service
public class ProductService {

    private static final Logger logger = LogManager.getLogger(ProductService.class.getName());

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    /**
     * Check if the product id in use if so throw a response status exception
     * @param id The id of the product to find
     *
     */
    public Boolean checkIfProductIdNotInUse(String id) {
        Product product = productRepository.findFirstById(id);
        if (product != null) {
            logger.warn("Product ID already exists");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID provided already exists.");
        }
        logger.info("Product ID: {} generated", id);
        return true;
    }

    /**
     * Find product by product id (code)
     *
     * @param id        The id of the product to find
     * @return          The found product, if any, otherwise null
     */
    public Product findProductById(String id) {
        Product product = productRepository.findFirstById(id);
        if (product == null) {
            logger.warn("Product with id: {} Not Found", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with given id does not exist");
        }
        logger.info("Product with ID: {} Found", id);
        return product;
    }

    /**
     * Create a new product in Database
     *
     * @param product       The product object to create
     * @return              The created product
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Saves the given product object with updated fields in Database
     *
     * @param newProduct    The new product object to save in the DB
     */
    public void updateProduct(Product newProduct) {
        productRepository.save(newProduct);
    }

    /**
     * Get all the products for a chosen business
     *
     * @param id The id of a business
     * @return A list of business's products, if any, otherwise empty list
     */
    public List<Product> getAllProductsByBusinessId(Integer id) { return  productRepository.findAllByBusinessId(id); }


    /**
     * Add image to a product
     * Calling the method in this way allows for mocking during automated testing
     * @param product product image to be added to
     * @param productImage the product image to be added
     */
    public void addImageToProduct(Product product, ProductImage productImage) {
        product.addImage(productImage);
    }

    /**
     * Check if product belongs to the business
     * @param product product to check
     * @param businessId id of the business the product should belong to
     */
    public void checkProductBelongsToBusiness(Product product, Integer businessId) {
        if (!product.getBusinessId().equals(businessId)) {
            logger.warn("Request Failed, product does not belong to current business");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id does not exist for Current Business");
        }
    }

}

