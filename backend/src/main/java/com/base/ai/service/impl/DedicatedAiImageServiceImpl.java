package com.base.ai.service.impl;

import com.base.ai.config.AiChatModelHolder;
import com.base.ai.dto.image.DedicatedAiImageGenerateRequest;
import com.base.ai.dto.image.DedicatedAiImageGenerateResponse;
import com.base.ai.dto.image.DedicatedAiImageItemResponse;
import com.base.ai.dto.image.DedicatedAiImagePolishRequest;
import com.base.ai.dto.image.DedicatedAiImagePolishResponse;
import com.base.ai.image.dedicated.DedicatedAiImageAdapter;
import com.base.ai.image.dedicated.DedicatedAiImageAdapterSelector;
import com.base.ai.image.dedicated.DedicatedAiImageContext;
import com.base.ai.image.dedicated.DedicatedAiImageItem;
import com.base.ai.image.dedicated.DedicatedAiImageResult;
import com.base.ai.service.AiConfigProvider;
import com.base.ai.service.DedicatedAiImageService;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 作图专用服务实现。
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DedicatedAiImageServiceImpl implements DedicatedAiImageService {

    private static final int DEFAULT_COUNT = 1;

    private static final int MAX_COUNT = 8;

    private static final String DEFAULT_SIZE = "1024x1024";

    private static final String DEFAULT_OPENAI_IMAGE_MODEL = "gpt-image-1";

    private static final String DEFAULT_DASHSCOPE_IMAGE_MODEL = "wanx2.1-t2i-turbo";

    private static final String DEFAULT_SENSENOVA_IMAGE_MODEL = "sensenova-u1-fast";

    private final AiConfigProvider aiConfigProvider;

    private final AiChatModelHolder chatModelHolder;

    private final DedicatedAiImageAdapterSelector adapterSelector;

    @Override
    public DedicatedAiImageGenerateResponse generate(DedicatedAiImageGenerateRequest request) {
        if (!aiConfigProvider.isConfigured()) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }
        DedicatedAiImageContext context = buildContext(request);
        DedicatedAiImageAdapter adapter = adapterSelector.select(context);
        log.info("AI 作图专用适配器已选择，adapter: {}，model: {}，size: {}，count: {}，baseUrl: {}",
                adapter.getName(), context.getModel(), context.getSize(), context.getCount(), context.getBaseUrl());
        DedicatedAiImageResult result = adapter.generate(context);
        return convertResponse(result);
    }

    @Override
    public DedicatedAiImagePolishResponse polish(DedicatedAiImagePolishRequest request) {
        if (request == null || !StringUtils.hasText(request.getPrompt())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "图片描述不能为空");
        }
        if (!aiConfigProvider.isConfigured()) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }
        ChatLanguageModel model = chatModelHolder.getModel(aiConfigProvider);
        if (model == null) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }

        String prompt = request.getPrompt().trim();
        List<ChatMessage> messages = buildPolishMessages(prompt, request);
        long start = System.currentTimeMillis();
        try {
            Response<AiMessage> response = model.generate(messages);
            long cost = System.currentTimeMillis() - start;
            String text = response != null && response.content() != null ? response.content().text() : "";
            DedicatedAiImagePolishResponse polishResponse = new DedicatedAiImagePolishResponse();
            polishResponse.setPrompt(prompt);
            polishResponse.setPolishedPrompt(cleanPolishedPrompt(text));
            log.info("AI 作图提示词润色成功，耗时 {} ms", cost);
            return polishResponse;
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.warn("AI 作图提示词润色失败，耗时 {} ms，原因: {}", cost, e.getMessage());
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "AI 提示词润色暂时不可用: " + (e.getMessage() != null ? e.getMessage() : "请求失败"));
        }
    }

    private List<ChatMessage> buildPolishMessages(String prompt, DedicatedAiImagePolishRequest request) {
        List<ChatMessage> messages = new ArrayList<>();
        String systemContent = "你是专业 AI 图片提示词设计师，只负责润色用户给出的图片描述。"
                + "输出一段可直接用于文生图的中文提示词，不要执行图片生成，不要返回 Markdown，不要解释。"
                + "保留原始主体和意图，补充主体细节、环境、构图、镜头、光线、材质、色彩、画面层次和质感。"
                + "不要包含“生成、绘制、画、制作、出图、请”等命令式表达。";
        StringBuilder userContent = new StringBuilder();
        userContent.append("原始图片描述：").append(prompt);
        if (StringUtils.hasText(request.getStyle())) {
            userContent.append("\n风格参考：").append(request.getStyle().trim());
        }
        if (StringUtils.hasText(request.getSize())) {
            userContent.append("\n画面尺寸：").append(request.getSize().trim());
        }
        userContent.append("\n请输出润色后的单段提示词。");
        messages.add(new SystemMessage(systemContent));
        messages.add(new UserMessage(userContent.toString()));
        return messages;
    }

    private String cleanPolishedPrompt(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String text = value.trim();
        text = text.replace("```text", "").replace("```markdown", "").replace("```", "").trim();
        String[] prefixes = {"润色后的提示词：", "润色后提示词：", "提示词：", "输出："};
        for (String prefix : prefixes) {
            if (text.startsWith(prefix)) {
                text = text.substring(prefix.length()).trim();
            }
        }
        return text;
    }

    private DedicatedAiImageContext buildContext(DedicatedAiImageGenerateRequest request) {
        if (request == null || !StringUtils.hasText(request.getPrompt())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "图片提示词不能为空");
        }
        String baseUrl = aiConfigProvider.getImageBaseUrl();
        String adapter = trimToNull(request.getAdapter());
        if (!StringUtils.hasText(adapter)) {
            adapter = trimToNull(aiConfigProvider.getImageAdapter());
        }

        DedicatedAiImageContext context = new DedicatedAiImageContext();
        context.setOriginalPrompt(request.getPrompt().trim());
        context.setNegativePrompt(trimToNull(request.getNegativePrompt()));
        context.setStyle(trimToNull(request.getStyle()));
        context.setSize(resolveSize(request.getSize()));
        context.setCount(resolveCount(request.getCount()));
        context.setModel(resolveModel(request.getModel(), baseUrl, adapter));
        context.setAdapter(adapter);
        context.setBaseUrl(baseUrl);
        context.setApiKey(aiConfigProvider.getApiKey());
        context.setTimeout(aiConfigProvider.getTimeout());
        context.setSeed(request.getSeed());
        context.setPrompt(buildGenerationPrompt(context));
        if (!StringUtils.hasText(context.getModel())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "图片生成模型不能为空，请在 AI 配置中填写供应商实际支持的出图模型名称");
        }
        return context;
    }

    private String buildGenerationPrompt(DedicatedAiImageContext context) {
        StringBuilder sb = new StringBuilder(context.getOriginalPrompt());
        if (StringUtils.hasText(context.getStyle())) {
            sb.append("\n风格要求：").append(context.getStyle());
        }
        if (StringUtils.hasText(context.getNegativePrompt())) {
            sb.append("\n避免出现：").append(context.getNegativePrompt());
        }
        return sb.toString();
    }

    private String resolveModel(String requestModel, String baseUrl, String adapter) {
        if (StringUtils.hasText(requestModel)) {
            return requestModel.trim();
        }
        String configuredModel = aiConfigProvider.getImageModel();
        if (StringUtils.hasText(configuredModel)) {
            return configuredModel.trim();
        }
        String normalizedBaseUrl = baseUrl == null ? "" : baseUrl.toLowerCase();
        String normalizedAdapter = adapter == null ? "" : adapter.toLowerCase();
        if (normalizedBaseUrl.contains("sensenova")
                || "sensenova".equals(normalizedAdapter)
                || "sense-nova".equals(normalizedAdapter)) {
            return DEFAULT_SENSENOVA_IMAGE_MODEL;
        }
        if (normalizedBaseUrl.contains("dashscope")) {
            return DEFAULT_DASHSCOPE_IMAGE_MODEL;
        }
        return DEFAULT_OPENAI_IMAGE_MODEL;
    }

    private String resolveSize(String size) {
        if (!StringUtils.hasText(size)) {
            return DEFAULT_SIZE;
        }
        return size.trim().toLowerCase();
    }

    private Integer resolveCount(Integer count) {
        if (count == null) {
            return DEFAULT_COUNT;
        }
        if (count < 1 || count > MAX_COUNT) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "图片数量必须在 1~8 之间");
        }
        return count;
    }

    private DedicatedAiImageGenerateResponse convertResponse(DedicatedAiImageResult result) {
        DedicatedAiImageGenerateResponse response = new DedicatedAiImageGenerateResponse();
        response.setPrompt(result.getPrompt());
        response.setModel(result.getModel());
        response.setAdapter(result.getAdapterName());
        response.setSize(result.getSize());
        response.setCount(result.getCount());
        response.setRawText(result.getRawText());
        response.setImages(convertImages(result.getImages()));
        return response;
    }

    private List<DedicatedAiImageItemResponse> convertImages(List<DedicatedAiImageItem> images) {
        List<DedicatedAiImageItemResponse> responses = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            return responses;
        }
        for (DedicatedAiImageItem image : images) {
            DedicatedAiImageItemResponse response = new DedicatedAiImageItemResponse();
            response.setIndex(image.getIndex());
            response.setUrl(image.getUrl());
            response.setBase64(image.getBase64());
            response.setMimeType(image.getMimeType());
            response.setRevisedPrompt(image.getRevisedPrompt());
            responses.add(response);
        }
        return responses;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
