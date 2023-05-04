package com.moyeou.moyeoubackend.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
public class UpdateRequest {
    @NotEmpty(message = "소개글을 입력해주세요")
    private String introduction;

    @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;

    private MultipartFile file;
}
