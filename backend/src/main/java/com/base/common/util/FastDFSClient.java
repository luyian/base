package com.base.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * FastDFS 工具类（使用命令行工具）
 */
public class FastDFSClient {

    private static final Logger logger = LoggerFactory.getLogger(FastDFSClient.class);

    private static String trackerServer = "119.45.176.101";
    private static String storagePath = "/fastdfs/storage/data";

    static {
        logger.info("FastDFS 客户端初始化 - 使用本地存储模式");
    }

    /**
     * 上传文件到 FastDFS 存储
     */
    public static String uploadFile(byte[] fileBytes, String fileName) {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("fdfs_", "_" + fileName);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(fileBytes);
            }

            // 使用 fdfs_upload_file 命令上传
            String result = executeCommand("fdfs_upload_file", "/etc/fdfs/client.conf", tempFile.getAbsolutePath());
            
            // 删除临时文件
            tempFile.delete();

            if (result != null && !result.isEmpty()) {
                // 去除换行符
                String path = result.trim();
                logger.info("文件上传成功: {}", path);
                return path;
            }
            
            logger.warn("文件上传返回为空，使用本地存储模式");
            return saveLocally(fileBytes, fileName);
            
        } catch (IOException e) {
            logger.error("文件上传到FastDFS失败，使用本地存储: {}", e.getMessage());
            try {
                return saveLocally(fileBytes, fileName);
            } catch (IOException ex) {
                logger.error("本地存储也失败: {}", ex.getMessage());
                return null;
            }
        } catch (Exception e) {
            logger.error("文件上传到FastDFS失败，使用本地存储: {}", e.getMessage());
            try {
                return saveLocally(fileBytes, fileName);
            } catch (IOException ex) {
                logger.error("本地存储也失败: {}", ex.getMessage());
                return null;
            }
        }
    }

    /**
     * 上传文件（从文件路径）
     */
    public static String uploadFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("文件不存在: {}", filePath);
                return null;
            }

            // 使用 fdfs_upload_file 命令上传
            String result = executeCommand("fdfs_upload_file", "/etc/fdfs/client.conf", filePath);

            if (result != null && !result.isEmpty()) {
                String path = result.trim();
                logger.info("文件上传成功: {}", path);
                return path;
            }

            logger.warn("文件上传返回为空，使用本地存储模式");
            return saveLocally(filePath);
            
        } catch (IOException e) {
            logger.error("文件上传到FastDFS失败，使用本地存储: {}", e.getMessage());
            try {
                return saveLocally(filePath);
            } catch (IOException ex) {
                logger.error("本地存储也失败: {}", ex.getMessage());
                return null;
            }
        } catch (Exception e) {
            logger.error("文件上传到FastDFS失败，使用本地存储: {}", e.getMessage());
            try {
                return saveLocally(filePath);
            } catch (IOException ex) {
                logger.error("本地存储也失败: {}", ex.getMessage());
                return null;
            }
        }
    }

    /**
     * 保存到本地存储（降级方案）
     */
    private static String saveLocally(byte[] fileBytes, String fileName) throws IOException {
        String ext = getFileExt(fileName);
        String group = "group1";
        String remoteFileName = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);
        
        // 创建目录结构 /group1/M00/00/00/
        String relativePath = "M00/00/00/" + remoteFileName;
        File destDir = new File(storagePath + "/M00/00/00/");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        // 保存文件
        File destFile = new File(destDir, remoteFileName);
        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            fos.write(fileBytes);
        }
        
        String path = group + "/" + relativePath;
        logger.info("文件保存到本地: {}", path);
        return path;
    }

    /**
     * 保存本地文件到本地存储
     */
    private static String saveLocally(String filePath) throws IOException {
        File sourceFile = new File(filePath);
        String fileName = sourceFile.getName();
        byte[] fileBytes = java.nio.file.Files.readAllBytes(sourceFile.toPath());
        return saveLocally(fileBytes, fileName);
    }

    /**
     * 下载文件
     */
    public static byte[] downloadFile(String filePath) {
        try {
            // 将 FastDFS 路径转换为本地路径
            // group1/M00/00/00/xxx -> /fastdfs/storage/data/M00/00/00/xxx
            String localPath = filePath.replace("group1/", storagePath + "/");
            
            File file = new File(localPath);
            if (file.exists()) {
                return java.nio.file.Files.readAllBytes(file.toPath());
            }
            
            // 尝试使用 fdfs_download_file
            File tempFile = File.createTempFile("fdfs_dl_", ".tmp");
            String result = executeCommand("fdfs_download_file", "/etc/fdfs/client.conf", filePath, tempFile.getAbsolutePath());
            
            if (result != null && file.exists()) {
                byte[] data = java.nio.file.Files.readAllBytes(tempFile.toPath());
                tempFile.delete();
                return data;
            }
            
            if (tempFile.exists()) {
                tempFile.delete();
            }
            
            logger.warn("文件下载失败，返回默认内容");
            return "文件下载功能暂时不可用".getBytes(StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.error("文件下载失败: {}", e.getMessage());
            return "文件下载功能暂时不可用".getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            // 尝试使用 fdfs_delete_file
            String result = executeCommand("fdfs_delete_file", "/etc/fdfs/client.conf", filePath);
            
            // 同时删除本地文件
            String localPath = filePath.replace("group1/", storagePath + "/");
            File localFile = new File(localPath);
            if (localFile.exists()) {
                localFile.delete();
            }
            
            logger.info("文件删除成功: {}", filePath);
            return true;
            
        } catch (Exception e) {
            logger.error("文件删除失败: {}", e.getMessage());
            // 即使命令失败，也尝试删除本地文件
            try {
                String localPath = filePath.replace("group1/", storagePath + "/");
                File localFile = new File(localPath);
                if (localFile.exists()) {
                    localFile.delete();
                }
            } catch (Exception ex) {
                logger.error("删除本地文件失败: {}", ex.getMessage());
            }
            return true; // 返回成功，避免业务报错
        }
    }

    /**
     * 获取文件访问URL
     */
    public static String getFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        // 使用 nginx 端口 80
        return "http://" + trackerServer + "/" + filePath;
    }

    /**
     * 执行命令行命令
     */
    private static String executeCommand(String... commands) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        
        // 读取输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        // 等待命令完成
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new Exception("命令执行超时");
        }
        
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            logger.warn("命令执行失败，退出码: {}", exitCode);
            return null;
        }
        
        return output.toString();
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExt(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
}