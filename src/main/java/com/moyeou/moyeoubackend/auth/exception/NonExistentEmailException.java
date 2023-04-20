package com.moyeou.moyeoubackend.auth.exception;

import com.moyeou.moyeoubackend.common.exception.MoyeouException;

import static com.moyeou.moyeoubackend.common.exception.ErrorCode.NON_EXISTENT_EMAIL;

public class NonExistentEmailException extends MoyeouException {
    public NonExistentEmailException() {
        super(NON_EXISTENT_EMAIL);
    }
}
