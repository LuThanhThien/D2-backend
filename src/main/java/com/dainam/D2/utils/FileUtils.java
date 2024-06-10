package com.dainam.D2.utils;


import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileUtils {
    static public final String UPLOAD_FOLDER = "storage/";

    public static String upload(MultipartFile file) throws Exception {
        try {
            if(file.isEmpty()) {
                throw new Exception("Failed to upload empty file.");
            }

            // Create dir if not exists
            String uploadDir = UPLOAD_FOLDER + LocalDateTime.now().getYear()+"/"+LocalDateTime.now().getMonthValue()+"/";
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
            throw new Exception("Could not store file " + file.getOriginalFilename() + ". Please try again!", e);
        }
    }

    public static void delete(String file) {
        try {
            // Get file link
            Path path = Paths.get(file);
            // Delete file
            Files.delete(path);
        } catch (Exception e) {
            throw new RuntimeException("Could not delete file " + file + ". Please try again!", e);
        }
    }

    public static boolean fileExist(String file) {
        File f = new File(file);
        return f.exists() && !f.isDirectory();
    }

}

