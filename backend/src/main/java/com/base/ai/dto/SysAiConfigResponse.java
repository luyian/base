package com.base.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 大模型配置列表/详情响应（apiKey 编辑时返回原文，列表可脱敏）
 *
 * @author base
 * @since 2026-03-17
 */
@Data
public class SysAiConfigResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String configName;
    private String baseUrl;
    private String apiKey;       // 列表可脱敏为 ***，编辑时需原文
    private String model;
    private Integer timeout;
    private Integer retry;
    private Integer maxMessageLength;
    private Integer maxContextLength;
    private Integer isActive;
    private Integer status;
    private Integer sortOrder;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
