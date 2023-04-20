package com.moyeou.moyeoubackend.auth.exception;

import com.moyeou.moyeoubackend.common.exception.MoyeouException;

import static com.moyeou.moyeoubackend.common.exception.ErrorCode.INCORRECT_PASSWORD;

public class IncorrectPasswordException extends MoyeouException {
    public IncorrectPasswordException() {
        super(INCORRECT_PASSWORD);
    }
}
