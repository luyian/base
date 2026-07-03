package com.base.ai.image;

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

    private String buildPrompt(String userMessage) {
        String prompt = userMessage == null ? "" : userMessage.trim();
        prompt = prompt.replaceFirst("^(请)?(帮我)?(生成|成|画|绘制|做|制作)(一张|一个|个)?"
                + "(图片|图|插画|海报|头像)?[:：，,\\s]*", "");
        prompt = prompt.replaceFirst("^(文生图|出图|AI作图|AI绘图)[:：，,\\s]*", "");
        prompt = MODEL_PATTERN.matcher(prompt).replaceAll("");
        prompt = SIZE_PATTERN.matcher(prompt).replaceAll("");
        return prompt.trim();
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
