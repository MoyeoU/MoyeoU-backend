package com.moyeou.moyeoubackend.common.exception;

import static com.moyeou.moyeoubackend.common.exception.ErrorCode.UNAUTHORIZED_ACCESS;

public class UnAuthorizedException extends MoyeouException {
    public UnAuthorizedException() {
        super(UNAUTHORIZED_ACCESS);
    }

    public UnAuthorizedException(String message) {
        super(UNAUTHORIZED_ACCESS.getCode(), message, UNAUTHORIZED_ACCESS.getStatus());
    }
}
