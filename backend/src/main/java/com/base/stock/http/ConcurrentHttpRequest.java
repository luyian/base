package com.base.stock.http;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 并发HTTP请求封装类
 *
 * @author base
 */
@Data
@Builder
public class ConcurrentHttpRequest {

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求方法（GET/POST）
     */
    @Builder.Default
    private String method = "GET";

    /**
     * 请求头（不包含Token，Token由执行器自动添加）
     */
    private Map<String, String> headers;

    /**
     * 请求体（POST请求使用）
     */
    private Object body;

    /**
     * 超时时间（毫秒）
     */
    @Builder.Default
    private int timeout = 30000;

    /**
     * 业务标识（用于失败记录，如股票代码）
     */
    private String bizId;

    /**
     * 创建GET请求
     *
     * @param url 请求URL
     * @return 请求对象
     */
    public static ConcurrentHttpRequest get(String url) {
        return ConcurrentHttpRequest.builder()
                .url(url)
                .method("GET")
                .build();
    }

    /**
     * 创建GET请求（带业务标识）
     *
     * @param url   请求URL
     * @param bizId 业务标识
     * @return 请求对象
     */
    public static ConcurrentHttpRequest get(String url, String bizId) {
        return ConcurrentHttpRequest.builder()
                .url(url)
                .method("GET")
                .bizId(bizId)
                .build();
    }

    /**
     * 创建POST请求
     *
     * @param url  请求URL
     * @param body 请求体
     * @return 请求对象
     */
    public static ConcurrentHttpRequest post(String url, Object body) {
        return ConcurrentHttpRequest.builder()
                .url(url)
                .method("POST")
                .body(body)
                .build();
    }
}
