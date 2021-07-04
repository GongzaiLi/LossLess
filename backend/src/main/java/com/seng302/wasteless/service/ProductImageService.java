package com.seng302.wasteless.service;

import com.seng302.wasteless.controller.InventoryController;
import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.repository.ProductImageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ProductImageService applies product image logic over the ProductImage JPA repository.
 */
@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private static final Logger logger = LogManager.getLogger(InventoryController.class.getName());

    @Autowired
    public ProductImageService(ProductImageRepository productImageRepository) { this.productImageRepository = productImageRepository; }

    /**
     * Creates a ProductImage by saving the productImage object and persisting it in the database
     * @param productImage The ProductImage object to be created.
     * @return The created ProductImage object.
     */
    public ProductImage createProductImage(ProductImage productImage) {return productImageRepository.save(productImage); }

    /**
     * Find product by product id (code)
     *
     * @param id        The id of the image to find
     * @return          The found product image, if any, otherwise null
     */
    public ProductImage findImageById(Integer id) { return productImageRepository.findFirstById(id); }

    public void deleteImage(ProductImage productImage) { this.productImageRepository.delete(productImage);}

//    public void deleteImageFile(ProductImage productImage) {
//
//        String filename = productImage.getFileName();
//        String thumbnailFilename = productImage.getThumbnailFilename();
//        //System.out.println(getClass().getClassLoader().getResource("./media/images/test.png").getPath());
//        System.out.println(Files.exists(Paths.get("./media/images/test.png")));
////        System.out.println(getClass().getClassLoader().getResource("./media/images/test.png").getPath());
////        System.out.println(getClass().getClassLoader().getResource(thumbnailFilename).getPath());
//        try {
//            Files.delete(Paths.get(getClass().getClassLoader().getResource("test.png").getPath()));
//        } catch (IOException error) {
//            logger.debug("File: {} does not exist", filename);
//        }
//        try {
//            Files.delete(Paths.get(thumbnailFilename));
//        } catch (IOException error) {
//            logger.debug("File: {} does not exist", thumbnailFilename);
//        }
//    }
}
