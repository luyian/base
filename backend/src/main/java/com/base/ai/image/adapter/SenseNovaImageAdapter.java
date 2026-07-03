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
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", resolveModel(context));
        body.put("prompt", context.getPrompt());
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
}
