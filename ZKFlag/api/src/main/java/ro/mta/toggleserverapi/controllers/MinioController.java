package ro.mta.toggleserverapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.mta.toggleserverapi.services.MinioService;

@RestController
@RequestMapping("/minio")
@Slf4j
public class MinioController {

    private final MinioService minioService;

    @Autowired
    public MinioController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Uploading file: {}", file.getOriginalFilename());
        try {
            String fileUrl = minioService.uploadFile(file);
            log.info("File uploaded successfully to URL: {}", fileUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileUrl);
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String fileUrl) {
        log.info("Deleting file with URL: {}", fileUrl);
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            minioService.deleteFile(fileName);
            log.info("File deleted successfully: {}", fileName);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file: " + e.getMessage());
        }
    }
}


