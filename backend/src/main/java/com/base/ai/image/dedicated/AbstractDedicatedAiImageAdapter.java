package com.base.ai.image.dedicated;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 作图专用适配器公共逻辑。
 *
 * @author base
 */
public abstract class AbstractDedicatedAiImageAdapter implements DedicatedAiImageAdapter {

    private static final int MAX_RESPONSE_SUMMARY_LENGTH = 500;

    private static final Pattern MARKDOWN_IMAGE_PATTERN = Pattern.compile("!\\[[^]]*]\\(([^)]+)\\)");

    private static final Pattern HTTP_IMAGE_URL_PATTERN = Pattern.compile("https?://[^\\s)\"']+");

    private static final Pattern DATA_IMAGE_PATTERN = Pattern.compile("data:image/([^;]+);base64,([A-Za-z0-9+/=]+)");

    /**
     * 构造请求头。
     *
     * @param context 作图上下文
     * @return 请求头
     */
    protected Map<String, String> buildHeaders(DedicatedAiImageContext context) {
        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", "Bearer " + normalizeApiKey(context.getApiKey()));
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    /**
     * 图片请求超时时间。
     *
     * @param context 作图上下文
     * @return 超时时间
     */
    protected int imageTimeout(DedicatedAiImageContext context) {
        int timeout = context.getTimeout() != null ? context.getTimeout() : 60000;
        return Math.max(timeout, 120000);
    }

    /**
     * 构造基础结果。
     *
     * @param context 作图上下文
     * @return 基础结果
     */
    protected DedicatedAiImageResult buildBaseResult(DedicatedAiImageContext context) {
        DedicatedAiImageResult result = new DedicatedAiImageResult();
        result.setPrompt(context.getOriginalPrompt());
        result.setModel(context.getModel());
        result.setSize(context.getSize());
        result.setCount(context.getCount());
        result.setAdapterName(getName());
        return result;
    }

    /**
     * 校验供应商错误响应。
     *
     * @param result 供应商响应
     */
    protected void assertNoProviderError(JSONObject result) {
        if (result == null) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(), "图片生成接口返回为空");
        }
        if (result.containsKey("error")) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片生成失败：" + readErrorMessage(result));
        }
    }

    /**
     * 从 JSON 节点提取多张图片。
     *
     * @param result 结果对象
     * @param value JSON 节点
     */
    protected void fillImagesFromJson(DedicatedAiImageResult result, Object value) {
        if (value == null || reachedImageLimit(result)) {
            return;
        }
        if (value instanceof JSONObject) {
            fillImagesFromObject(result, (JSONObject) value);
            return;
        }
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size() && !reachedImageLimit(result); i++) {
                fillImagesFromJson(result, array.get(i));
            }
            return;
        }
        fillImagesFromText(result, String.valueOf(value));
    }

    /**
     * 从文本提取多张图片。
     *
     * @param result 结果对象
     * @param text 文本内容
     */
    protected void fillImagesFromText(DedicatedAiImageResult result, String text) {
        if (!StringUtils.hasText(text) || reachedImageLimit(result)) {
            return;
        }
        result.setRawText(text.trim());

        Matcher dataMatcher = DATA_IMAGE_PATTERN.matcher(text);
        while (dataMatcher.find() && !reachedImageLimit(result)) {
            addBase64Image(result, dataMatcher.group(2), "image/" + dataMatcher.group(1), null);
        }

        Matcher markdownMatcher = MARKDOWN_IMAGE_PATTERN.matcher(text);
        while (markdownMatcher.find() && !reachedImageLimit(result)) {
            addUrlImage(result, markdownMatcher.group(1).trim(), null);
        }

        Matcher urlMatcher = HTTP_IMAGE_URL_PATTERN.matcher(text);
        while (urlMatcher.find() && !reachedImageLimit(result)) {
            addUrlImage(result, urlMatcher.group().trim(), null);
        }
    }

    /**
     * 是否已有图片。
     *
     * @param result 结果对象
     * @return 是否已有图片
     */
    protected boolean hasImages(DedicatedAiImageResult result) {
        return result.getImages() != null && !result.getImages().isEmpty();
    }

    /**
     * 响应摘要。
     *
     * @param response 原始响应
     * @return 摘要
     */
    protected String abbreviate(String response) {
        if (!StringUtils.hasText(response)) {
            return "空响应";
        }
        String text = response.replaceAll("\\s+", " ").trim();
        return text.length() <= MAX_RESPONSE_SUMMARY_LENGTH
                ? text
                : text.substring(0, MAX_RESPONSE_SUMMARY_LENGTH) + "...";
    }

    /**
     * 从对象中读取第一个字符串字段。
     *
     * @param object JSON 对象
     * @param keys 字段名
     * @return 字符串值
     */
    protected String firstText(JSONObject object, String... keys) {
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

    private void fillImagesFromObject(DedicatedAiImageResult result, JSONObject object) {
        String revisedPrompt = firstText(object, "revised_prompt", "revisedPrompt");
        String url = firstText(object, "url", "image_url", "imageUrl", "output_url", "origin_image_url");
        if (StringUtils.hasText(url)) {
            addUrlImage(result, url, revisedPrompt);
        }
        String base64 = firstText(object, "b64_json", "base64", "image_base64", "imageBase64");
        if (StringUtils.hasText(base64)) {
            addBase64Image(result, base64, resolveMimeType(base64), revisedPrompt);
        }
        Object imageUrlObject = object.get("image_url");
        if (imageUrlObject instanceof JSONObject) {
            fillImagesFromJson(result, imageUrlObject);
        }
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            if (reachedImageLimit(result)) {
                return;
            }
            fillImagesFromJson(result, entry.getValue());
        }
    }

    private void addUrlImage(DedicatedAiImageResult result, String url, String revisedPrompt) {
        if (!StringUtils.hasText(url) || reachedImageLimit(result) || containsImage(result, url, null)) {
            return;
        }
        DedicatedAiImageItem item = new DedicatedAiImageItem();
        item.setIndex(result.getImages().size() + 1);
        item.setUrl(url.trim());
        item.setMimeType(resolveMimeType(url));
        item.setRevisedPrompt(revisedPrompt);
        result.getImages().add(item);
    }

    private void addBase64Image(DedicatedAiImageResult result, String base64, String mimeType, String revisedPrompt) {
        if (!StringUtils.hasText(base64) || reachedImageLimit(result)) {
            return;
        }
        String normalizedBase64 = stripDataImagePrefix(base64);
        if (containsImage(result, null, normalizedBase64)) {
            return;
        }
        DedicatedAiImageItem item = new DedicatedAiImageItem();
        item.setIndex(result.getImages().size() + 1);
        item.setBase64(normalizedBase64);
        item.setMimeType(StringUtils.hasText(mimeType) ? mimeType : "image/png");
        item.setRevisedPrompt(revisedPrompt);
        result.getImages().add(item);
    }

    private boolean containsImage(DedicatedAiImageResult result, String url, String base64) {
        for (DedicatedAiImageItem item : result.getImages()) {
            if (StringUtils.hasText(url) && url.equals(item.getUrl())) {
                return true;
            }
            if (StringUtils.hasText(base64) && base64.equals(item.getBase64())) {
                return true;
            }
        }
        return false;
    }

    private boolean reachedImageLimit(DedicatedAiImageResult result) {
        int limit = result.getCount() == null ? 1 : result.getCount();
        return result.getImages().size() >= limit;
    }

    private String readErrorMessage(JSONObject result) {
        Object error = result.get("error");
        if (error instanceof JSONObject) {
            String message = ((JSONObject) error).getString("message");
            return StringUtils.hasText(message) ? message : error.toString();
        }
        return error == null ? "未知错误" : String.valueOf(error);
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

    private String resolveMimeType(String value) {
        if (!StringUtils.hasText(value)) {
            return "image/png";
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.startsWith("data:image/")) {
            int semicolonIndex = normalized.indexOf(';');
            return semicolonIndex > -1 ? normalized.substring(5, semicolonIndex) : "image/png";
        }
        int queryIndex = normalized.indexOf('?');
        if (queryIndex > -1) {
            normalized = normalized.substring(0, queryIndex);
        }
        if (normalized.endsWith(".jpg") || normalized.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (normalized.endsWith(".webp")) {
            return "image/webp";
        }
        if (normalized.endsWith(".gif")) {
            return "image/gif";
        }
        return "image/png";
    }
}
