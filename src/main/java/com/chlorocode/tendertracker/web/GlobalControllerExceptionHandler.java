package com.chlorocode.tendertracker.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class is used to handle the exception of whole application.
 */
@ControllerAdvice
class GlobalControllerExceptionHandler {
    /**
     * This method is used to control the exception as default.
     * @param req HttpServletRequest
     * @param e Exception
     * @return String
     * @throws Exception Exception
     */
    @ExceptionHandler(value = Exception.class)
    public String defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        return "error/error";
    }
}
