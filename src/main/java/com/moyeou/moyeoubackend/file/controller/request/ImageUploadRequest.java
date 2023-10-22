package com.moyeou.moyeoubackend.file.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class ImageUploadRequest {
    @NotNull(message = "이미지를 포함해주세요.")
    private MultipartFile image;
}
