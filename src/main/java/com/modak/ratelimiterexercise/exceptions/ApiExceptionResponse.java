package com.modak.ratelimiterexercise.exceptions;

import org.springframework.http.HttpStatus;

public class ApiExceptionResponse {

    private String code;
    private String msg;
    private Integer statusCode;

    public ApiExceptionResponse(String code, String msg, Integer statusCode) {
        this.code = code;
        this.msg = msg;
        this.statusCode = statusCode;
    }

    public ApiExceptionResponse(ApiException apiException) {
        this.code = apiException.getCode();
        this.msg = apiException.getMsg();
        this.statusCode = apiException.getStatusCode().value();
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
