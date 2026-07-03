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
 * OpenAI 兼容图片接口适配器。
 *
 * @author base
 */
@Slf4j
@Order(20)
@Component
public class OpenAiImagesImageAdapter extends AbstractAiImageAdapter {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private static final String IMAGES_GENERATIONS_PATH = "/images/generations";

    @Override
    public boolean supports(AiImageContext context) {
        if (StringUtils.hasText(context.getAdapter())) {
            return "openai-images".equalsIgnoreCase(context.getAdapter())
                    || "images".equalsIgnoreCase(context.getAdapter());
        }
        String baseUrl = context.getBaseUrl() == null ? "" : context.getBaseUrl().toLowerCase();
        return !baseUrl.contains("sensenova");
    }

    @Override
    public AiImageResult generate(AiImageContext context) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", context.getModel());
        body.put("prompt", context.getPrompt());
        body.put("n", 1);
        body.put("size", context.getSize());

        String url = buildImagesUrl(context.getBaseUrl());
        log.info("AI 图片生成请求开始，adapter: {}, model: {}, size: {}, url: {}", getName(), context.getModel(),
                context.getSize(), url);
        String response = postImageRequest(url, body, context);
        return parseResponse(context, response);
    }

    @Override
    public String getName() {
        return "openai-images";
    }

    private String postImageRequest(String url, Map<String, Object> body, AiImageContext context) {
        try {
            return HttpClientUtil.post(url, body, buildHeaders(context), imageTimeout(context));
        } catch (RuntimeException e) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成请求失败，请确认图片模型名称和接口地址可用，模型："
                            + context.getModel() + "，原因：" + e.getMessage());
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
                    "图片生成接口未返回可展示图片，响应摘要：" + abbreviate(response));
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
}
