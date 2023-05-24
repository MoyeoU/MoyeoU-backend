package com.moyeou.moyeoubackend.member.exception;

import com.moyeou.moyeoubackend.common.exception.MoyeouException;

import static com.moyeou.moyeoubackend.common.exception.ErrorCode.DUPLICATE_MEMBER;

public class DuplicateMemberException extends MoyeouException {
    public DuplicateMemberException() {
        super(DUPLICATE_MEMBER);
    }
}
