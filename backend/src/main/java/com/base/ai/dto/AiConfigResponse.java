package com.base.ai.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 大模型配置响应（用于系统管理-大模型配置页）
 *
 * @author base
 * @since 2026-03-17
 */
@Data
public class AiConfigResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private String baseUrl;
    private String apiKey;
    private String model;
    private Integer timeout;
    private Integer retry;
    private Integer maxMessageLength;
    private Integer maxContextLength;
}
