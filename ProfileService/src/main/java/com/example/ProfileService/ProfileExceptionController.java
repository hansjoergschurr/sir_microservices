package com.example.ProfileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ProfileExceptionController {

    private final Logger logger = LoggerFactory.getLogger(ProfileExceptionController.class);

    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String profileNotFoundHandler(ProfileNotFoundException ex) {
        logger.trace(ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(EmailInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String emailInUseHandler(EmailInUseException ex) {
        logger.trace(ex.getMessage());
        return ex.getMessage();
    }

    @ResponseBody // not necessary with @RestControllerAdvice
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(
           MethodArgumentNotValidException ex ) {
        logger.trace("Object not valid.");
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        return errors;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        logger.trace("JSON has syntax errors.");
        return ex.getMessage();
    }
}
