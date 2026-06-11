package com.base.system.service.impl;

import com.base.ai.config.AiSkillConfig;
import com.base.common.service.CosService;
import com.base.system.entity.SysFile;
import com.base.system.mapper.SysFileMapper;
import com.base.system.service.FileConvertService;
import com.base.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件转换服务实现
 *
 * @author base
 * @since 2026-06-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileConvertServiceImpl implements FileConvertService {

    private final AiSkillConfig aiSkillConfig;
    private final CosService cosService;
    private final SysFileMapper sysFileMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Map<String, Object> pdfToWord(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        log.info("开始 PDF 转 Word: {}", originalName);

        // 1. 上传源 PDF 到 COS 并记录
        SysFile sourceSysFile = uploadAndRecord(file, "pdf");

        // 2. 调用 python-tools 转换
        byte[] docxBytes = callPythonToolsConvert(file);

        // 3. 上传转换后的 docx 到 COS 并记录
        String docxName = originalName != null
                ? originalName.replaceAll("(?i)\\.pdf$", ".docx")
                : "output.docx";
        SysFile targetSysFile = uploadBytesAndRecord(docxBytes, docxName, "docx");

        // 4. 组装返回结果
        Map<String, Object> result = new HashMap<>(4);
        result.put("sourceFile", buildFileInfo(sourceSysFile));
        result.put("targetFile", buildFileInfo(targetSysFile));
        return result;
    }

    /**
     * 调用 python-tools PDF 转 Word 接口
     */
    private byte[] callPythonToolsConvert(MultipartFile file) {
        String url = aiSkillConfig.getPythonToolsUrl() + "/api/pdf/to-word";

        try {
            // 构建 multipart 请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", new HttpEntity<>(resource, createFileHeaders(file)));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, byte[].class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("python-tools 返回异常: " + response.getStatusCode());
            }

            log.info("PDF 转 Word 成功，文件大小: {} bytes", response.getBody().length);
            return response.getBody();

        } catch (Exception e) {
            log.error("调用 python-tools PDF 转 Word 失败", e);
            throw new RuntimeException("PDF 转换服务异常: " + e.getMessage());
        }
    }

    /**
     * 上传 MultipartFile 到 COS 并写入 sys_file 记录
     */
    private SysFile uploadAndRecord(MultipartFile file, String fileExt) {
        try {
            String cosKey = cosService.uploadFile(file.getBytes(), "convert", fileExt);
            return saveSysFile(file.getOriginalFilename(), fileExt, file.getSize(),
                    file.getContentType(), cosKey);
        } catch (Exception e) {
            throw new RuntimeException("源文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传 byte[] 到 COS 并写入 sys_file 记录
     */
    private SysFile uploadBytesAndRecord(byte[] data, String fileName, String fileExt) {
        String cosKey = cosService.uploadFile(data, "convert", fileExt);
        String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return saveSysFile(fileName, fileExt, (long) data.length, contentType, cosKey);
    }

    /**
     * 保存文件记录到数据库
     */
    private SysFile saveSysFile(String originalName, String fileExt, Long fileSize,
                                String fileType, String cosKey) {
        SysFile sysFile = new SysFile();
        sysFile.setFileName(cosKey);
        sysFile.setOriginalName(originalName);
        sysFile.setFileExt(fileExt);
        sysFile.setFileSize(fileSize);
        sysFile.setFileType(fileType);
        sysFile.setFilePath(cosKey);
        sysFile.setFileUrl(cosKey);
        sysFile.setFileGroup("convert");
        sysFile.setFileDesc("文件转换");
        sysFile.setStatus(1);
        sysFile.setCreateTime(LocalDateTime.now());
        sysFile.setUpdateTime(LocalDateTime.now());

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String username = SecurityUtils.getCurrentUsername();
            sysFile.setUploadUserId(userId);
            sysFile.setUploadUserName(username);
        } catch (Exception ignored) {
            // 获取用户信息失败时忽略
        }

        sysFileMapper.insert(sysFile);
        // 设置访问 URL
        sysFile.setFileUrl(cosService.getFileUrl(cosKey));
        return sysFile;
    }

    /**
     * 构建文件信息 Map 返回前端
     */
    private Map<String, Object> buildFileInfo(SysFile sysFile) {
        Map<String, Object> info = new HashMap<>(6);
        info.put("id", sysFile.getId());
        info.put("fileName", sysFile.getOriginalName());
        info.put("fileSize", sysFile.getFileSize());
        info.put("fileUrl", sysFile.getFileUrl());
        info.put("createTime", sysFile.getCreateTime());
        return info;
    }

    /**
     * 创建文件 part 的 headers
     */
    private HttpHeaders createFileHeaders(MultipartFile file) {
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_PDF);
        fileHeaders.setContentDispositionFormData("file", file.getOriginalFilename());
        return fileHeaders;
    }
}
