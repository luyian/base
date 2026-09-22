package com.base.system.service.impl;

import com.base.ai.config.AiSkillConfig;
import com.base.common.exception.BusinessException;
import com.base.common.service.CosService;
import com.base.system.entity.SysFile;
import com.base.system.mapper.SysFileMapper;
import com.base.system.service.ImageWatermarkService;
import com.base.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片去水印服务实现
 * <p>
 * 链路：上传源图到 COS → 转发 python-tools /api/image/remove-watermark
 * → 收去水印后图片 → 上传 COS 并写 sys_file → 返回文件信息。
 * </p>
 *
 * @author base
 * @since 2026-09-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageWatermarkServiceImpl implements ImageWatermarkService {

    private final AiSkillConfig aiSkillConfig;
    private final CosService cosService;
    private final SysFileMapper sysFileMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    /** 允许的图片扩展名 */
    private static final String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".webp"};

    @Override
    public Map<String, Object> removeWatermark(MultipartFile file, String region, String backend) {
        String originalName = file.getOriginalFilename();
        log.info("开始图片去水印: {}, region={}, backend={}", originalName, region, backend);

        // 1. 上传源图到 COS 并记录
        SysFile sourceSysFile = uploadAndRecord(file);

        // 2. 调用 python-tools 去水印（region/backend 作为额外表单字段透传）
        Map<String, String> formFields = new HashMap<>(2);
        formFields.put("backend", backend == null || backend.isEmpty() ? "cv2" : backend);
        if (region != null && !region.isEmpty()) {
            formFields.put("region", region);
        }
        byte[] cleanBytes = callPythonTools(file, formFields);

        // 3. 上传去水印后的图片到 COS 并记录
        String ext = fileExtOf(originalName);
        String cleanName = baseNameOf(originalName) + "_clean" + ext;
        String contentType = contentTypeOf(ext);
        SysFile targetSysFile = uploadBytesAndRecord(cleanBytes, cleanName, ext, contentType);

        // 4. 组装返回结果
        Map<String, Object> result = new HashMap<>(4);
        result.put("sourceFile", buildFileInfo(sourceSysFile));
        result.put("targetFile", buildFileInfo(targetSysFile));
        return result;
    }

    /**
     * 调用 python-tools 图片去水印接口
     *
     * @param file        源图片
     * @param formFields  额外表单字段（backend/region）
     * @return 去水印后的图片字节
     */
    private byte[] callPythonTools(MultipartFile file, Map<String, String> formFields) {
        String url = aiSkillConfig.getPythonToolsUrl() + "/api/image/remove-watermark";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            fileHeaders.setContentDispositionFormData("file", file.getOriginalFilename());
            body.add("file", new HttpEntity<>(resource, fileHeaders));
            formFields.forEach(body::add);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, byte[].class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("python-tools 返回异常: " + response.getStatusCode());
            }
            log.info("图片去水印成功: {}，结果大小 {} bytes", file.getOriginalFilename(), response.getBody().length);
            return response.getBody();
        } catch (Exception e) {
            log.error("调用 python-tools 去水印失败", e);
            throw new BusinessException("去水印服务异常，请稍后重试");
        }
    }

    /**
     * 上传 MultipartFile 到 COS 并写入 sys_file 记录
     */
    private SysFile uploadAndRecord(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String ext = fileExtOf(originalName);
            String cosKey = cosService.uploadFile(file.getBytes(), "watermark", ext, originalName);
            return saveSysFile(originalName, ext, file.getSize(),
                    file.getContentType(), cosKey);
        } catch (Exception e) {
            throw new BusinessException("源文件上传失败，请稍后重试");
        }
    }

    /**
     * 上传 byte[] 到 COS 并写入 sys_file 记录
     */
    private SysFile uploadBytesAndRecord(byte[] data, String fileName, String fileExt, String contentType) {
        String cosKey = cosService.uploadFile(data, "watermark", fileExt, fileName);
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
        sysFile.setFileGroup("watermark");
        sysFile.setFileDesc("图片去水印");
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
        sysFile.setFileUrl(cosService.getFileUrl(cosKey, originalName));
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
     * 提取文件扩展名（小写，含点）
     */
    private String fileExtOf(String fileName) {
        if (fileName == null) {
            return ".png";
        }
        int idx = fileName.lastIndexOf('.');
        return idx < 0 ? ".png" : fileName.substring(idx).toLowerCase();
    }

    /**
     * 提取不含扩展名的文件名
     */
    private String baseNameOf(String fileName) {
        if (fileName == null) {
            return "image";
        }
        int idx = fileName.lastIndexOf('.');
        return idx < 0 ? fileName : fileName.substring(0, idx);
    }

    /**
     * 图片扩展名 → Content-Type 映射，预览图内嵌需要正确类型
     */
    private String contentTypeOf(String ext) {
        switch (ext) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".bmp":
                return "image/bmp";
            case ".webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 校验是否为允许的图片扩展名
     */
    public static boolean isImageFile(String fileName) {
        if (fileName == null) {
            return false;
        }
        String name = fileName.toLowerCase();
        for (String ext : IMAGE_EXTENSIONS) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}