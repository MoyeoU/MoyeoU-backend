package com.moyeou.moyeoubackend.file.exception;

import com.moyeou.moyeoubackend.common.exception.MoyeouException;

import static com.moyeou.moyeoubackend.common.exception.ErrorCode.STORAGE_EXCEPTION;

public class StorageException extends MoyeouException {

    public StorageException(String message) {
        super(STORAGE_EXCEPTION, message);
    }

    public StorageException(String message, Throwable cause) {
        super(STORAGE_EXCEPTION, message, cause);
    }
}
