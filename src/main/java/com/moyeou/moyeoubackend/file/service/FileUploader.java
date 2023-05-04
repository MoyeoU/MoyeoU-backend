package com.moyeou.moyeoubackend.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    String upload(MultipartFile file);
}
