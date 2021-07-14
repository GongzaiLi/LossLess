package com.seng302.wasteless.service;


import com.seng302.wasteless.model.Product;
import com.seng302.wasteless.model.ProductImage;
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
     * Find product by product id (code)
     *
     * @param id        The id of the product to find
     * @return          The found product, if any, otherwise null
     */
    public Product findProductById(String id) {
        return productRepository.findFirstById(id);
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
     * @param product Product that image is to be added to
     * @param productImage image that is to be added to product
     */
    public void addImageToProduct(Product product, ProductImage productImage) {
        product.addImage(productImage);
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

