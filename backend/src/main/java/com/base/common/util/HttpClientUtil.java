package com.base.common.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Supplier;

/**
 * HTTP 请求工具类
 * 封装常用的 HTTP 请求方法，支持重试机制
 *
 * @author base
 */
@Slf4j
public class HttpClientUtil {

    /**
     * 默认超时时间（毫秒）
     */
    private static final int DEFAULT_TIMEOUT = 30000;

    /**
     * 默认重试次数
     */
    private static final int DEFAULT_RETRY = 3;

    /**
     * 重试间隔时间（毫秒）
     */
    private static final int RETRY_INTERVAL = 1000;

    /**
     * GET 请求，返回字符串
     *
     * @param url     请求地址
     * @param headers 请求头
     * @return 响应字符串
     */
    public static String get(String url, Map<String, String> headers) {
        return get(url, headers, DEFAULT_TIMEOUT);
    }

    /**
     * GET 请求，返回字符串
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param timeout 超时时间（毫秒）
     * @return 响应字符串
     */
    public static String get(String url, Map<String, String> headers, int timeout) {
        try {
            HttpRequest request = HttpUtil.createGet(url)
                    .timeout(timeout);
            if (headers != null && !headers.isEmpty()) {
                request.addHeaders(headers);
            }
            HttpResponse response = request.execute();
            if (response.isOk()) {
                return response.body();
            } else {
                log.error("HTTP GET 请求失败，URL: {}，状态码: {}，响应: {}", url, response.getStatus(), response.body());
                throw new RuntimeException("HTTP GET 请求失败，状态码: " + response.getStatus());
            }
        } catch (Exception e) {
            log.error("HTTP GET 请求异常，URL: {}", url, e);
            throw new RuntimeException("HTTP GET 请求异常: " + e.getMessage(), e);
        }
    }

    /**
     * GET 请求，返回指定类型对象
     *
     * @param url          请求地址
     * @param headers      请求头
     * @param responseType 响应类型
     * @param <T>          泛型类型
     * @return 响应对象
     */
    public static <T> T get(String url, Map<String, String> headers, Class<T> responseType) {
        return get(url, headers, DEFAULT_TIMEOUT, responseType);
    }

    /**
     * GET 请求，返回指定类型对象
     *
     * @param url          请求地址
     * @param headers      请求头
     * @param timeout      超时时间（毫秒）
     * @param responseType 响应类型
     * @param <T>          泛型类型
     * @return 响应对象
     */
    public static <T> T get(String url, Map<String, String> headers, int timeout, Class<T> responseType) {
        String response = get(url, headers, timeout);
        return JSON.parseObject(response, responseType);
    }

    /**
     * POST 请求，返回字符串
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 请求头
     * @return 响应字符串
     */
    public static String post(String url, Object body, Map<String, String> headers) {
        return post(url, body, headers, DEFAULT_TIMEOUT);
    }

    /**
     * POST 请求，返回字符串
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 请求头
     * @param timeout 超时时间（毫秒）
     * @return 响应字符串
     */
    public static String post(String url, Object body, Map<String, String> headers, int timeout) {
        try {
            HttpRequest request = HttpUtil.createPost(url)
                    .timeout(timeout)
                    .contentType("application/json");
            if (headers != null && !headers.isEmpty()) {
                request.addHeaders(headers);
            }
            if (body != null) {
                String jsonBody = body instanceof String ? (String) body : JSON.toJSONString(body);
                request.body(jsonBody);
            }
            HttpResponse response = request.execute();
            if (response.isOk()) {
                return response.body();
            } else {
                log.error("HTTP POST 请求失败，URL: {}，状态码: {}，响应: {}", url, response.getStatus(), response.body());
                throw new RuntimeException("HTTP POST 请求失败，状态码: " + response.getStatus());
            }
        } catch (Exception e) {
            log.error("HTTP POST 请求异常，URL: {}", url, e);
            throw new RuntimeException("HTTP POST 请求异常: " + e.getMessage(), e);
        }
    }

    /**
     * POST 请求，返回指定类型对象
     *
     * @param url          请求地址
     * @param body         请求体
     * @param headers      请求头
     * @param responseType 响应类型
     * @param <T>          泛型类型
     * @return 响应对象
     */
    public static <T> T post(String url, Object body, Map<String, String> headers, Class<T> responseType) {
        return post(url, body, headers, DEFAULT_TIMEOUT, responseType);
    }

    /**
     * POST 请求，返回指定类型对象
     *
     * @param url          请求地址
     * @param body         请求体
     * @param headers      请求头
     * @param timeout      超时时间（毫秒）
     * @param responseType 响应类型
     * @param <T>          泛型类型
     * @return 响应对象
     */
    public static <T> T post(String url, Object body, Map<String, String> headers, int timeout, Class<T> responseType) {
        String response = post(url, body, headers, timeout);
        return JSON.parseObject(response, responseType);
    }

    /**
     * 带重试机制的请求执行
     *
     * @param request  请求执行器
     * @param maxRetry 最大重试次数
     * @param <T>      泛型类型
     * @return 响应结果
     */
    public static <T> T executeWithRetry(Supplier<T> request, int maxRetry) {
        if (maxRetry <= 0) {
            maxRetry = DEFAULT_RETRY;
        }
        Exception lastException = null;
        for (int i = 0; i < maxRetry; i++) {
            try {
                return request.get();
            } catch (Exception e) {
                lastException = e;
                log.warn("请求失败，第 {} 次重试，异常: {}", i + 1, e.getMessage());
                if (i < maxRetry - 1) {
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("重试被中断", ie);
                    }
                }
            }
        }
        log.error("请求失败，已达到最大重试次数: {}", maxRetry);
        throw new RuntimeException("请求失败，已达到最大重试次数: " + maxRetry, lastException);
    }

    /**
     * 带重试机制的 GET 请求
     *
     * @param url      请求地址
     * @param headers  请求头
     * @param maxRetry 最大重试次数
     * @return 响应字符串
     */
    public static String getWithRetry(String url, Map<String, String> headers, int maxRetry) {
        return executeWithRetry(() -> get(url, headers), maxRetry);
    }

    /**
     * 带重试机制的 GET 请求
     *
     * @param url          请求地址
     * @param headers      请求头
     * @param maxRetry     最大重试次数
     * @param responseType 响应类型
     * @param <T>          泛型类型
     * @return 响应对象
     */
    public static <T> T getWithRetry(String url, Map<String, String> headers, int maxRetry, Class<T> responseType) {
        return executeWithRetry(() -> get(url, headers, responseType), maxRetry);
    }

    /**
     * 带重试机制的 POST 请求
     *
     * @param url      请求地址
     * @param body     请求体
     * @param headers  请求头
     * @param maxRetry 最大重试次数
     * @return 响应字符串
     */
    public static String postWithRetry(String url, Object body, Map<String, String> headers, int maxRetry) {
        return executeWithRetry(() -> post(url, body, headers), maxRetry);
    }

    /**
     * 带重试机制的 POST 请求
     *
     * @param url          请求地址
     * @param body         请求体
     * @param headers      请求头
     * @param maxRetry     最大重试次数
     * @param responseType 响应类型
     * @param <T>          泛型类型
     * @return 响应对象
     */
    public static <T> T postWithRetry(String url, Object body, Map<String, String> headers, int maxRetry, Class<T> responseType) {
        return executeWithRetry(() -> post(url, body, headers, responseType), maxRetry);
    }
}
