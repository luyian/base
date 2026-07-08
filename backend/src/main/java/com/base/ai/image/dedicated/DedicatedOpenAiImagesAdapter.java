package com.base.ai.image.dedicated;

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
 * AI 作图专用 OpenAI Images 适配器。
 *
 * @author base
 */
@Slf4j
@Order(20)
@Component
public class DedicatedOpenAiImagesAdapter extends AbstractDedicatedAiImageAdapter {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private static final String IMAGES_GENERATIONS_PATH = "/images/generations";

    @Override
    public boolean supports(DedicatedAiImageContext context) {
        if (StringUtils.hasText(context.getAdapter())) {
            return "openai-images".equalsIgnoreCase(context.getAdapter())
                    || "images".equalsIgnoreCase(context.getAdapter());
        }
        String baseUrl = context.getBaseUrl() == null ? "" : context.getBaseUrl().toLowerCase();
        return !baseUrl.contains("sensenova");
    }

    @Override
    public DedicatedAiImageResult generate(DedicatedAiImageContext context) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", context.getModel());
        body.put("prompt", context.getPrompt());
        body.put("n", context.getCount());
        body.put("size", context.getSize());

        String url = buildImagesUrl(context.getBaseUrl());
        log.info("AI 作图专用请求开始，adapter: {}，model: {}，size: {}，count: {}，url: {}", getName(),
                context.getModel(), context.getSize(), context.getCount(), url);
        String response = postImageRequest(url, body, context);
        return parseResponse(context, response);
    }

    @Override
    public String getName() {
        return "openai-images";
    }

    private String postImageRequest(String url, Map<String, Object> body, DedicatedAiImageContext context) {
        try {
            return HttpClientUtil.post(url, body, buildHeaders(context), imageTimeout(context));
        } catch (RuntimeException e) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成请求失败，请确认图片模型名称和接口地址可用，模型："
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
        if (!hasImages(result)) {
            fillImagesFromJson(result, resultObject);
        }
        if (!hasImages(result)) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成接口未返回可展示图片，响应摘要：" + abbreviate(response));
        }
        return result;
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
