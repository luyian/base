package com.base.ai.image;

import com.base.ai.dto.ChatRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 图片模型、尺寸和提示词解析器。
 *
 * @author base
 */
@Component
public class AiImageModelResolver {

    private static final String DEFAULT_OPENAI_IMAGE_MODEL = "gpt-image-1";

    private static final String DEFAULT_DASHSCOPE_IMAGE_MODEL = "wanx2.1-t2i-turbo";

    private static final String DEFAULT_SENSENOVA_IMAGE_MODEL = "sensenova-u1-fast";

    private static final String DEFAULT_SENSENOVA_IMAGE_SIZE = "2048x2048";

    private static final String DEFAULT_IMAGE_SIZE = "1024x1024";

    private static final Pattern MODEL_PATTERN = Pattern.compile("(?:模型|model)[:：]?\\s*([A-Za-z0-9_.:-]+)");

    private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d{3,4})\\s*[xX*×]\\s*(\\d{3,4})");

    /**
     * 构建图片生成上下文。
     *
     * @param userMessage 用户原始输入
     * @param configuredModel 当前图片模型配置
     * @param baseUrl API 基础地址
     * @param adapter 图片生成适配器
     * @param apiKey API Key
     * @param timeout 超时时间
     * @return 图片生成上下文
     */
    public AiImageContext resolve(String userMessage, String configuredModel, String baseUrl, String adapter,
                                  String apiKey, Integer timeout) {
        AiImageContext context = new AiImageContext();
        context.setUserMessage(userMessage);
        context.setPrompt(buildPrompt(userMessage));
        context.setModel(resolveImageModel(userMessage, configuredModel, baseUrl));
        context.setSize(resolveImageSize(userMessage, baseUrl, adapter));
        context.setBaseUrl(baseUrl);
        context.setAdapter(adapter);
        context.setApiKey(apiKey);
        context.setTimeout(timeout);
        return context;
    }

    /**
     * 构建图片生成或调整上下文。
     *
     * @param request 用户请求
     * @param configuredModel 当前图片模型配置
     * @param baseUrl API 基础地址
     * @param adapter 图片生成适配器
     * @param apiKey API Key
     * @param timeout 超时时间
     * @return 图片生成上下文
     */
    public AiImageContext resolve(ChatRequest request, String configuredModel, String baseUrl, String adapter,
                                  String apiKey, Integer timeout) {
        String userMessage = request == null ? null : request.getMessage();
        AiImageContext context = resolve(userMessage, configuredModel, baseUrl, adapter, apiKey, timeout);
        if (request != null && StringUtils.hasText(request.getImageSize())) {
            context.setSize(request.getImageSize().trim().toLowerCase());
        }
        if (request == null || !StringUtils.hasText(request.getReferenceImageUrl())) {
            return context;
        }

        String editInstruction = buildPrompt(userMessage);
        context.setEditInstruction(editInstruction);
        context.setReferenceImageUrl(request.getReferenceImageUrl().trim());
        context.setReferenceImagePrompt(trimToNull(request.getReferenceImagePrompt()));
        context.setReferenceImageRevisedPrompt(trimToNull(request.getReferenceImageRevisedPrompt()));
        context.setPrompt(buildEditPrompt(context));
        return context;
    }

    private String buildPrompt(String userMessage) {
        String prompt = userMessage == null ? "" : userMessage.trim();
        prompt = prompt.replaceFirst("^(请)?(帮我)?(生成|成|画|绘制|做|制作)(一张|一个|个)?"
                + "(图片|图|插画|海报|头像)?[:：，,\\s]*", "");
        prompt = prompt.replaceFirst("^(请)?(帮我)?(调整|修改|改|优化|重新生成|重画|基于上一张|基于上图)"
                + "(一下|下)?(这张|这个|上一张|上图|图片|图)?[:：，,\\s]*", "");
        prompt = prompt.replaceFirst("^(文生图|出图|AI作图|AI绘图)[:：，,\\s]*", "");
        prompt = MODEL_PATTERN.matcher(prompt).replaceAll("");
        prompt = SIZE_PATTERN.matcher(prompt).replaceAll("");
        return prompt.trim();
    }

    private String buildEditPrompt(AiImageContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("基于参考图片继续生成一张调整后的图片。");
        sb.append("保持参考图片的主体、构图、视觉风格、人物一致性和关键元素不变，只应用新的调整要求。");
        if (StringUtils.hasText(context.getReferenceImageRevisedPrompt())) {
            sb.append("\n参考图片优化提示词：").append(context.getReferenceImageRevisedPrompt());
        } else if (StringUtils.hasText(context.getReferenceImagePrompt())) {
            sb.append("\n参考图片原始提示词：").append(context.getReferenceImagePrompt());
        }
        if (StringUtils.hasText(context.getReferenceImageUrl())) {
            sb.append("\n参考图片地址：").append(context.getReferenceImageUrl());
        }
        if (StringUtils.hasText(context.getEditInstruction())) {
            sb.append("\n新的调整要求：").append(context.getEditInstruction());
        }
        return sb.toString();
    }

    private String trimToNull(String text) {
        return StringUtils.hasText(text) ? text.trim() : null;
    }

    private String resolveImageModel(String userMessage, String configuredModel, String baseUrl) {
        Matcher matcher = MODEL_PATTERN.matcher(userMessage == null ? "" : userMessage);
        if (matcher.find() && StringUtils.hasText(matcher.group(1))) {
            return matcher.group(1).trim();
        }
        String normalizedBaseUrl = baseUrl == null ? "" : baseUrl.toLowerCase();
        if (StringUtils.hasText(configuredModel)) {
            return configuredModel;
        }
        if (normalizedBaseUrl.contains("sensenova")) {
            return DEFAULT_SENSENOVA_IMAGE_MODEL;
        }
        if (normalizedBaseUrl.contains("dashscope")) {
            return DEFAULT_DASHSCOPE_IMAGE_MODEL;
        }
        return DEFAULT_OPENAI_IMAGE_MODEL;
    }

    private String resolveImageSize(String userMessage, String baseUrl, String adapter) {
        String message = userMessage == null ? "" : userMessage;
        Matcher matcher = SIZE_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1) + "x" + matcher.group(2);
        }
        boolean sensenova = isSenseNovaProvider(baseUrl, adapter);
        if (message.contains("横图") || message.contains("横版")) {
            return sensenova ? "2752x1536" : "1536x1024";
        }
        if (message.contains("竖图") || message.contains("竖版") || message.contains("海报")) {
            return sensenova ? "1536x2752" : "1024x1536";
        }
        return sensenova ? DEFAULT_SENSENOVA_IMAGE_SIZE : DEFAULT_IMAGE_SIZE;
    }

    private boolean isSenseNovaProvider(String baseUrl, String adapter) {
        String normalizedBaseUrl = baseUrl == null ? "" : baseUrl.toLowerCase();
        String normalizedAdapter = adapter == null ? "" : adapter.trim().toLowerCase();
        return normalizedBaseUrl.contains("sensenova")
                || "sensenova".equals(normalizedAdapter)
                || "sense-nova".equals(normalizedAdapter);
    }
}
