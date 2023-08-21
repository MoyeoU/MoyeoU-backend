package com.moyeou.moyeoubackend.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@AllArgsConstructor
public class MemberUpdateRequest {
    private String introduction;

    @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;

    private MultipartFile file;

    private List<String> hashtags;
}
