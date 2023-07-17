package com.modak.ratelimiterexercise.exceptions;

import org.springframework.http.HttpStatus;

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

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
