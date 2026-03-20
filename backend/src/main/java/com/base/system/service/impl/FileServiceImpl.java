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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
    public SysFile uploadFile(MultipartFile file, String fileDesc) {
        return uploadFile(file, "default", fileDesc);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile uploadFile(MultipartFile file, String fileGroup, String fileDesc) {
        long startTime = System.currentTimeMillis();
        SysFileLog fileLog = new SysFileLog();
        
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
    public void downloadFile(Long id, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        SysFileLog fileLog = new SysFileLog();

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
            response.setContentLengthLong(sysFile.getFileSize());

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