package com.test.atipera.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class WebClientResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorMessage> webClientResponseException(WebClientResponseException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getStatusCode().value(), exception.getMessage().substring(4));
        return new ResponseEntity<>(errorMessage, exception.getStatusCode());
    }
}
