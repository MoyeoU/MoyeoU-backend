package com.moyeou.moyeoubackend.file.controller;

import com.moyeou.moyeoubackend.file.controller.request.ImageUploadRequest;
import com.moyeou.moyeoubackend.file.service.FileSystemStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 관련 API")
public class FileController {
    private final FileSystemStorageService fileSystemStorageService;

    @Operation(summary = "파일 조회")
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileSystemStorageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(file);
    }

    @PostMapping("/images")
    public String saveImage(@Valid ImageUploadRequest request) {
        return fileSystemStorageService.upload(request.getImage());
    }
}
