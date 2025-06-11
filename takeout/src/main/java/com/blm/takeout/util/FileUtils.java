package com.blm.takeout.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    private static final String IMAGE_UPLOAD_DIR = "uploads/";
    public static String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IOException("图片不能为空");
        }
        Path uploadPath = Paths.get(IMAGE_UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath);
        return "/" + IMAGE_UPLOAD_DIR + fileName;
    }

    public static String convertImageToBase64(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            return "";
        }
        
        // 从 /uploads/xxx.jpg 转换为 uploads/xxx.jpg
        String relativePath = imagePath.replace("/uploads/", "");
        Path filePath = Paths.get(IMAGE_UPLOAD_DIR, relativePath);
        
        if (!Files.exists(filePath)) {
            System.err.println("图片文件不存在: " + filePath);
            return "";
        }
        
        byte[] fileContent = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
