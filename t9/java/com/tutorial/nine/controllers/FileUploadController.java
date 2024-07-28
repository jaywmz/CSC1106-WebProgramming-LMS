package com.tutorial.nine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.List; // Add this import statement
import java.util.Arrays; // Add this import statement
import java.io.File; // Add this import statement
import java.util.Collections; // Add this import statement

@RestController
public class FileUploadController {
    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if(file.isEmpty()) {
                return ResponseEntity.badRequest().body("Empty File");
            }
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadPath + file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading file");
        }
        return ResponseEntity.ok("File uploaded successfully " + file.getOriginalFilename());
    }

    @GetMapping("/images")
    public ResponseEntity<List<String>> listUploadedFiles() {
    try {
        File uploadDir = new File(uploadPath);
        List<String> fileNames = Arrays.stream(uploadDir.listFiles())
                                       .filter(File::isFile)
                                       .map(File::getName)
                                       .collect(Collectors.toList());
        return ResponseEntity.ok(fileNames);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Collections.emptyList());
    }
}

    // Delete the file according to the file name received
    @DeleteMapping("/images/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        try {
            File file = new File(uploadPath + fileName);
            if(file.delete()) {
                return ResponseEntity.ok("File deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Error deleting file");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting file");
        }
    }
}
