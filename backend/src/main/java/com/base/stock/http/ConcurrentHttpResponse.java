package com.base.stock.http;

import lombok.Builder;
import lombok.Data;

/**
 * 并发HTTP响应封装类
 *
 * @author base
 */
@Data
@Builder
public class ConcurrentHttpResponse {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应内容
     */
    private String body;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 业务标识（从请求中传递）
     */
    private String bizId;

    /**
     * 是否为Token错误
     */
    private boolean tokenError;

    /**
     * 创建成功响应
     *
     * @param body  响应内容
     * @param bizId 业务标识
     * @return 响应对象
     */
    public static ConcurrentHttpResponse success(String body, String bizId) {
        return ConcurrentHttpResponse.builder()
                .success(true)
                .body(body)
                .bizId(bizId)
                .build();
    }

    /**
     * 创建失败响应
     *
     * @param errorMessage 错误信息
     * @param bizId        业务标识
     * @return 响应对象
     */
    public static ConcurrentHttpResponse fail(String errorMessage, String bizId) {
        return ConcurrentHttpResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .bizId(bizId)
                .build();
    }

    /**
     * 创建Token错误响应
     *
     * @param errorMessage 错误信息
     * @param bizId        业务标识
     * @return 响应对象
     */
    public static ConcurrentHttpResponse tokenError(String errorMessage, String bizId) {
        return ConcurrentHttpResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .bizId(bizId)
                .tokenError(true)
                .build();
    }
}
