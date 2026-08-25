package com.base.system.service.impl;

import com.base.ai.config.AiSkillConfig;
import com.base.common.exception.BusinessException;
import com.base.common.service.CosService;
import com.base.system.dto.ScanDocImage;
import com.base.system.dto.ScanDocWorkArea;
import com.base.system.entity.SysFile;
import com.base.system.mapper.SysFileMapper;
import com.base.system.service.ScanDocService;
import com.base.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 扫描文档整理服务实现
 *
 * @author base
 * @since 2026-08-25
 */
@Slf4j
@Service
public class ScanDocServiceImpl implements ScanDocService {

    /** 允许的图片扩展名 */
    private static final Set<String> IMAGE_EXTENSIONS =
            new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".bmp", ".webp"));

    /** 通配图片 MIME，用于 multipart part */
    private static final MediaType IMAGE_MEDIA_TYPE = MediaType.APPLICATION_OCTET_STREAM;

    private final AiSkillConfig aiSkillConfig;
    private final CosService cosService;
    private final SysFileMapper sysFileMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    /** 进行中的工作区：docId -> 工作区 */
    private final Map<String, ScanDocWorkArea> workAreas = new ConcurrentHashMap<>();

    /** 唯一编号防同毫秒冲突的自增后缀 */
    private final AtomicInteger docNoSeq = new AtomicInteger();

    /**
     * 临时根目录（必须为绝对路径）。
     * 默认系统临时目录下的 base-scan，避免相对路径被 Servlet 容器的
     * MultipartFile.transferTo 重定向到容器的 multipart 临时目录导致目录不一致。
     */
    @Value("${scan-doc.temp-dir:${java.io.tmpdir}/base-scan}")
    private String tempDir;

    public ScanDocServiceImpl(AiSkillConfig aiSkillConfig, CosService cosService,
                              SysFileMapper sysFileMapper) {
        this.aiSkillConfig = aiSkillConfig;
        this.cosService = cosService;
        this.sysFileMapper = sysFileMapper;
    }

    @Override
    public Map<String, Object> create(String docName) {
        String name = (docName == null || docName.trim().isEmpty()) ? "扫描文档" : docName.trim();
        String docId = generateDocNo();
        ScanDocWorkArea workArea = new ScanDocWorkArea();
        workArea.setDocId(docId);
        workArea.setDocNo(docId);
        workArea.setDocName(name);
        workArea.setUserId(SecurityUtils.getCurrentUserId());
        workArea.setStatus(0);
        workAreas.put(docId, workArea);

        Map<String, Object> result = new HashMap<>(4);
        result.put("docId", docId);
        result.put("docNo", docId);
        result.put("docName", name);
        log.info("创建扫描文档工作区: docId={}, docName={}", docId, name);
        return result;
    }

    @Override
    public Map<String, Object> appendImages(String docId, MultipartFile[] files) {
        ScanDocWorkArea workArea = requireWorkArea(docId);
        if (files == null || files.length == 0) {
            throw new BusinessException("未收到图片文件");
        }
        synchronized (workArea) {
            checkWritable(workArea);
            Path dir = scanDir(workArea.getUserId(), docId);
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new BusinessException("图片存储目录创建失败");
            }
            // 新图接在现有最大 sort 之后，保持追加语义
            int base = workArea.getImages().stream()
                    .mapToInt(ScanDocImage::getSort).max().orElse(0);
            List<String> errors = new ArrayList<>();
            List<ScanDocImage> added = new ArrayList<>();
            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();
                String ext = fileExtOf(originalName);
                if (file.isEmpty() || !IMAGE_EXTENSIONS.contains(ext)) {
                    errors.add((originalName == null ? "未知文件" : originalName) + "(仅支持 JPG/PNG/BMP/WebP)");
                    continue;
                }
                if (file.getSize() > 50 * 1024 * 1024) {
                    errors.add((originalName == null ? "未知文件" : originalName) + "(单张不能超过 50MB)");
                    continue;
                }
                String imageId = UUID.randomUUID().toString().replace("-", "");
                Path target = dir.resolve(imageId + ext);
                try {
                    file.transferTo(target.toFile());
                } catch (IOException e) {
                    log.warn("图片落盘失败: {}", originalName, e);
                    errors.add((originalName == null ? "未知文件" : originalName) + "(保存失败)");
                    continue;
                }
                ScanDocImage image = new ScanDocImage();
                image.setId(imageId);
                image.setFileName(originalName);
                image.setTempPath(target.toAbsolutePath().toString());
                image.setSort(base + added.size() + 1);
                image.setSize(file.getSize());
                workArea.getImages().add(image);
                added.add(image);
            }
            log.info("扫描文档工作区追加图片: docId={}, 本次成功 {} 张", docId, added.size());
            if (!errors.isEmpty() && added.isEmpty()) {
                throw new BusinessException(String.join("；", errors));
            }
            if (!errors.isEmpty()) {
                log.warn("部分图片未添加: {}", errors);
            }
        }
        return imagesResult(workArea);
    }

    @Override
    public Map<String, Object> reorderImages(String docId, List<String> ids) {
        ScanDocWorkArea workArea = requireWorkArea(docId);
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("图片顺序列表为空");
        }
        synchronized (workArea) {
            checkWritable(workArea);
            Set<String> idSet = new HashSet<>(ids);
            if (idSet.size() != workArea.getImages().size()
                    || !idSet.containsAll(workArea.getImages().stream()
                    .map(ScanDocImage::getId).collect(Collectors.toSet()))) {
                throw new BusinessException("顺序列表与当前图片列表不匹配");
            }
            Map<String, ScanDocImage> byId = workArea.getImages().stream()
                    .collect(Collectors.toMap(ScanDocImage::getId, img -> img));
            List<ScanDocImage> reordered = new ArrayList<>(ids.size());
            for (int i = 0; i < ids.size(); i++) {
                ScanDocImage image = byId.get(ids.get(i));
                image.setSort(i + 1);
                reordered.add(image);
            }
            workArea.getImages().clear();
            workArea.getImages().addAll(reordered);
        }
        return imagesResult(workArea);
    }

    @Override
    public Map<String, Object> deleteImage(String docId, String imageId) {
        ScanDocWorkArea workArea = requireWorkArea(docId);
        synchronized (workArea) {
            checkWritable(workArea);
            workArea.getImages().stream()
                    .filter(image -> image.getId().equals(imageId))
                    .findFirst()
                    .ifPresent(image -> {
                        try {
                            Files.deleteIfExists(Paths.get(image.getTempPath()));
                        } catch (IOException e) {
                            log.warn("删除图片临时文件失败: {}", image.getTempPath(), e);
                        }
                        workArea.getImages().remove(image);
                    });
        }
        return imagesResult(workArea);
    }

    @Override
    public void deleteWorkArea(String docId) {
        ScanDocWorkArea workArea = workAreas.remove(docId);
        if (workArea == null) {
            throw new BusinessException("文档工作区不存在");
        }
        synchronized (workArea) {
            deleteWorkAreaFiles(workArea);
            workArea.getImages().clear();
        }
        log.info("放弃并清理扫描文档工作区: docId={}", docId);
    }

    @Override
    public Map<String, Object> finalize(String docId, int maxSide, int quality) {
        ScanDocWorkArea workArea = requireWorkArea(docId);
        synchronized (workArea) {
            checkWritable(workArea);
            List<ScanDocImage> images = workArea.getImages();
            if (images.isEmpty()) {
                throw new BusinessException("请先添加图片再完成整理");
            }

            // 1. 按 sort 排序后交 python-tools 压缩合并
            List<Path> paths = images.stream()
                    .sorted(Comparator.comparingInt(ScanDocImage::getSort))
                    .map(image -> Paths.get(image.getTempPath()))
                    .collect(Collectors.toList());
            byte[] pdfBytes = callPythonImages(paths, maxSide, quality);

            // 2. 上传 COS + 写 sys_file，命名 {命名}_{编号}.pdf
            String pdfName = workArea.getDocName() + "_" + workArea.getDocNo() + ".pdf";
            SysFile targetFile = uploadPdfBytesAndRecord(pdfBytes, pdfName);

            // 3. 删除源图及临时目录，工作区置已完成
            deleteWorkAreaFiles(workArea);
            workArea.getImages().clear();
            workArea.setStatus(1);

            log.info("扫描文档归档完成: docId={}, 共 {} 张 -> PDF {}",
                    docId, images.size(), targetFile.getOriginalName());

            Map<String, Object> result = new HashMap<>(2);
            result.put("targetFile", buildFileInfo(targetFile));
            return result;
        }
    }

    /**
     * 调用 python-tools 图片转 PDF 接口（multipart 多 part）
     *
     * @param imagePaths 有序图片路径
     * @param maxSide    降采样最长边
     * @param quality    JPEG 质量
     * @return PDF 字节
     */
    private byte[] callPythonImages(List<Path> imagePaths, int maxSide, int quality) {
        String url = aiSkillConfig.getPythonToolsUrl() + "/api/pdf/images-to-pdf";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            for (Path path : imagePaths) {
                try {
                    byte[] bytes = Files.readAllBytes(path);
                    ByteArrayResource resource = new ByteArrayResource(bytes) {
                        @Override
                        public String getFilename() {
                            return path.getFileName().toString();
                        }
                    };
                    HttpHeaders fileHeaders = new HttpHeaders();
                    fileHeaders.setContentType(IMAGE_MEDIA_TYPE);
                    fileHeaders.setContentDispositionFormData("files", path.getFileName().toString());
                    body.add("files", new HttpEntity<>(resource, fileHeaders));
                } catch (IOException e) {
                    throw new RuntimeException("读取图片失败: " + path);
                }
            }
            // 普通表单字段
            body.add("max_side", String.valueOf(maxSide));
            body.add("quality", String.valueOf(quality));

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(body, headers), byte[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("python-tools 返回异常: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 python-tools 图片转 PDF 失败", e);
            throw new BusinessException("图片转 PDF 服务异常，请稍后重试");
        }
    }

    /**
     * 上传 PDF 到 COS 并写入 sys_file 记录
     */
    private SysFile uploadPdfBytesAndRecord(byte[] data, String fileName) {
        String cosKey = cosService.uploadFile(data, "scan-doc", "pdf");
        SysFile sysFile = new SysFile();
        sysFile.setFileName(cosKey);
        sysFile.setOriginalName(fileName);
        sysFile.setFileExt("pdf");
        sysFile.setFileSize((long) data.length);
        sysFile.setFileType("application/pdf");
        sysFile.setFilePath(cosKey);
        sysFile.setFileUrl(cosKey);
        sysFile.setFileGroup("scan-doc");
        sysFile.setFileDesc("扫描图片整理");
        sysFile.setStatus(1);
        sysFile.setCreateTime(LocalDateTime.now());
        sysFile.setUpdateTime(LocalDateTime.now());
        try {
            sysFile.setUploadUserId(SecurityUtils.getCurrentUserId());
            sysFile.setUploadUserName(SecurityUtils.getCurrentUsername());
        } catch (Exception ignored) {
            // 获取用户信息失败时忽略
        }
        sysFileMapper.insert(sysFile);
        sysFile.setFileUrl(cosService.getFileUrl(cosKey, fileName));
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
     * 构建图片列表返回结果
     */
    private Map<String, Object> imagesResult(ScanDocWorkArea workArea) {
        List<Map<String, Object>> list = workArea.getImages().stream()
                .sorted(Comparator.comparingInt(ScanDocImage::getSort))
                .map(this::buildImageInfo)
                .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>(4);
        result.put("docId", workArea.getDocId());
        result.put("docName", workArea.getDocName());
        result.put("status", workArea.getStatus());
        result.put("images", list);
        return result;
    }

    private Map<String, Object> buildImageInfo(ScanDocImage image) {
        Map<String, Object> info = new HashMap<>(5);
        info.put("id", image.getId());
        info.put("fileName", image.getFileName());
        info.put("size", image.getSize());
        info.put("sort", image.getSort());
        return info;
    }

    private ScanDocWorkArea requireWorkArea(String docId) {
        ScanDocWorkArea workArea = workAreas.get(docId);
        if (workArea == null) {
            throw new BusinessException("文档工作区不存在或已结束");
        }
        return workArea;
    }

    private void checkWritable(ScanDocWorkArea workArea) {
        if (workArea.getStatus() == 1) {
            throw new BusinessException("文档已完成，不能再添加或修改图片");
        }
    }

    /**
     * 删除工作区全部源图及其临时目录
     */
    private void deleteWorkAreaFiles(ScanDocWorkArea workArea) {
        Path dir = scanDir(workArea.getUserId(), workArea.getDocId());
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            List<Path> paths = walk.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            for (Path p : paths) {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("清理临时文件失败: {}", p, e);
                }
            }
        } catch (IOException e) {
            log.warn("扫描临时目录清理失败: {}", dir, e);
        }
    }

    /**
     * 工作区临时目录：{tempDir}/{userId}/{docId}（始终返回绝对路径）
     */
    private Path scanDir(Long userId, String docId) {
        return Paths.get(tempDir).toAbsolutePath().resolve(String.valueOf(userId)).resolve(docId);
    }

    /**
     * 生成唯一编号：PDF + 时间戳 + 3位自增防同毫秒冲突
     */
    private String generateDocNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int seq = docNoSeq.incrementAndGet() % 1000;
        return "PDF" + ts + String.format(Locale.ROOT, "%03d", seq);
    }

    private String fileExtOf(String fileName) {
        if (fileName == null) {
            return "";
        }
        int idx = fileName.lastIndexOf('.');
        return idx < 0 ? "" : fileName.substring(idx).toLowerCase(Locale.ROOT);
    }

    /**
     * 服务关闭时兜底清理所有扫描临时目录
     */
    @PreDestroy
    public void onShutdown() {
        Path root = Paths.get(tempDir);
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> paths = walk.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            for (Path p : paths) {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("重启清理临时目录失败: {}", p, e);
                }
            }
        } catch (IOException e) {
            log.warn("重启清理扫描临时目录失败: {}", root, e);
        }
    }
}