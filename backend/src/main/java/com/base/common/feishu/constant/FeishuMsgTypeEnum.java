package com.base.common.feishu.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 飞书消息类型枚举
 *
 * @author base
 * @since 2026-02-11
 */
@Getter
@AllArgsConstructor
public enum FeishuMsgTypeEnum {

    /**
     * 文本消息
     */
    TEXT("text", "文本消息"),

    /**
     * 图片消息
     */
    IMAGE("image", "图片消息"),

    /**
     * 文件消息
     */
    FILE("file", "文件消息");

    /**
     * 消息类型编码
     */
    private final String code;

    /**
     * 消息类型描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     *
     * @param code 消息类型编码
     * @return 枚举值，未匹配返回 null
     */
    public static FeishuMsgTypeEnum getByCode(String code) {
        for (FeishuMsgTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
