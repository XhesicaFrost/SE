package com.blm.takeout.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtils {
    private static final String UPLOAD_DIR = "uploads/images/";

    public static String saveImage(MultipartFile file) throws IOException {
        // 创建上传目录
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(UPLOAD_DIR + newFilename);
        Files.copy(file.getInputStream(), filePath);

        // 返回文件路径
        return "/images/" + newFilename;
    }
} 