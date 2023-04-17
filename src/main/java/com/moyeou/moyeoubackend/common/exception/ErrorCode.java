package com.moyeou.moyeoubackend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST_PARAMS("4000", "요청 파라미터가 잘못되었습니다.", HttpStatus.BAD_REQUEST),

    DUPLICATE_MEMBER("4001", "이미 가입한 회원입니다", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
