package com.seng302.wasteless.service;

import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

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

    /**
     * Create unique image filename for the database by using UUID which crates unique alphanumeric value by hashing the time
     * @param productImage
     * @param fileType
     * @return productImage
     */
    public ProductImage createImageFileName(ProductImage productImage, String fileType) {
        UUID uuid = UUID.randomUUID();
        productImage.setFileName(String.format("/media/images/%s.%s", uuid, fileType));
        productImage.setThumbnailFilename(String.format("/media/images/%s_thumbnail.%s", uuid, fileType));
        return productImage;
    }

    /**
     * saves the image into given path and creates directory if it doesnt exist already
     * @param productImagePath path in which file is to be saved to
     * @param image image to be saved
     * @return boolean
     */
    public Boolean storeImage(String productImagePath, MultipartFile image) {
        File file = new File(".." + productImagePath);
        try {
            file.mkdirs();
            Files.copy(image.getInputStream(), file.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }

    }
}
