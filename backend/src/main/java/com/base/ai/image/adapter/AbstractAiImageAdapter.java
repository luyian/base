package com.base.ai.image.adapter;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.base.ai.image.AiImageContext;
import com.base.ai.image.AiImageResult;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 图片生成适配器公共逻辑。
 *
 * @author base
 */
public abstract class AbstractAiImageAdapter implements AiImageAdapter {

    private static final Pattern MARKDOWN_IMAGE_PATTERN = Pattern.compile("!\\[[^]]*]\\(([^)]+)\\)");

    private static final Pattern HTTP_IMAGE_URL_PATTERN = Pattern.compile("https?://[^\\s)\"']+");

    private static final Pattern DATA_IMAGE_PATTERN = Pattern.compile("data:image/[^;]+;base64,[A-Za-z0-9+/=]+");

    protected Map<String, String> buildHeaders(AiImageContext context) {
        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", "Bearer " + normalizeApiKey(context.getApiKey()));
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private String normalizeApiKey(String apiKey) {
        if (apiKey == null) {
            return "";
        }
        String normalized = apiKey.trim();
        if ((normalized.startsWith("\"") && normalized.endsWith("\""))
                || (normalized.startsWith("'") && normalized.endsWith("'"))) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
        }
        return normalized;
    }

    protected int imageTimeout(AiImageContext context) {
        int timeout = context.getTimeout() != null ? context.getTimeout() : 60000;
        return Math.max(timeout, 120000);
    }

    protected AiImageResult buildBaseResult(AiImageContext context) {
        AiImageResult result = new AiImageResult();
        result.setModel(context.getModel());
        result.setSize(context.getSize());
        result.setPrompt(context.getPrompt());
        result.setAdapterName(getName());
        return result;
    }

    protected String readErrorMessage(JSONObject result) {
        Object error = result.get("error");
        if (error instanceof JSONObject) {
            String message = ((JSONObject) error).getString("message");
            return StringUtils.hasText(message) ? message : error.toString();
        }
        return error == null ? "未知错误" : String.valueOf(error);
    }

    protected void assertNoProviderError(JSONObject result) {
        if (result == null) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(), "图片生成接口返回为空");
        }
        if (result.containsKey("error")) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成失败：" + readErrorMessage(result));
        }
    }

    protected void fillImageFromText(AiImageResult result, String text) {
        if (!StringUtils.hasText(text)) {
            return;
        }
        result.setTextContent(text.trim());
        Matcher dataMatcher = DATA_IMAGE_PATTERN.matcher(text);
        if (dataMatcher.find()) {
            result.setImageBase64(stripDataImagePrefix(dataMatcher.group()));
            return;
        }
        Matcher markdownMatcher = MARKDOWN_IMAGE_PATTERN.matcher(text);
        if (markdownMatcher.find()) {
            result.setImageUrl(markdownMatcher.group(1).trim());
            return;
        }
        Matcher urlMatcher = HTTP_IMAGE_URL_PATTERN.matcher(text);
        if (urlMatcher.find()) {
            result.setImageUrl(urlMatcher.group());
        }
    }

    protected void fillImageFromJson(AiImageResult result, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof JSONObject) {
            fillImageFromObject(result, (JSONObject) value);
            return;
        }
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size(); i++) {
                fillImageFromJson(result, array.get(i));
                if (hasImage(result)) {
                    return;
                }
            }
            return;
        }
        fillImageFromText(result, String.valueOf(value));
    }

    protected boolean hasImage(AiImageResult result) {
        return StringUtils.hasText(result.getImageUrl()) || StringUtils.hasText(result.getImageBase64());
    }

    private void fillImageFromObject(AiImageResult result, JSONObject object) {
        String url = firstText(object, "url", "image_url", "imageUrl", "output_url", "origin_image_url");
        if (StringUtils.hasText(url)) {
            result.setImageUrl(url);
            return;
        }
        String base64 = firstText(object, "b64_json", "base64", "image_base64", "imageBase64");
        if (StringUtils.hasText(base64)) {
            result.setImageBase64(stripDataImagePrefix(base64));
            return;
        }
        Object imageUrlObject = object.get("image_url");
        if (imageUrlObject instanceof JSONObject) {
            fillImageFromJson(result, imageUrlObject);
            if (hasImage(result)) {
                return;
            }
        }
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            fillImageFromJson(result, entry.getValue());
            if (hasImage(result)) {
                return;
            }
        }
    }

    private String firstText(JSONObject object, String... keys) {
        for (String key : keys) {
            Object value = object.get(key);
            if (value instanceof JSONObject || value instanceof JSONArray) {
                continue;
            }
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value).trim();
            }
        }
        return null;
    }

    private String stripDataImagePrefix(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        int commaIndex = value.indexOf(',');
        if (value.startsWith("data:image/") && commaIndex > -1) {
            return value.substring(commaIndex + 1);
        }
        return value;
    }
}
