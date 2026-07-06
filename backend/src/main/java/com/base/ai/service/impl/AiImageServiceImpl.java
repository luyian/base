package com.base.ai.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.base.ai.dto.ChatRequest;
import com.base.ai.image.AiImageContext;
import com.base.ai.image.AiImageModelResolver;
import com.base.ai.image.AiImageResult;
import com.base.ai.image.adapter.AiImageAdapter;
import com.base.ai.image.adapter.AiImageAdapterSelector;
import com.base.common.service.CosService;
import com.base.ai.service.AiConfigProvider;
import com.base.ai.service.AiImageService;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.entity.SysFile;
import com.base.system.mapper.SysFileMapper;
import com.base.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * AI 图片生成服务实现。
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiImageServiceImpl implements AiImageService {

    private static final int REMOTE_IMAGE_TIMEOUT = 60000;

    private static final int MAX_IMAGE_BYTES = 20 * 1024 * 1024;

    private static final String AI_IMAGE_GROUP = "ai";

    private static final int MAX_FILE_DESC_LENGTH = 500;

    private final AiConfigProvider aiConfigProvider;

    private final AiImageModelResolver imageModelResolver;

    private final AiImageAdapterSelector imageAdapterSelector;

    private final CosService cosService;

    private final SysFileMapper sysFileMapper;

    @Override
    public String generateImage(String userMessage) {
        ChatRequest request = new ChatRequest();
        request.setMessage(userMessage);
        return generateImage(request);
    }

    @Override
    public String generateImage(ChatRequest request) {
        if (!aiConfigProvider.isConfigured()) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }
        AiImageContext context = imageModelResolver.resolve(request, aiConfigProvider.getImageModel(),
                aiConfigProvider.getImageBaseUrl(), aiConfigProvider.getImageAdapter(), aiConfigProvider.getApiKey(),
                aiConfigProvider.getTimeout());
        if (!StringUtils.hasText(context.getPrompt())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "图片生成提示词不能为空");
        }
        if (!StringUtils.hasText(context.getModel())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "图片生成模型不能为空，请在 AI 配置中填写供应商实际支持的出图模型名称");
        }

        AiImageAdapter adapter = imageAdapterSelector.select(context);
        log.info("AI 图片生成适配器已选择，adapter: {}，model: {}，size: {}，baseUrl: {}", adapter.getName(),
                context.getModel(), context.getSize(), context.getBaseUrl());
        AiImageResult result = adapter.generate(context);
        persistImageToCos(result);
        return buildMarkdownAnswer(result);
    }

    private String buildMarkdownAnswer(AiImageResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("### 图片生成结果\n");
        sb.append("- 模型：").append(result.getModel()).append("\n");
        sb.append("- 尺寸：").append(result.getSize()).append("\n");
        sb.append("- 适配器：").append(result.getAdapterName()).append("\n");
        sb.append("- 提示词：").append(result.getPrompt()).append("\n");
        if (StringUtils.hasText(result.getRevisedPrompt()) && !result.getRevisedPrompt().equals(result.getPrompt())) {
            sb.append("- 优化提示词：").append(result.getRevisedPrompt()).append("\n");
        }

        String imageMarkdown = buildImageMarkdown(result);
        if (StringUtils.hasText(imageMarkdown)) {
            sb.append("\n").append(imageMarkdown);
        }
        if (StringUtils.hasText(result.getTextContent()) && !result.getTextContent().contains("![")
                && !StringUtils.hasText(imageMarkdown)) {
            sb.append("\n\n").append(result.getTextContent());
        }
        if (StringUtils.hasText(result.getImageUrl())) {
            sb.append("\n\n[打开原图](").append(result.getImageUrl()).append(")");
        }
        return sb.toString();
    }

    private String buildImageMarkdown(AiImageResult result) {
        if (StringUtils.hasText(result.getImageUrl())) {
            return "![AI 生成图片](" + result.getImageUrl() + ")";
        }
        if (StringUtils.hasText(result.getImageBase64())) {
            return "![AI 生成图片](data:image/png;base64," + result.getImageBase64() + ")";
        }
        return null;
    }

    private void persistImageToCos(AiImageResult result) {
        byte[] imageBytes = loadImageBytes(result);
        if (imageBytes == null || imageBytes.length == 0) {
            return;
        }
        if (imageBytes.length > MAX_IMAGE_BYTES) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "AI 生成图片过大，暂不支持保存");
        }
        String fileExt = resolveImageExt(result.getImageUrl());
        String cosKey = cosService.uploadFile(imageBytes, AI_IMAGE_GROUP, fileExt);
        String imageUrl = cosService.getFileUrl(cosKey);
        saveAiImageFileRecord(result, cosKey, fileExt, imageBytes.length);
        result.setImageUrl(imageUrl);
        result.setImageBase64(null);
        log.info("AI 生成图片已保存到 COS，key: {}", cosKey);
    }

    private void saveAiImageFileRecord(AiImageResult result, String cosKey, String fileExt, int fileSize) {
        SysFile sysFile = new SysFile();
        sysFile.setFileName(cosKey);
        sysFile.setOriginalName(buildAiImageOriginalName(fileExt));
        sysFile.setFileExt(fileExt);
        sysFile.setFileSize((long) fileSize);
        sysFile.setFileType(resolveImageContentType(fileExt));
        sysFile.setFilePath(cosKey);
        sysFile.setFileUrl(cosKey);
        sysFile.setFileGroup(AI_IMAGE_GROUP);
        sysFile.setFileDesc(buildAiImageFileDesc(result));
        sysFile.setStatus(1);
        sysFile.setCreateTime(LocalDateTime.now());
        sysFile.setUpdateTime(LocalDateTime.now());

        try {
            sysFile.setUploadUserId(SecurityUtils.getCurrentUserId());
            sysFile.setUploadUserName(SecurityUtils.getCurrentUsername());
        } catch (Exception e) {
            log.debug("记录 AI 生成图片上传用户失败: {}", e.getMessage());
        }
        sysFileMapper.insert(sysFile);
    }

    private String buildAiImageOriginalName(String fileExt) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "AI生成图片_" + timestamp + "." + fileExt;
    }

    private String buildAiImageFileDesc(AiImageResult result) {
        StringBuilder desc = new StringBuilder("AI生成图片");
        appendDescPart(desc, "模型", result.getModel());
        appendDescPart(desc, "尺寸", result.getSize());
        appendDescPart(desc, "适配器", result.getAdapterName());
        appendDescPart(desc, "提示词", result.getPrompt());
        return truncate(desc.toString(), MAX_FILE_DESC_LENGTH);
    }

    private void appendDescPart(StringBuilder desc, String label, String value) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        desc.append("；").append(label).append("：").append(value);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private byte[] loadImageBytes(AiImageResult result) {
        if (StringUtils.hasText(result.getImageBase64())) {
            return Base64.decode(result.getImageBase64());
        }
        if (!StringUtils.hasText(result.getImageUrl())) {
            return null;
        }
        HttpResponse response = HttpRequest.get(result.getImageUrl())
                .timeout(REMOTE_IMAGE_TIMEOUT)
                .execute();
        if (!response.isOk()) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "AI 生成图片下载失败，状态码：" + response.getStatus());
        }
        return response.bodyBytes();
    }

    private String resolveImageExt(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return "png";
        }
        String lowerUrl = imageUrl.toLowerCase(Locale.ROOT);
        int queryIndex = lowerUrl.indexOf('?');
        if (queryIndex > -1) {
            lowerUrl = lowerUrl.substring(0, queryIndex);
        }
        if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) {
            return "jpg";
        }
        if (lowerUrl.endsWith(".webp")) {
            return "webp";
        }
        if (lowerUrl.endsWith(".gif")) {
            return "gif";
        }
        return "png";
    }

    private String resolveImageContentType(String fileExt) {
        if ("jpg".equals(fileExt) || "jpeg".equals(fileExt)) {
            return "image/jpeg";
        }
        if ("webp".equals(fileExt)) {
            return "image/webp";
        }
        if ("gif".equals(fileExt)) {
            return "image/gif";
        }
        return "image/png";
    }

}
