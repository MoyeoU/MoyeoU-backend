package com.moyeou.moyeoubackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MoyeouException extends RuntimeException {
    private final String code;
    private final String message;
    private final HttpStatus status;

    public MoyeouException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage(), errorCode.getStatus());
    }

    public MoyeouException(ErrorCode errorCode, String message) {
        this(errorCode.getCode(), message, errorCode.getStatus());
    }

    public MoyeouException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.message = message;
        this.status = errorCode.getStatus();
    }

    public MoyeouException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
