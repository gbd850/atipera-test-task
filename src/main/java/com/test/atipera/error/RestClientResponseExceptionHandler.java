package com.test.atipera.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestClientResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorMessage> restClientResponseException(RestClientResponseException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getStatusCode().value(), exception.getMessage());
        return new ResponseEntity<>(errorMessage, exception.getStatusCode());
    }
}
