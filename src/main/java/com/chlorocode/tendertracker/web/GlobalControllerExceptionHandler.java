package com.chlorocode.tendertracker.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

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
