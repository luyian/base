package com.base.ai.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.base.ai.dto.ChatRequest;
import com.base.ai.dto.ChatResponse;
import com.base.ai.service.AiService;
import com.base.common.exception.BusinessException;
import com.base.common.annotation.Log;
import com.base.common.result.Result;
import com.base.common.result.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;

/**
 * AI 助手接口（Dashboard 对话）
 *
 * @author base
 * @since 2026-03-16
 */
@Tag(name = "AI 助手")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private static final int IMAGE_PROXY_TIMEOUT = 60000;

    private static final int MAX_IMAGE_BYTES = 20 * 1024 * 1024;

    private final AiService aiService;

    @Operation(summary = "对话（支持技能调用）")
    @PostMapping("/chat")
    @Log(title = "AI 助手", content = "Dashboard AI 对话", type = "OTHER")
    public Result<ChatResponse> chat(@Validated @RequestBody ChatRequest request) {
        ChatResponse response;
        if (Boolean.TRUE.equals(request.getEnableSkills())) {
            response = aiService.chatWithSkills(request);
        } else {
            response = aiService.chat(request);
        }
        return Result.success(response);
    }

    @Operation(summary = "代理查看 AI 生成图片")
    @GetMapping("/image-proxy")
    public void proxyImage(@RequestParam("url") String imageUrl, HttpServletResponse response) throws IOException {
        String normalizedImageUrl = validateImageUrl(imageUrl);
        HttpResponse remoteResponse = HttpRequest.get(normalizedImageUrl)
                .timeout(IMAGE_PROXY_TIMEOUT)
                .execute();
        if (!remoteResponse.isOk()) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "图片加载失败，状态码：" + remoteResponse.getStatus());
        }
        byte[] body = remoteResponse.bodyBytes();
        if (body == null || body.length == 0) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(), "图片内容为空");
        }
        if (body.length > MAX_IMAGE_BYTES) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "图片文件过大，暂不支持预览");
        }
        response.setContentType(resolveContentType(remoteResponse.header("Content-Type")));
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
    }

    private String validateImageUrl(String imageUrl) {
        return validateImageUrl(imageUrl, 0);
    }

    private String validateImageUrl(String imageUrl, int depth) {
        if (!StringUtils.hasText(imageUrl)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "图片地址不能为空");
        }
        if (depth > 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "图片地址代理层级过深");
        }
        String normalizedImageUrl = imageUrl.trim();
        try {
            URL url = new URL(normalizedImageUrl);
            String protocol = url.getProtocol();
            if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "仅支持 http/https 图片地址");
            }
            if (isBlockedHost(url.getHost())) {
                String proxyTargetUrl = extractProxyTargetUrl(url);
                if (StringUtils.hasText(proxyTargetUrl)) {
                    return validateImageUrl(proxyTargetUrl, depth + 1);
                }
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不允许代理访问本机或内网图片地址");
            }
            return normalizedImageUrl;
        } catch (MalformedURLException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "图片地址格式不正确");
        }
    }

    private String extractProxyTargetUrl(URL url) {
        String path = url.getPath();
        if (!StringUtils.hasText(path) || !path.endsWith("/ai/image-proxy")) {
            return null;
        }
        String query = url.getQuery();
        if (!StringUtils.hasText(query)) {
            return null;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int index = pair.indexOf('=');
            if (index <= 0) {
                continue;
            }
            String key = decodeQueryComponent(pair.substring(0, index));
            if ("url".equals(key)) {
                return decodeQueryComponent(pair.substring(index + 1));
            }
        }
        return null;
    }

    private String decodeQueryComponent(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private boolean isBlockedHost(String host) {
        if (!StringUtils.hasText(host)) {
            return true;
        }
        String lowerHost = host.toLowerCase(Locale.ROOT);
        if ("localhost".equals(lowerHost) || "::1".equals(lowerHost) || lowerHost.startsWith("127.")) {
            return true;
        }
        if (lowerHost.startsWith("10.") || lowerHost.startsWith("192.168.")) {
            return true;
        }
        if (!lowerHost.startsWith("172.")) {
            return false;
        }
        String[] parts = lowerHost.split("\\.");
        if (parts.length < 2) {
            return false;
        }
        try {
            int second = Integer.parseInt(parts[1]);
            return second >= 16 && second <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String resolveContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return MediaType.IMAGE_PNG_VALUE;
        }
        String lowerContentType = contentType.toLowerCase(Locale.ROOT);
        if (lowerContentType.contains("jpeg") || lowerContentType.contains("jpg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        }
        if (lowerContentType.contains("webp")) {
            return "image/webp";
        }
        if (lowerContentType.contains("gif")) {
            return MediaType.IMAGE_GIF_VALUE;
        }
        return MediaType.IMAGE_PNG_VALUE;
    }
}
