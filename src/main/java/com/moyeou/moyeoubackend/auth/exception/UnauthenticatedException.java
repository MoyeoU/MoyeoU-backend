package com.moyeou.moyeoubackend.auth.exception;

import com.moyeou.moyeoubackend.common.exception.MoyeouException;

import static com.moyeou.moyeoubackend.common.exception.ErrorCode.UNAUTHENTICATED;

public class UnauthenticatedException extends MoyeouException {
    public UnauthenticatedException() {
        super(UNAUTHENTICATED);
    }

    public UnauthenticatedException(String message) {
        super(UNAUTHENTICATED.getCode(), message, UNAUTHENTICATED.getStatus());
    }
}
