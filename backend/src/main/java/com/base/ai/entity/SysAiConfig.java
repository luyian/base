package com.base.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 大模型配置实体（支持多条，is_active 标记当前生效）
 *
 * @author base
 * @since 2026-03-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ai_config")
public class SysAiConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 配置名称 */
    private String configName;
    /** API 基础地址 */
    private String baseUrl;
    /** API Key（列表/详情可脱敏） */
    @JsonIgnore
    private String apiKey;
    /** 模型名称 */
    private String model;
    /** 超时(ms) */
    private Integer timeout;
    /** 重试次数 */
    private Integer retry;
    /** 消息最大长度 */
    private Integer maxMessageLength;
    /** 上下文最大长度 */
    private Integer maxContextLength;
    /** 是否当前生效 0否 1是 */
    private Integer isActive;
    /** 状态 0禁用 1启用 */
    private Integer status;
    /** 排序 */
    private Integer sortOrder;
    /** 备注 */
    private String remark;
}
