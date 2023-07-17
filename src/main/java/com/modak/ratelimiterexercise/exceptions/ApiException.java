package com.modak.ratelimiterexercise.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private String code;
    private String msg;
    private HttpStatus statusCode;

    public ApiException(HttpStatus status, String code, String msg) {
        super(msg);
        this.statusCode = status;
        this.code = code;
        this.msg = msg;
    }
}
