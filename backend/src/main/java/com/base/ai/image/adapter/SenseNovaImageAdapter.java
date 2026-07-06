package com.base.ai.image.adapter;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.base.ai.image.AiImageContext;
import com.base.ai.image.AiImageResult;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.common.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 商汤日日新 SenseNova 图片生成适配器。
 * <p>
 * 文档：https://platform.sensenova.cn/docs
 * 接口：POST https://token.sensenova.cn/v1/images/generations
 *
 * @author base
 */
@Slf4j
@Order(15)
@Component
public class SenseNovaImageAdapter extends AbstractAiImageAdapter {

    private static final String DEFAULT_BASE_URL = "https://token.sensenova.cn/v1";

    private static final String DEFAULT_MODEL = "sensenova-u1-fast";

    private static final String VISION_PROMPT_MODEL = "sensenova-6.7-flash-lite";

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private static final String IMAGES_GENERATIONS_PATH = "/images/generations";

    private static final String DEFAULT_SIZE = "2048x2048";

    private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d{3,4})\\s*[xX*×]\\s*(\\d{3,4})");

    private static final Set<String> ALLOWED_SIZES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "1664x2496", "2496x1664", "1760x2368", "2368x1760", "1824x2272", "2272x1824",
            "2048x2048", "2752x1536", "1536x2752", "3072x1376", "1344x3136", "2560x720", "3072x864"
    )));

    @Override
    public boolean supports(AiImageContext context) {
        if (StringUtils.hasText(context.getAdapter())) {
            return "sensenova".equalsIgnoreCase(context.getAdapter())
                    || "sense-nova".equalsIgnoreCase(context.getAdapter());
        }
        String baseUrl = context.getBaseUrl() == null ? "" : context.getBaseUrl().toLowerCase();
        return baseUrl.contains("sensenova");
    }

    @Override
    public AiImageResult generate(AiImageContext context) {
        String prompt = resolveGenerationPrompt(context);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", resolveModel(context));
        body.put("prompt", prompt);
        body.put("n", 1);
        body.put("size", resolveSize(context.getSize()));

        String url = buildImagesUrl(context.getBaseUrl());
        log.info("AI 图片生成请求开始，adapter: {}, model: {}, requestSize: {}, apiSize: {}, url: {}",
                getName(), body.get("model"), context.getSize(), body.get("size"), url);
        String response = postImageRequest(url, body, context);
        return parseResponse(context, response);
    }

    @Override
    public String getName() {
        return "sensenova";
    }

    private String resolveModel(AiImageContext context) {
        if (StringUtils.hasText(context.getModel())) {
            return context.getModel().trim();
        }
        return DEFAULT_MODEL;
    }

    private String resolveSize(String size) {
        if (!StringUtils.hasText(size)) {
            return DEFAULT_SIZE;
        }
        String normalized = size.trim().toLowerCase().replace('*', 'x');
        if (ALLOWED_SIZES.contains(normalized)) {
            return normalized;
        }
        String mapped = mapToAllowedSize(normalized);
        log.info("商汤图片尺寸 {} 不在支持列表，已映射为 {}", size, mapped);
        return mapped;
    }

    private String mapToAllowedSize(String size) {
        Matcher matcher = SIZE_PATTERN.matcher(size);
        if (!matcher.find()) {
            return DEFAULT_SIZE;
        }
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        if (height == 0) {
            return DEFAULT_SIZE;
        }
        double ratio = (double) width / height;
        String best = DEFAULT_SIZE;
        double bestDiff = Double.MAX_VALUE;
        for (String allowed : ALLOWED_SIZES) {
            String[] parts = allowed.split("x");
            double allowedRatio = Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
            double diff = Math.abs(allowedRatio - ratio);
            if (diff < bestDiff) {
                bestDiff = diff;
                best = allowed;
            }
        }
        return best;
    }

    private String resolveGenerationPrompt(AiImageContext context) {
        if (!StringUtils.hasText(context.getReferenceImageUrl())) {
            return context.getPrompt();
        }
        String rewrittenPrompt = rewritePromptWithReferenceImage(context);
        context.setPrompt(rewrittenPrompt);
        return rewrittenPrompt;
    }

    private String rewritePromptWithReferenceImage(AiImageContext context) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", VISION_PROMPT_MODEL);
        body.put("messages", buildPromptRewriteMessages(context));
        body.put("n", 1);
        body.put("stream", false);
        body.put("max_tokens", 1000);
        body.put("reasoning_effort", "none");

        String url = buildChatCompletionsUrl(context.getBaseUrl());
        String response = postPromptRewriteRequest(url, body, context);
        String prompt = parsePromptRewriteResponse(response);
        log.info("商汤参考图调整提示词已生成，model: {}，prompt: {}", VISION_PROMPT_MODEL, prompt);
        return prompt;
    }

    private JSONArray buildPromptRewriteMessages(AiImageContext context) {
        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是图片生成提示词改写助手。请客观观察参考图，并结合用户调整要求，"
                + "只输出一段可直接交给文生图模型使用的中文提示词。不要解释，不要输出 Markdown。");
        messages.add(systemMessage);

        JSONArray userContent = new JSONArray();
        JSONObject textPart = new JSONObject();
        textPart.put("type", "text");
        textPart.put("text", buildPromptRewriteInstruction(context));
        userContent.add(textPart);

        JSONObject imageUrl = new JSONObject();
        imageUrl.put("url", context.getReferenceImageUrl());
        JSONObject imagePart = new JSONObject();
        imagePart.put("type", "image_url");
        imagePart.put("image_url", imageUrl);
        userContent.add(imagePart);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", userContent);
        messages.add(userMessage);
        return messages;
    }

    private String buildPromptRewriteInstruction(AiImageContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("请根据随请求提供的参考图，生成一段新的文生图提示词。\n");
        sb.append("要求保留参考图中可见的主体、人物数量、位置关系、构图、场景、动作、光线和整体风格，");
        sb.append("只应用新的调整要求。\n");
        if (StringUtils.hasText(context.getReferenceImageRevisedPrompt())) {
            sb.append("参考图上一轮优化提示词：").append(context.getReferenceImageRevisedPrompt()).append("\n");
        } else if (StringUtils.hasText(context.getReferenceImagePrompt())) {
            sb.append("参考图上一轮原始提示词：").append(context.getReferenceImagePrompt()).append("\n");
        }
        if (StringUtils.hasText(context.getEditInstruction())) {
            sb.append("新的调整要求：").append(context.getEditInstruction()).append("\n");
        }
        sb.append("目标图片尺寸：").append(context.getSize()).append("\n");
        sb.append("输出格式：只输出最终提示词，不要附加说明。");
        return sb.toString();
    }

    private String postPromptRewriteRequest(String url, Map<String, Object> body, AiImageContext context) {
        try {
            return HttpClientUtil.post(url, body, buildHeaders(context), imageTimeout(context));
        } catch (RuntimeException e) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "商汤参考图读取失败，无法基于上一张图片调整。请确认参考图片链接未过期且可访问，原因为：" + e.getMessage());
        }
    }

    private String parsePromptRewriteResponse(String response) {
        JSONObject resultObject = JSONObject.parseObject(response);
        assertNoProviderError(resultObject);

        JSONArray choices = resultObject.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "商汤参考图读取未返回提示词，响应摘要：" + abbreviate(response));
        }
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice == null ? null : firstChoice.getJSONObject("message");
        Object content = message == null ? null : message.get("content");
        String prompt = extractTextContent(content);
        if (!StringUtils.hasText(prompt)) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "商汤参考图读取未返回可用提示词，响应摘要：" + abbreviate(response));
        }
        return normalizeGeneratedPrompt(prompt);
    }

    private String extractTextContent(Object content) {
        if (content instanceof String) {
            return (String) content;
        }
        if (!(content instanceof JSONArray)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        JSONArray array = (JSONArray) content;
        for (int i = 0; i < array.size(); i++) {
            Object item = array.get(i);
            if (item instanceof JSONObject) {
                String text = ((JSONObject) item).getString("text");
                if (StringUtils.hasText(text)) {
                    sb.append(text).append("\n");
                }
            }
        }
        return sb.toString();
    }

    private String normalizeGeneratedPrompt(String prompt) {
        String text = prompt.trim();
        text = text.replaceAll("^```[A-Za-z]*", "").replaceAll("```$", "").trim();
        text = text.replaceFirst("^(最终提示词|提示词|文生图提示词)[:：]\\s*", "").trim();
        return text.length() <= 4000 ? text : text.substring(0, 4000);
    }

    private String postImageRequest(String url, Map<String, Object> body, AiImageContext context) {
        try {
            return HttpClientUtil.post(url, body, buildHeaders(context), imageTimeout(context));
        } catch (RuntimeException e) {
            String message = e.getMessage() == null ? "" : e.getMessage();
            if (message.contains("401") || message.contains("403")) {
                throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                        "商汤图片生成鉴权失败，请确认 API Key 来自 Token Plan 控制台（https://platform.sensenova.cn/token-plan），"
                                + "且基础地址为 https://token.sensenova.cn/v1，模型为 sensenova-u1-fast。"
                                + "原始错误：" + message);
            }
            if (message.contains("400") && message.contains("size")) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                        "商汤图片尺寸不受支持，请使用 2048x2048、2752x1536、1536x2752 等官方尺寸。"
                                + "原始错误：" + message);
            }
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "商汤图片生成请求失败，请确认模型名称和接口地址可用，模型："
                            + body.get("model") + "，原因：" + message);
        }
    }

    private AiImageResult parseResponse(AiImageContext context, String response) {
        JSONObject resultObject = JSONObject.parseObject(response);
        assertNoProviderError(resultObject);

        AiImageResult result = buildBaseResult(context);
        fillImageFromJson(result, resultObject.get("data"));
        fillImageFromJson(result, resultObject.get("output"));
        fillImageFromJson(result, resultObject.get("images"));
        if (!hasImage(result)) {
            fillImageFromJson(result, resultObject);
        }
        fillRevisedPrompt(result, resultObject);
        if (!hasImage(result)) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "商汤图片生成接口未返回可展示图片，响应摘要：" + abbreviate(response));
        }
        return result;
    }

    private void fillRevisedPrompt(AiImageResult result, JSONObject resultObject) {
        JSONArray data = resultObject.getJSONArray("data");
        if (data == null || data.isEmpty() || !(data.get(0) instanceof JSONObject)) {
            return;
        }
        JSONObject firstImage = data.getJSONObject(0);
        result.setRevisedPrompt(firstImage.getString("revised_prompt"));
    }

    private String abbreviate(String response) {
        if (!StringUtils.hasText(response)) {
            return "空响应";
        }
        String text = response.replaceAll("\\s+", " ").trim();
        return text.length() <= 500 ? text : text.substring(0, 500) + "...";
    }

    private String buildImagesUrl(String baseUrl) {
        String url = baseUrl == null ? "" : baseUrl.trim();
        if (!StringUtils.hasText(url)) {
            return DEFAULT_BASE_URL + IMAGES_GENERATIONS_PATH;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.endsWith(IMAGES_GENERATIONS_PATH)) {
            return url;
        }
        if (url.endsWith(CHAT_COMPLETIONS_PATH)) {
            return url.substring(0, url.length() - CHAT_COMPLETIONS_PATH.length()) + IMAGES_GENERATIONS_PATH;
        }
        if (url.endsWith("/v1")) {
            return url + IMAGES_GENERATIONS_PATH;
        }
        return url + "/v1" + IMAGES_GENERATIONS_PATH;
    }

    private String buildChatCompletionsUrl(String baseUrl) {
        String url = baseUrl == null ? "" : baseUrl.trim();
        if (!StringUtils.hasText(url)) {
            return DEFAULT_BASE_URL + CHAT_COMPLETIONS_PATH;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.endsWith(CHAT_COMPLETIONS_PATH)) {
            return url;
        }
        if (url.endsWith(IMAGES_GENERATIONS_PATH)) {
            return url.substring(0, url.length() - IMAGES_GENERATIONS_PATH.length()) + CHAT_COMPLETIONS_PATH;
        }
        if (url.endsWith("/v1")) {
            return url + CHAT_COMPLETIONS_PATH;
        }
        return url + "/v1" + CHAT_COMPLETIONS_PATH;
    }
}
