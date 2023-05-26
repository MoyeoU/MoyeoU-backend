package com.moyeou.moyeoubackend.post.exception;

import com.moyeou.moyeoubackend.common.exception.ErrorCode;
import com.moyeou.moyeoubackend.common.exception.MoyeouException;

public class NonExistentItemException extends MoyeouException {
    public NonExistentItemException() {
        super(ErrorCode.NON_EXISTENT_ITEM);
    }
}
