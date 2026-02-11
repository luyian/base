package com.base.common.feishu.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 飞书接收者 ID 类型枚举
 *
 * @author base
 * @since 2026-02-11
 */
@Getter
@AllArgsConstructor
public enum FeishuReceiveIdTypeEnum {

    /**
     * 飞书 open_id
     */
    OPEN_ID("open_id", "飞书 open_id"),

    /**
     * 飞书 user_id
     */
    USER_ID("user_id", "飞书 user_id"),

    /**
     * 飞书 union_id
     */
    UNION_ID("union_id", "飞书 union_id"),

    /**
     * 飞书邮箱
     */
    EMAIL("email", "飞书邮箱");

    /**
     * ID 类型编码
     */
    private final String code;

    /**
     * ID 类型描述
     */
    private final String desc;
}
