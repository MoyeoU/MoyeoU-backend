package com.moyeou.moyeoubackend.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> errors;

    public ErrorResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }

    public ErrorResponse(ErrorCode errorCode, Map<String, String> errors) {
        this(errorCode.getCode(), errorCode.getMessage(), errors);
    }

    public ErrorResponse(String code, String message) {
        this(code, message, null);
    }

    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
}
