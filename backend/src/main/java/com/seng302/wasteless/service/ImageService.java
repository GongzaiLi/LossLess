package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Image;
import com.seng302.wasteless.repository.ImageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

import java.nio.file.Paths;

/**
 * ImageService applies image logic over the Image JPA repository.
 */
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private static final Logger logger = LogManager.getLogger(ImageService.class.getName());

    //Constant for Thumbnail.
    private static final int TARGET_HEIGHT = 128;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Find image by id (database_id)
     * @param imageId  The id of the image to find
     * @return The found image, if any, otherwise null
     */
    public Image findImageById(Integer imageId) {
        return imageRepository.findFirstById(imageId);
    }

    /**
     * Creates a Image by saving the Image object and persisting it in the database
     *
     * @param image The Image object to be created.
     * @return The created Image object.
     */
    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    /**
     * Create unique image filename for the database by using UUID which crates unique alphanumeric value by hashing the time
     *
     * @param image image setting the file name and thumbnail file name
     * @param fileType image type
     * @return Image
     */
    public Image createImageFileName(Image image, String fileType) {
        UUID uuid = UUID.randomUUID();
        image.setFileName(String.format("media/images/%s.%s", uuid, fileType));
        image.setThumbnailFilename(String.format("media/images/%s_thumbnail.%s", uuid, fileType));
        return image;
    }

    /**     
     * saves the image into given path and creates directory if it doesnt exist already
     *
     * @param imagePath path in which file is to be saved to
     * @param image            image to be saved
     */
    public void storeImage(String imagePath, MultipartFile image) {
        File file = new File("./" + imagePath);
        try {
            if (!file.exists()) file.mkdirs();
            Files.copy(image.getInputStream(), file.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException error) {
            try {
                Files.deleteIfExists(file.getAbsoluteFile().toPath());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error with creating directory");
            }
            logger.debug("Failed to save image locally: {0}", error);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error with creating directory");
        }
    }

    /**
     * Resizes the original image to the thumbnail target height and the width is calculated from the ratio
     * of the target height and original height and
     * by using Graphics2D library
     * https://www.baeldung.com/java-resize-image#2-imagegetscaledinstance
     *
     * @param image The image to be resized
     * @throws IOException IOException
     * @return The resized version of the original image
     */
    public BufferedImage resizeImage(Image imageObj) {
        File image = new File("./" + imageObj.getFileName());
        try {
            BufferedImage originalImage = ImageIO.read(image);
            if (originalImage == null) {
                Files.deleteIfExists(image.getAbsoluteFile().toPath());
                logger.info("Cannot read Corrupt Image File");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The image file you tried to upload is corrupted.");
            }
            int height = TARGET_HEIGHT;
            int width = originalImage.getWidth() * TARGET_HEIGHT / originalImage.getHeight();
            BufferedImage resizedImage = new BufferedImage( width, height, originalImage.getType() == 0 ? 5 : originalImage.getType()); // This is a hack to fix a known bug in Java. See https://github.com/usnistgov/pyramidio/issues/7
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(originalImage, 0, 0, width, height, null);
            graphics2D.dispose();
            return resizedImage;

        } catch (IOException error) {
            logger.debug("Corrupt Image File", error);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The image file you tried to upload is corrupted.");
        }
    }


    /**
     * delete image from database
     * @param image the image to delete
     */
    public void deleteImageRecordFromDB (Image image) {
        this.imageRepository.delete(image);
    }

    /**
     * delete image file and thumbnail from serve
     * @param image the image to delete
     */
    public void deleteImageFile(Image image) {
        try {
            logger.info("Deleting: {}", image.getFileName());
            Files.delete(Paths.get("./" + image.getFileName()));
        } catch (IOException error) {
            logger.debug("Failed to delete image locally: {0}", error);
        }
        try {
            logger.info("Deleting: {}", image.getThumbnailFilename());
            Files.delete(Paths.get("./" + image.getThumbnailFilename()));
        } catch (IOException error) {
            logger.debug("Failed to delete thumbnail image locally: {0}", error);
        }

    }

    /**
     * Saves the thumbnail image into given path.
     *
     * @param imagePath The file path that is used to save the image
     * @param imageType        The image type
     * @param image            The image to be saved
     */
    public void storeThumbnailImage(String imagePath, String imageType, BufferedImage image) {
        File file = new File("./" + imagePath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            ImageIO.write(image, imageType, out);
            out.close();
        } catch (IOException error) {
            try {
                Files.deleteIfExists(file.getAbsoluteFile().toPath());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save thumbnail image");
            }
            logger.debug("Failed to save thumbnail image locally: {0}", error);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save thumbnail image");
        }
    }

    /**
     * Validates the image file, and returns the type of the image.
     *
     * Image validated for content and type.
     *
     * @param file  The image file to validate
     * @return      The file type of the image
     * @throws ResponseStatusException  If the image is invalid
     */
    private String validateImageFileAndReturnImageType(MultipartFile file) throws ResponseStatusException {
        String imageType;

        if (file.isEmpty()) {
            logger.warn("Cannot post user image, no image received");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Image Received");
        }

        String fileContentType = file.getContentType();
        if (fileContentType != null && fileContentType.contains("/")) {
            imageType = fileContentType.split("/")[1];
        } else {
            logger.debug("Error with image type is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error with image type is null");
        }

        if (!Arrays.asList("png", "jpeg", "jpg", "gif").contains(imageType)) {
            logger.warn("Cannot post product image, invalid image type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Image type");
        }

        return imageType;
    }


    /**
     * Save an image and create and save a thumbnail.
     * Validates image using helper function
     *
     * @param file  The image file to be saved
     * @throws ResponseStatusException If the image is invalid
     * @return Image entity containing image information
     */
    public Image saveImageWithThumbnail(MultipartFile file) throws ResponseStatusException {
        String imageType = validateImageFileAndReturnImageType(file);

        Image newImage = new Image();
        newImage = createImageFileName(newImage, imageType);

        storeImage(newImage.getFileName(), file);

        BufferedImage thumbnail = resizeImage(newImage);
        if (thumbnail == null) {
            logger.debug("Error resizing image");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resizing file");
        }

        storeThumbnailImage(newImage.getThumbnailFilename(), imageType, thumbnail);
        newImage = createImage(newImage);
        logger.debug("Created new image entity with filename {}", newImage.getFileName());

        return newImage;
    }

}
