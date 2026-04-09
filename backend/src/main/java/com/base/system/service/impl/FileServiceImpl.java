package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.util.FastDFSClient;
import com.base.system.entity.SysFile;
import com.base.system.entity.SysFileLog;
import com.base.system.mapper.SysFileLogMapper;
import com.base.system.mapper.SysFileMapper;
import com.base.system.service.FileService;
import com.base.util.SecurityUtils;
import com.base.system.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private SysFileMapper sysFileMapper;

    @Autowired
    private SysFileLogMapper sysFileLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile uploadFile(MultipartFile file, String fileDesc, HttpServletRequest request) {
        return uploadFile(file, "default", fileDesc, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile uploadFile(MultipartFile file, String fileGroup, String fileDesc, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        SysFileLog fileLog = new SysFileLog();
        
        // 获取IP和地址
        String ip = IpUtils.getIpAddress(request);
        String location = ""; // 需要IP解析库
        fileLog.setIp(ip);
        fileLog.setLocation(location);
        
        try {
            // 获取当前用户
            Long currentUserId = SecurityUtils.getCurrentUserId();
            String currentUsername = SecurityUtils.getCurrentUsername();

            // 上传到 FastDFS
            String originalName = file.getOriginalFilename();
            String filePath = FastDFSClient.uploadFile(file.getBytes(), originalName);
            
            if (filePath == null) {
                throw new RuntimeException("文件上传到FastDFS失败");
            }

            // 获取文件信息
            String fileExt = getFileExt(originalName);
            long fileSize = file.getSize();
            String fileType = file.getContentType();

            // 保存文件记录
            SysFile sysFile = new SysFile();
            sysFile.setFileName(filePath);
            sysFile.setOriginalName(originalName);
            sysFile.setFileExt(fileExt);
            sysFile.setFileSize(fileSize);
            sysFile.setFileType(fileType);
            sysFile.setFilePath(filePath);
            sysFile.setFileUrl(FastDFSClient.getFileUrl(filePath));
            sysFile.setFileGroup(fileGroup);
            sysFile.setFileDesc(fileDesc);
            sysFile.setStatus(1);
            sysFile.setCreateTime(LocalDateTime.now());
            sysFile.setUpdateTime(LocalDateTime.now());

            if (currentUserId != null) {
                sysFile.setUploadUserId(currentUserId);
                sysFile.setUploadUserName(currentUsername);
                fileLog.setOperatorId(currentUserId);
                fileLog.setOperatorName(currentUsername);
            }

            sysFileMapper.insert(sysFile);

            // 记录日志
            long executeTime = System.currentTimeMillis() - startTime;
            fileLog.setFileId(sysFile.getId());
            fileLog.setFileName(originalName);
            fileLog.setFilePath(filePath);
            fileLog.setFileSize(fileSize);
            fileLog.setOperationType(1); // 上传
            fileLog.setStatus(1);
            fileLog.setExecuteTime((int) executeTime);
            fileLog.setCreateTime(LocalDateTime.now());
            sysFileLogMapper.insert(fileLog);

            logger.info("文件上传成功: {}, 耗时: {}ms", originalName, executeTime);
            return sysFile;

        } catch (Exception e) {
            long executeTime = System.currentTimeMillis() - startTime;
            logger.error("文件上传失败", e);

            // 记录失败日志
            fileLog.setOperationType(1);
            fileLog.setStatus(0);
            fileLog.setErrorMsg(e.getMessage());
            fileLog.setExecuteTime((int) executeTime);
            fileLog.setCreateTime(LocalDateTime.now());
            try {
                sysFileLogMapper.insert(fileLog);
            } catch (Exception ex) {
                logger.error("记录文件日志失败", ex);
            }

            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile uploadFile(MultipartFile file, String fileGroup, String fileDesc, String uploaderName, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        SysFileLog fileLog = new SysFileLog();

        // 获取IP和地址
        String ip = IpUtils.getIpAddress(request);
        fileLog.setIp(ip);
        fileLog.setLocation("");

        try {
            // 上传到 FastDFS
            String originalName = file.getOriginalFilename();
            String filePath = FastDFSClient.uploadFile(file.getBytes(), originalName);

            if (filePath == null) {
                throw new RuntimeException("文件上传到FastDFS失败");
            }

            // 获取文件信息
            String fileExt = getFileExt(originalName);
            long fileSize = file.getSize();
            String fileType = file.getContentType();

            // 保存文件记录
            SysFile sysFile = new SysFile();
            sysFile.setFileName(filePath);
            sysFile.setOriginalName(originalName);
            sysFile.setFileExt(fileExt);
            sysFile.setFileSize(fileSize);
            sysFile.setFileType(fileType);
            sysFile.setFilePath(filePath);
            sysFile.setFileUrl(FastDFSClient.getFileUrl(filePath));
            sysFile.setFileGroup(fileGroup);
            sysFile.setFileDesc(fileDesc);
            sysFile.setUploadUserName(uploaderName);
            sysFile.setStatus(1);
            sysFile.setCreateTime(LocalDateTime.now());
            sysFile.setUpdateTime(LocalDateTime.now());

            sysFileMapper.insert(sysFile);

            // 记录日志
            long executeTime = System.currentTimeMillis() - startTime;
            fileLog.setFileId(sysFile.getId());
            fileLog.setFileName(originalName);
            fileLog.setFilePath(filePath);
            fileLog.setFileSize(fileSize);
            fileLog.setOperationType(1);
            fileLog.setOperatorName(uploaderName);
            fileLog.setStatus(1);
            fileLog.setExecuteTime((int) executeTime);
            fileLog.setCreateTime(LocalDateTime.now());
            sysFileLogMapper.insert(fileLog);

            logger.info("开放接口文件上传成功: {}, 上传者: {}, 耗时: {}ms", originalName, uploaderName, executeTime);
            return sysFile;

        } catch (Exception e) {
            long executeTime = System.currentTimeMillis() - startTime;
            logger.error("开放接口文件上传失败", e);

            fileLog.setOperationType(1);
            fileLog.setOperatorName(uploaderName);
            fileLog.setStatus(0);
            fileLog.setErrorMsg(e.getMessage());
            fileLog.setExecuteTime((int) executeTime);
            fileLog.setCreateTime(LocalDateTime.now());
            try {
                sysFileLogMapper.insert(fileLog);
            } catch (Exception ex) {
                logger.error("记录文件日志失败", ex);
            }

            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public SysFile getFileById(Long id) {
        return sysFileMapper.selectById(id);
    }

    @Override
    public Page<SysFile> pageFiles(Long pageNum, Long pageSize, String fileName, String fileGroup) {
        Page<SysFile> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
        
        if (fileName != null && !fileName.isEmpty()) {
            wrapper.like(SysFile::getOriginalName, fileName);
        }
        if (fileGroup != null && !fileGroup.isEmpty()) {
            wrapper.eq(SysFile::getFileGroup, fileGroup);
        }
        
        wrapper.orderByDesc(SysFile::getCreateTime);
        return sysFileMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null) {
            throw new RuntimeException("文件不存在");
        }

        // 从 FastDFS 删除
        boolean deleted = FastDFSClient.deleteFile(sysFile.getFilePath());
        if (!deleted) {
            logger.warn("从FastDFS删除文件失败: {}", sysFile.getFilePath());
        }

        // 删除数据库记录
        sysFileMapper.deleteById(id);

        // 记录日志
        saveFileLog(sysFile, 3, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteFiles(List<Long> ids) {
        for (Long id : ids) {
            deleteFile(id);
        }
    }

    @Override
    public void downloadFile(Long id, HttpServletResponse response, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        SysFileLog fileLog = new SysFileLog();

        // 获取IP和地址
        String ip = IpUtils.getIpAddress(request);
        String location = ""; // 需要IP解析库
        fileLog.setIp(ip);
        fileLog.setLocation(location);

        try {
            SysFile sysFile = sysFileMapper.selectById(id);
            if (sysFile == null) {
                throw new RuntimeException("文件不存在");
            }

            // 从 FastDFS 下载
            byte[] fileBytes = FastDFSClient.downloadFile(sysFile.getFilePath());
            if (fileBytes == null) {
                throw new RuntimeException("从FastDFS下载文件失败");
            }

            // 设置响应头
            response.setContentType(sysFile.getFileType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + sysFile.getOriginalName() + "\"");
            // 使用实际文件大小，不使用预存的大小
            response.setContentLengthLong(fileBytes.length);

            // 写入响应
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(fileBytes);
            outputStream.flush();

            // 记录日志
            long executeTime = System.currentTimeMillis() - startTime;
            fileLog.setFileId(id);
            fileLog.setFileName(sysFile.getOriginalName());
            fileLog.setFilePath(sysFile.getFilePath());
            fileLog.setFileSize(sysFile.getFileSize());
            fileLog.setOperationType(2); // 下载
            fileLog.setStatus(1);
            fileLog.setExecuteTime((int) executeTime);
            saveFileLog(fileLog);

            logger.info("文件下载成功: {}, 耗时: {}ms", sysFile.getOriginalName(), executeTime);

        } catch (Exception e) {
            long executeTime = System.currentTimeMillis() - startTime;
            logger.error("文件下载失败", e);

            fileLog.setStatus(0);
            fileLog.setErrorMsg(e.getMessage());
            fileLog.setExecuteTime((int) executeTime);
            try {
                saveFileLog(fileLog);
            } catch (Exception ex) {
                logger.error("记录文件日志失败", ex);
            }

            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void batchDownloadFiles(List<Long> ids, HttpServletResponse response, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            // 批量查询文件信息
            List<SysFile> fileList = sysFileMapper.selectBatchIds(ids);
            if (fileList.isEmpty()) {
                throw new RuntimeException("未找到对应文件");
            }

            // 设置响应头
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"files.zip\"");

            // 创建 ZIP 输出流
            java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(response.getOutputStream());

            // 用于处理重名文件
            java.util.Map<String, Integer> nameCountMap = new java.util.HashMap<>();

            for (SysFile sysFile : fileList) {
                try {
                    // 通过 HTTP 从 fileUrl 下载，与单文件下载走同一通道
                    byte[] fileBytes = downloadFileFromUrl(sysFile.getFileUrl());
                    if (fileBytes == null || fileBytes.length == 0) {
                        logger.warn("批量下载跳过文件（下载失败）: {}", sysFile.getOriginalName());
                        continue;
                    }

                    // 处理重名
                    String entryName = sysFile.getOriginalName();
                    Integer count = nameCountMap.get(entryName);
                    if (count != null) {
                        String baseName = entryName;
                        String ext = "";
                        int dotIndex = entryName.lastIndexOf(".");
                        if (dotIndex > 0) {
                            baseName = entryName.substring(0, dotIndex);
                            ext = entryName.substring(dotIndex);
                        }
                        entryName = baseName + "(" + count + ")" + ext;
                        nameCountMap.put(sysFile.getOriginalName(), count + 1);
                    } else {
                        nameCountMap.put(entryName, 1);
                    }

                    // 写入 ZIP
                    java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(entryName);
                    zipOut.putNextEntry(zipEntry);
                    zipOut.write(fileBytes);
                    zipOut.closeEntry();
                } catch (Exception e) {
                    logger.warn("批量下载跳过文件: {}, 原因: {}", sysFile.getOriginalName(), e.getMessage());
                }
            }

            zipOut.finish();
            zipOut.flush();

            // 记录日志
            long executeTime = System.currentTimeMillis() - startTime;
            String ip = IpUtils.getIpAddress(request);
            Long currentUserId = SecurityUtils.getCurrentUserId();
            String currentUsername = SecurityUtils.getCurrentUsername();

            SysFileLog fileLog = new SysFileLog();
            fileLog.setFileName("批量下载(" + fileList.size() + "个文件)");
            fileLog.setOperationType(2);
            fileLog.setStatus(1);
            fileLog.setExecuteTime((int) executeTime);
            fileLog.setIp(ip);
            fileLog.setLocation("");
            fileLog.setCreateTime(LocalDateTime.now());
            if (currentUserId != null) {
                fileLog.setOperatorId(currentUserId);
                fileLog.setOperatorName(currentUsername);
            }
            sysFileLogMapper.insert(fileLog);

            logger.info("批量下载成功: {}个文件, 耗时: {}ms", fileList.size(), executeTime);

        } catch (Exception e) {
            logger.error("批量下载失败", e);
            throw new RuntimeException("批量下载失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        return FastDFSClient.getFileUrl(filePath);
    }

    @Override
    public void saveFileLog(SysFileLog fileLog) {
        sysFileLogMapper.insert(fileLog);
    }

    /**
     * 保存文件操作日志
     */
    private void saveFileLog(SysFile sysFile, Integer operationType, String errorMsg) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUsername = SecurityUtils.getCurrentUsername();

        SysFileLog fileLog = new SysFileLog();
        fileLog.setFileId(sysFile.getId());
        fileLog.setFileName(sysFile.getOriginalName());
        fileLog.setFilePath(sysFile.getFilePath());
        fileLog.setFileSize(sysFile.getFileSize());
        fileLog.setOperationType(operationType);
        fileLog.setStatus(errorMsg == null ? 1 : 0);
        fileLog.setErrorMsg(errorMsg);
        fileLog.setCreateTime(LocalDateTime.now());

        if (currentUserId != null) {
            fileLog.setOperatorId(currentUserId);
            fileLog.setOperatorName(currentUsername);
        }

        sysFileLogMapper.insert(fileLog);
    }

    /**
     * 通过 HTTP 从 URL 下载文件内容
     *
     * @param fileUrl 文件访问URL
     * @return 文件字节数组，失败返回 null
     */
    private byte[] downloadFileFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        try {
            // 确保使用 HTTPS 或 HTTP
            java.net.URL url = new java.net.URL(fileUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.warn("HTTP下载文件失败，状态码: {}, URL: {}", responseCode, fileUrl);
                return null;
            }

            try (java.io.InputStream is = conn.getInputStream();
                 java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return baos.toByteArray();
            }
        } catch (Exception e) {
            logger.error("HTTP下载文件失败, URL: {}, 原因: {}", fileUrl, e.getMessage());
            return null;
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExt(String fileName) {
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