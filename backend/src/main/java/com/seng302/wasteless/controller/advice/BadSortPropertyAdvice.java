package com.seng302.wasteless.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class provides the exception handler for PropertyReferenceException Exceptions.
 * These exceptions are thrown when a bad sort property is passed into a query that takes a
 * Pageable object.
 * See https://stackoverflow.com/questions/41944267/proper-handling-of-propertyreferenceexception-for-query-params
 */
@ControllerAdvice
public class BadSortPropertyAdvice {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(BadSortPropertyAdvice.class.getName());

    /**
     * Exception handler method for PropertyReferenceException Exceptions.
     * Modifies the response into a BAD REQUEST response with an error message as the body.
     * See https://stackoverflow.com/questions/41944267/proper-handling-of-propertyreferenceexception-for-query-params
     *
     * @param exc      The PropertyReferenceException exception thrown.
     * @param response The HttpServletResponse that will be modified and sent back to the client.
     * @throws IOException If writing to the response fails. This should never happen and would indicate a serious error with Spring Boot
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public void handleMaxSizeException(
            PropertyReferenceException exc,
            HttpServletResponse response) throws IOException {
        logger.warn("Bad property reference: {}", exc.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().format("The property '%s' is not a valid sort property", exc.getPropertyName());
    }
}
