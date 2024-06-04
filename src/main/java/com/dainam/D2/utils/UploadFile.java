package com.dainam.D2.utils;


import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class UploadFile {
    public static String uploadFile(MultipartFile file) throws Exception {
        try {
            if(file.isEmpty()) {
                throw new Exception("Failed to store empty file");
            }

            // Create dir if not exists
            String uploadDir = "uploads/"+ LocalDateTime.now().getYear()+"/"+LocalDateTime.now().getMonthValue()+"/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // File name: Date Time + File Name
            String filename = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)+"_"+file.getOriginalFilename();

            // File link
            Path filePath = uploadPath.resolve(filename);

            // Copy to dir
            Files.copy(
                    file.getInputStream(),
                    filePath);

            // return file link as String
            return filePath.toString();
        } catch (Exception e) {
            throw new Exception("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!", e);
        }
    }

    public static void deleteFile(String avatar) {
        try {
            // Get file link
            Path path = Paths.get(avatar);
            // Delete file
            Files.delete(path);
        } catch (Exception e) {
            throw new RuntimeException("Could not delete file " + avatar
                    + ". Please try again!", e);
        }
    }
}

