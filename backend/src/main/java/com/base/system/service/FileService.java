package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.entity.SysFile;
import com.base.system.entity.SysFileLog;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传文件
     */
    SysFile uploadFile(MultipartFile file, String fileDesc, HttpServletRequest request);

    /**
     * 上传文件（带分组）
     */
    SysFile uploadFile(MultipartFile file, String fileGroup, String fileDesc, HttpServletRequest request);

    /**
     * 根据ID获取文件信息
     */
    SysFile getFileById(Long id);

    /**
     * 分页查询文件列表
     */
    Page<SysFile> pageFiles(Long pageNum, Long pageSize, String fileName, String fileGroup);

    /**
     * 删除文件
     */
    void deleteFile(Long id);

    /**
     * 批量删除文件
     */
    void batchDeleteFiles(List<Long> ids);

    /**
     * 下载文件
     */
    void downloadFile(Long id, HttpServletResponse response, HttpServletRequest request);

    /**
     * 获取文件访问URL
     */
    String getFileUrl(String filePath);

    /**
     * 保存文件操作日志
     */
    void saveFileLog(SysFileLog fileLog);
}