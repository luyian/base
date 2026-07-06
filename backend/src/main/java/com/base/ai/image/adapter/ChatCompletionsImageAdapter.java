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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Chat Completions 图片生成适配器。
 *
 * @author base
 */
@Slf4j
@Order(10)
@Component
public class ChatCompletionsImageAdapter extends AbstractAiImageAdapter {

    @Override
    public boolean supports(AiImageContext context) {
        if (StringUtils.hasText(context.getAdapter())) {
            return "chat-completions".equalsIgnoreCase(context.getAdapter())
                    || "chat".equalsIgnoreCase(context.getAdapter());
        }
        return false;
    }

    @Override
    public AiImageResult generate(AiImageContext context) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", context.getModel());
        body.put("messages", buildMessages(context));
        body.put("stream", false);

        String url = buildChatCompletionsUrl(context.getBaseUrl());
        log.info("AI 图片生成请求开始，adapter: {}, model: {}, size: {}", getName(), context.getModel(),
                context.getSize());
        String response = postImageRequest(url, body, context);
        return parseResponse(context, response);
    }

    @Override
    public String getName() {
        return "chat-completions";
    }

    private String postImageRequest(String url, Map<String, Object> body, AiImageContext context) {
        try {
            return HttpClientUtil.post(url, body, buildHeaders(context), imageTimeout(context));
        } catch (RuntimeException e) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成请求失败，请确认图片模型名称在当前供应商账号下可用，模型："
                            + context.getModel() + "，原因：" + e.getMessage());
        }
    }

    private JSONArray buildMessages(AiImageContext context) {
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是图片生成助手。请根据用户提示生成图片，返回可直接展示的图片链接、Markdown 图片"
                + "或结构化图片数据，不要只描述图片。");
        messages.add(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", buildUserContent(context));
        messages.add(userMessage);
        return messages;
    }

    private Object buildUserContent(AiImageContext context) {
        if (!StringUtils.hasText(context.getReferenceImageUrl())) {
            return buildImagePrompt(context);
        }

        JSONArray content = new JSONArray();
        JSONObject textPart = new JSONObject();
        textPart.put("type", "text");
        textPart.put("text", buildImagePrompt(context));
        content.add(textPart);

        JSONObject imageUrl = new JSONObject();
        imageUrl.put("url", context.getReferenceImageUrl());
        JSONObject imagePart = new JSONObject();
        imagePart.put("type", "image_url");
        imagePart.put("image_url", imageUrl);
        content.add(imagePart);
        return content;
    }

    private String buildImagePrompt(AiImageContext context) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(context.getReferenceImageUrl())) {
            sb.append("请基于随请求提供的参考图片生成调整后的图片。\n");
        } else {
            sb.append("请生成一张图片。\n");
        }
        sb.append("图片提示词：").append(context.getPrompt()).append("\n");
        sb.append("图片尺寸：").append(context.getSize()).append("\n");
        sb.append("请返回图片本身或可访问的图片 URL/Markdown 图片。");
        return sb.toString();
    }

    private AiImageResult parseResponse(AiImageContext context, String response) {
        JSONObject resultObject = JSONObject.parseObject(response);
        assertNoProviderError(resultObject);

        AiImageResult result = buildBaseResult(context);
        fillImageFromJson(result, resultObject.get("data"));
        fillImageFromJson(result, resultObject.get("output"));
        fillImageFromJson(result, resultObject.get("images"));

        JSONArray choices = resultObject.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice == null ? null : firstChoice.getJSONObject("message");
            if (message != null) {
                Object content = message.get("content");
                if (content instanceof String) {
                    fillImageFromText(result, (String) content);
                } else {
                    fillImageFromJson(result, content);
                }
            }
        }
        if (!hasImage(result)) {
            String content = StringUtils.hasText(result.getTextContent()) ? "，原始返回：" + result.getTextContent() : "";
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成接口未返回图片，请确认图片模型是出图模型" + content);
        }
        return result;
    }

    private String buildChatCompletionsUrl(String baseUrl) {
        String url = baseUrl == null ? "" : baseUrl.trim();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.endsWith("/chat/completions")) {
            return url;
        }
        if (url.endsWith("/v1")) {
            return url + "/chat/completions";
        }
        return url + "/v1/chat/completions";
    }
}
