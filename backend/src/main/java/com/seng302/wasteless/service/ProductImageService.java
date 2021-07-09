package com.seng302.wasteless.service;

import com.seng302.wasteless.model.ProductImage;
import com.seng302.wasteless.repository.ProductImageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
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
    private static final Logger logger = LogManager.getLogger(ProductImageService.class.getName());

    // Constants for the thumbnail image size
    private static final int TARGET_WIDTH = 128;
    private static final int TARGET_HEIGHT = 128;

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
        } catch (IOException error) {
            logger.debug("Failed to save image locally: {0}", error);
            return false;
        }
    }

    /**
     * Resizes the original image to the thumbnail target width and target height
     * by using Graphics2D library
     * https://www.baeldung.com/java-resize-image#2-imagegetscaledinstance
     * @param productImage The Product image to be resized
     * @returnconstant The resized version of the original image
     * @throws IOException
     */
    public BufferedImage resizeImage(ProductImage productImage) {
        File image = new File(".." + productImage.getFileName());
        BufferedImage originalImage = null;

        try {
            originalImage = ImageIO.read(image);
        } catch (IOException error) {
            logger.debug("", error);
        }

        BufferedImage resizedImage = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, TARGET_WIDTH, TARGET_HEIGHT, null);
        graphics2D.dispose();
        return resizedImage;
    }

    /**
     * Saves the thumbnail image into given path.
     * @param productImagePath The file path that is used to save the image
     * @param imageType The image type
     * @param image The image to be saved
     * @return
     */
    public Boolean storeThumbnailImage(String productImagePath, String imageType, BufferedImage image) {
        File file = new File(".." + productImagePath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            ImageIO.write(image, imageType, out);
            out.close();
            return true;
        } catch (IOException error) {
            logger.debug("Failed to save thumbnail image locally: {0}", error);
            return false;
        }
    }

}
