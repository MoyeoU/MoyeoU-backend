package com.moyeou.moyeoubackend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST_PARAMS("4000", "요청 파라미터가 잘못되었습니다.", HttpStatus.BAD_REQUEST),

    DUPLICATE_MEMBER("4001", "이미 가입한 회원입니다.", HttpStatus.BAD_REQUEST),

    NON_EXISTENT_EMAIL("4002", "가입된 회원이 아닙니다.", HttpStatus.BAD_REQUEST),

    INCORRECT_PASSWORD("4003", "비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED_ACCESS("4004", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    UNAUTHENTICATED("4005", "인증되지 않음", HttpStatus.UNAUTHORIZED),

    ENTITY_NOT_FOUND("4006", "엔티티를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    ILLEGAL_STATE("4007", "IllegalStateException", HttpStatus.BAD_REQUEST),

    STORAGE_EXCEPTION("5000", "저장소 예외", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
