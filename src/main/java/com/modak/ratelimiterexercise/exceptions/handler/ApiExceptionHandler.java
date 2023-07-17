package com.modak.ratelimiterexercise.exceptions.handler;

import com.modak.ratelimiterexercise.exceptions.ApiException;
import com.modak.ratelimiterexercise.exceptions.ApiExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<Object> exception(ApiException exception) {
        ApiExceptionResponse response = new ApiExceptionResponse(exception);
        return new ResponseEntity<>(response, exception.getStatusCode());
    }

}
