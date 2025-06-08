package com.blm.takeout.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    private static final String IMAGE_UPLOAD_DIR = "E:/takeout/uploads/";
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
        return "/uploads/" + fileName;
    }

    public static String convertImageToBase64(String imagePath) throws IOException {
        Path filePath = Paths.get(IMAGE_UPLOAD_DIR + imagePath.replace("/uploads", ""));
        byte[] imageBytes = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
