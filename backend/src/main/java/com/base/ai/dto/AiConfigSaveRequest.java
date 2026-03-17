package com.base.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 大模型配置保存请求
 *
 * @author base
 * @since 2026-03-17
 */
@Data
public class AiConfigSaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "启用状态不能为空")
    private Boolean enabled;
    private String baseUrl;
    private String apiKey;
    private String model;
    private Integer timeout;
    private Integer retry;
    private Integer maxMessageLength;
    private Integer maxContextLength;
}
