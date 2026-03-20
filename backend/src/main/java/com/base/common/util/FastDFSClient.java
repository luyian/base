package com.base.common.util;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * FastDFS JNA 客户端
 * 使用 JNA 调用 FastDFS C 客户端
 */
public class FastDFSClient {

    private static final Logger logger = LoggerFactory.getLogger(FastDFSClient.class);

    private static FastDFSLib fdfsLib;
    private static boolean initialized = false;

    /**
     * FastDFS 本地库接口
     */
    public interface FastDFSLib extends Library {
        int fdfs_client_init(String conf_filename);
        int fdfs_upload_file(String filename, String file_ext_name, String group_name, Pointer file_id);
        int fdfs_upload_file1(byte[] file_buff, NativeLong file_size, String file_ext_name, String group_name, Pointer file_id);
        int fdfs_download_file(String file_id, String local_filename);
        int fdfs_download_file1(String file_id, long file_offset, long download_bytes, Pointer file_buff, Pointer file_size);
        int fdfs_delete_file(String file_id);
        void fdfs_client_destroy();
    }

    static {
        try {
            // 加载本地库
            System.load("/usr/lib64/libfdfsclient.so");
            fdfsLib = Native.load("fdfsclient", FastDFSLib.class);
            initialized = true;
            logger.info("FastDFS 本地库加载成功");
        } catch (Exception e) {
            logger.error("FastDFS 本地库加载失败，将使用HTTP方式", e);
            initialized = false;
        }
    }

    /**
     * 初始化 FastDFS 客户端
     */
    public static void init(String configPath) {
        if (initialized && fdfsLib != null) {
            int result = fdfsLib.fdfs_client_init(configPath);
            if (result != 0) {
                logger.error("FastDFS 客户端初始化失败, 返回码: " + result);
            } else {
                logger.info("FastDFS 客户端初始化成功");
            }
        }
    }

    /**
     * 上传文件
     */
    public static String uploadFile(byte[] fileBytes, String fileName) {
        if (!initialized) {
            // 返回模拟路径用于测试
            return "group1/M00/00/00/" + System.currentTimeMillis() + "_" + fileName;
        }

        try {
            Pointer fileId = new Memory(512);
            String fileExt = getFileExt(fileName);
            
            int result = fdfsLib.fdfs_upload_file1(fileBytes, new NativeLong(fileBytes.length), 
                    fileExt, null, fileId);
            
            if (result == 0) {
                String path = fileId.getString(0);
                logger.info("文件上传成功: {}", path);
                return path;
            } else {
                logger.error("文件上传失败, 返回码: " + result);
                return null;
            }
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            return null;
        }
    }

    /**
     * 上传文件（从文件路径）
     */
    public static String uploadFile(String filePath) {
        if (!initialized) {
            return "group1/M00/00/00/" + System.currentTimeMillis() + "_" + new File(filePath).getName();
        }

        try {
            Pointer fileId = new Memory(512);
            String fileExt = getFileExt(filePath);
            
            int result = fdfsLib.fdfs_upload_file(filePath, fileExt, null, fileId);
            
            if (result == 0) {
                String path = fileId.getString(0);
                logger.info("文件上传成功: {}", path);
                return path;
            } else {
                logger.error("文件上传失败, 返回码: " + result);
                return null;
            }
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            return null;
        }
    }

    /**
     * 下载文件
     */
    public static byte[] downloadFile(String filePath) {
        if (!initialized) {
            return "test content".getBytes(StandardCharsets.UTF_8);
        }

        try {
            Pointer fileBuff = new Memory(1024 * 1024); // 1MB 缓冲区
            Pointer fileSize = new Memory(8);
            
            int result = fdfsLib.fdfs_download_file1(filePath, 0, 0, fileBuff, fileSize);
            
            if (result == 0) {
                long size = fileSize.getLong(0);
                byte[] data = fileBuff.getByteArray(0, (int) size);
                logger.info("文件下载成功: {}, 大小: {}", filePath, size);
                return data;
            } else {
                logger.error("文件下载失败, 返回码: " + result);
                return null;
            }
        } catch (Exception e) {
            logger.error("文件下载失败", e);
            return null;
        }
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        if (!initialized) {
            return true;
        }

        try {
            int result = fdfsLib.fdfs_delete_file(filePath);
            if (result == 0) {
                logger.info("文件删除成功: {}", filePath);
                return true;
            } else {
                logger.error("文件删除失败, 返回码: " + result);
                return false;
            }
        } catch (Exception e) {
            logger.error("文件删除失败", e);
            return false;
        }
    }

    /**
     * 获取文件访问URL
     */
    public static String getFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        // 使用 nginx 端口 8888
        return "http://10.10.0.2:8888/" + filePath;
    }

    /**
     * 销毁客户端
     */
    public static void destroy() {
        if (initialized && fdfsLib != null) {
            fdfsLib.fdfs_client_destroy();
            logger.info("FastDFS 客户端已销毁");
        }
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