package com.hieu.apigateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GatewayErrorCode {

    TOKEN_INVALID("AUTH_001", "Token invalid or expired", HttpStatus.UNAUTHORIZED),
    AUTH_REQUIRED("AUTH_002", "Authentication required", HttpStatus.UNAUTHORIZED),
    ROUTE_NOT_FOUND("GW_404", "Route not found", HttpStatus.NOT_FOUND),
    RATE_LIMIT_EXCEEDED("GW_429", "Too many requests. Please slow down", HttpStatus.TOO_MANY_REQUESTS),
    SERVICE_UNAVAILABLE("GW_502", "Service temporarily unavailable", HttpStatus.BAD_GATEWAY),
    UPSTREAM_CONNECTION_REFUSED("GW_502", "Upstream connection refused", HttpStatus.BAD_GATEWAY),
    UNEXPECTED_ERROR("GW_500", "Unexpected error. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final String PREFIX = "GW_";

    private final String code;
    private final String message;
    private final HttpStatus status;

    GatewayErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public static GatewayErrorCode fromHttpStatus(int statusCode) {
        for (GatewayErrorCode errorCode : values()) {
            // Chỉ map cho những mã thuộc dòng GW_ (ngoại trừ UNEXPECTED_ERROR/UPSTREAM_CONNECTION_REFUSED
            // vì có thể có nhiều nguyên nhân, ta dùng mã HTTP status để tìm mapping mặc định)
            if (errorCode.getStatus().value() == statusCode && errorCode.getCode().startsWith(PREFIX) 
                && errorCode != UPSTREAM_CONNECTION_REFUSED && errorCode != UNEXPECTED_ERROR) {
                return errorCode;
            }
        }
        return null;
    }
}
