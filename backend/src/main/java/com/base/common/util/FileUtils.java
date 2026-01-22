package com.base.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @author base
 * @since 2026-01-13
 */
public class FileUtils {

    /**
     * 获取文件扩展名
     */
    public static String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * 生成唯一文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + extension;
    }

    /**
     * 根据日期生成文件路径
     * 格式：yyyy/MM/dd
     */
    public static String generateDatePath() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return now.format(formatter);
    }

    /**
     * 创建目录
     */
    public static void createDirectory(String path) throws IOException {
        Path directory = Paths.get(path);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }

    /**
     * 保存文件
     */
    public static String saveFile(MultipartFile file, String uploadPath) throws IOException {
        // 生成日期路径
        String datePath = generateDatePath();
        String fullPath = uploadPath + File.separator + datePath;

        // 创建目录
        createDirectory(fullPath);

        // 生成唯一文件名
        String filename = generateUniqueFilename(file.getOriginalFilename());
        String filePath = fullPath + File.separator + filename;

        // 保存文件
        file.transferTo(new File(filePath));

        // 返回相对路径
        return datePath + "/" + filename;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * 获取文件大小（格式化）
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 验证文件类型
     */
    public static boolean isAllowedType(String contentType, String[] allowedTypes) {
        if (contentType == null || allowedTypes == null) {
            return false;
        }
        for (String allowedType : allowedTypes) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }
        return false;
    }
}
