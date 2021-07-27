package com.seng302.wasteless.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class provides the exception handler for MaxUploadSizeExceededException Exceptions.
 */
@ControllerAdvice
public class FileUploadSizeExceptionAdvice {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FileUploadSizeExceptionAdvice.class.getName());

    /**
     * Handles MaxUploadSizeExceededException Exceptions by sending back a 419 PAYLOAD_TOO_LARGE response with a message
     * stating the maximum upload size.
     * See https://www.baeldung.com/spring-maxuploadsizeexceeded for more details
     * @param exc The MaxUploadSizeExceededException exception thrown.
     * @param response  The HttpServletResponse that will be modified and sent back to the client.
     * @throws IOException If writing to the response fails. This should never happen and would indicate a serious error with Spring Boot
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public void handleMaxSizeException(
            MaxUploadSizeExceededException exc,
            HttpServletResponse response) throws IOException
    {
        logger.warn("Tried to upload file that was too large: {}", exc.getLocalizedMessage());
        response.setStatus(HttpStatus.PAYLOAD_TOO_LARGE.value());
        response.getWriter().write("The file that you tried to upload is too large. Files must be less than 1MB in size.");
    }
}
