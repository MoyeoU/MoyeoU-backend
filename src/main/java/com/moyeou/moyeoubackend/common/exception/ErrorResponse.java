package com.moyeou.moyeoubackend.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@JsonInclude(NON_NULL)
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
