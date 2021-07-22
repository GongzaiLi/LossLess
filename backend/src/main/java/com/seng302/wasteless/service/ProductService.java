package com.seng302.wasteless.service;


import com.seng302.wasteless.model.GetProductSortTypes;
import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * Get the count of products of a business
     *
     * @param id   The id of the business to get the prouct count of
     * @return     The product count
     */
    public Integer getTotalProductsCountByBusinessId(Integer id) {
        return productRepository.countProductByBusinessId(id);
    }

    /**
     * Get count number of products for a chosen business. Starting at offset.
     * Sorted by sortBy and sortDirection
     *
     * @param id The id of a business
     * @return A list of business's products, if any, otherwise empty list
     */
    public List<Product> getCountProductsByBusinessIdFromOffset(Integer id, Integer offset, Integer count, GetProductSortTypes sortBy, String sortDirection) {

        Pageable pageable = PageRequest.of(
                offset,
                count,
                sortDirection.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.toString()
        );

        return  productRepository.findAllByBusinessId(id, pageable);
    }


    /**
     * Add image to a product
     * Calling the method in this way allows for mocking during automated testing
     * @param product Product that image is to be added to
     * @param productImage image that is to be added to product
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

    /**
     * Remove an image from a product
     * Calling the method in this way allows for mocking during automated testing
     *  @param product Product that image is to be removed from
     *  @param productImage image that is to be removed from product
     */
    public void deleteImageRecordFromProductInDB (Product product, ProductImage productImage) {
        product.removeImage(productImage);
    }

    /**
     * update the primary image for product to first image list or null for empty
     * @param product Product that image is being removed from
     * @param productImage image that is being removed from product
     */
    public void updatePrimaryImage(Product product, ProductImage productImage) {
        if (product.getPrimaryImage().getId().equals(productImage.getId())){
            if (product.getImages().isEmpty()) {
                product.setPrimaryImage(null);
            } else {
                product.setPrimaryImage(product.getImages().get(0));
            }

        }
    }



}

