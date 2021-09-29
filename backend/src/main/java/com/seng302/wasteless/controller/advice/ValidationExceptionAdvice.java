package com.seng302.wasteless.controller.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides the exception handlers for validation exceptions. Specifically, it handles MethodArgumentNotValidExceptions
 * and ConstraintViolationExceptions. See the docstrings of the methods for more info.
 */
@ControllerAdvice
public class ValidationExceptionAdvice {
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Exception handler for MethodArgumentNotValidExceptions. MethodArgumentNotValidExceptions are usually thrown when
     * a supplied request body (e.g. DTO) does not meet the constraints defined for it.
     * This handler will cause a 400 BAD REQUEST response to be returned to the client.
     * Additionally, a message will be written to the response. This will be a stringified JSON object where the keys
     * are the names of each invalid field, and the values are the relevant validation messages.
     * For example: {"name": "must not be empty", "birthday": "must be in the past"}
     * @param exception The exception that was thrown and must be handled
     * @param response HTTP response to be sent back to the client. Will be modified in the manner above
     * @throws IOException If writing to the response fails. This should never happen and would indicate a serious error with Spring Boot
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationExceptions(MethodArgumentNotValidException exception, HttpServletResponse response) throws IOException {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        response.getWriter().write(objectMapper.writeValueAsString(errors));
    }

    /**
     * Exception handler for ConstraintViolationExceptions. ConstraintViolationExceptions are usually thrown when
     * an JPA object that violates some constraint is attempted to be saved in the DB.
     * This handler will cause a 400 BAD REQUEST response to be returned to the client.
     * Additionally, a message will be written to the response. This will be a stringified JSON object where the keys
     * are the names of each invalid field, and the values are the relevant constraint messages.
     * For example: {"expiry": "must be in the future"}
     * @param exception The exception that was thrown and must be handled
     * @param response HTTP response to be sent back to the client. Will be modified in the manner above
     * @throws IOException If writing to the response fails. This should never happen and would indicate a serious error with Spring Boot
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public void handleValidationExceptions(
            ConstraintViolationException exception, HttpServletResponse response) throws IOException {

        Map<String, String> errors = new HashMap<>();

        exception.getConstraintViolations().forEach(error ->
            errors.put(error.getPropertyPath().toString(), error.getMessage())
        );

        response.getWriter().write(objectMapper.writeValueAsString(errors));
    }
}
