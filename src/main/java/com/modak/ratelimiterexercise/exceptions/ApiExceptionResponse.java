package com.modak.ratelimiterexercise.exceptions;

import lombok.Getter;

@Getter
public class ApiExceptionResponse {
    private String code;
    private String msg;
    private Integer statusCode;

    public ApiExceptionResponse(ApiException apiException) {
        this.code = apiException.getCode();
        this.msg = apiException.getMsg();
        this.statusCode = apiException.getStatusCode().value();
    }

}
