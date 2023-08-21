package com.moyeou.moyeoubackend.file.service;

import com.moyeou.moyeoubackend.file.exception.StorageException;
import com.moyeou.moyeoubackend.file.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class FileSystemStorageService implements FileUploader {
    private static final String FILE_UPLOAD_PATH = "http://localhost:8080/files/";

    private final Path rootLocation;

    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            log.warn("디렉토리가 이미 존재합니다.", e);
        }
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return null;
            }
            String originalFilename = file.getOriginalFilename();
            String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID() + extension;

            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            return FILE_UPLOAD_PATH + filename;
        } catch (IOException e) {
            throw new StorageException("파일 저장에 실패했습니다: " + file.getOriginalFilename(), e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("파일이 존재하지 않거나 읽을 수 없습니다: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("파일을 읽을 수 없습니다: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
