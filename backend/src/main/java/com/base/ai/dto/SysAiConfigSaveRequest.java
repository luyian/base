package com.base.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 大模型配置保存请求
 *
 * @author base
 * @since 2026-03-17
 */
@Data
public class SysAiConfigSaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "配置名称不能为空")
    private String configName;

    @NotBlank(message = "API 基础地址不能为空")
    private String baseUrl;

    private String apiKey;

    private String model;

    @NotNull(message = "超时时间不能为空")
    private Integer timeout = 30000;

    @NotNull(message = "重试次数不能为空")
    private Integer retry = 2;

    @NotNull(message = "消息最大长度不能为空")
    private Integer maxMessageLength = 2000;

    @NotNull(message = "上下文最大长度不能为空")
    private Integer maxContextLength = 5000;

    private Integer status = 1;
    private Integer sortOrder = 0;
    private String remark;
}
