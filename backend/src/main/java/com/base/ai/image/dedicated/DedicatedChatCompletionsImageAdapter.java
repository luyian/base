package com.base.ai.image.dedicated;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
 * AI 作图专用 Chat Completions 适配器。
 *
 * @author base
 */
@Slf4j
@Order(10)
@Component
public class DedicatedChatCompletionsImageAdapter extends AbstractDedicatedAiImageAdapter {

    @Override
    public boolean supports(DedicatedAiImageContext context) {
        if (StringUtils.hasText(context.getAdapter())) {
            return "chat-completions".equalsIgnoreCase(context.getAdapter())
                    || "chat".equalsIgnoreCase(context.getAdapter());
        }
        return false;
    }

    @Override
    public DedicatedAiImageResult generate(DedicatedAiImageContext context) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", context.getModel());
        body.put("messages", buildMessages(context));
        body.put("stream", false);

        String url = buildChatCompletionsUrl(context.getBaseUrl());
        log.info("AI 作图专用请求开始，adapter: {}，model: {}，size: {}，count: {}", getName(),
                context.getModel(), context.getSize(), context.getCount());
        String response = postImageRequest(url, body, context);
        return parseResponse(context, response);
    }

    @Override
    public String getName() {
        return "chat-completions";
    }

    private JSONArray buildMessages(DedicatedAiImageContext context) {
        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是图片生成助手。请根据用户提示生成图片，返回可直接展示的图片链接、"
                + "Markdown 图片或 JSON 图片数组，不要只描述图片。");
        messages.add(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", buildImagePrompt(context));
        messages.add(userMessage);
        return messages;
    }

    private String buildImagePrompt(DedicatedAiImageContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("请生成 ").append(context.getCount()).append(" 张图片。\n");
        sb.append("图片提示词：").append(context.getPrompt()).append("\n");
        sb.append("图片尺寸：").append(context.getSize()).append("\n");
        if (context.getSeed() != null) {
            sb.append("随机种子：").append(context.getSeed()).append("\n");
        }
        sb.append("请返回图片本身或可访问的图片 URL/Markdown 图片；如返回 JSON，请使用 images 数组。");
        return sb.toString();
    }

    private String postImageRequest(String url, Map<String, Object> body, DedicatedAiImageContext context) {
        try {
            return HttpClientUtil.post(url, body, buildHeaders(context), imageTimeout(context));
        } catch (RuntimeException e) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成请求失败，请确认图片模型名称在当前供应商账号下可用，模型："
                            + context.getModel() + "，原因：" + e.getMessage());
        }
    }

    private DedicatedAiImageResult parseResponse(DedicatedAiImageContext context, String response) {
        JSONObject resultObject = JSONObject.parseObject(response);
        assertNoProviderError(resultObject);

        DedicatedAiImageResult result = buildBaseResult(context);
        fillImagesFromJson(result, resultObject.get("data"));
        fillImagesFromJson(result, resultObject.get("output"));
        fillImagesFromJson(result, resultObject.get("images"));

        JSONArray choices = resultObject.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            for (int i = 0; i < choices.size() && !isEnoughImages(result); i++) {
                JSONObject choice = choices.getJSONObject(i);
                JSONObject message = choice == null ? null : choice.getJSONObject("message");
                if (message == null) {
                    continue;
                }
                Object content = message.get("content");
                if (content instanceof String) {
                    fillImagesFromText(result, (String) content);
                } else {
                    fillImagesFromJson(result, content);
                }
            }
        }
        if (!hasImages(result)) {
            String content = StringUtils.hasText(result.getRawText()) ? "，原始返回：" + result.getRawText() : "";
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成接口未返回图片，请确认图片模型是出图模型" + content);
        }
        return result;
    }

    private boolean isEnoughImages(DedicatedAiImageResult result) {
        int count = result.getCount() == null ? 1 : result.getCount();
        return result.getImages().size() >= count;
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
