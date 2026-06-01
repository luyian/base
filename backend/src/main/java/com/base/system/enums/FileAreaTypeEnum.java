package com.base.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 文件关联业务领域类型枚举
 *
 * <p>用于通用文件关联表 file_link_obj 的 area_type 字段，区分文件归属的业务领域。</p>
 *
 * @author base
 */
@Getter
public enum FileAreaTypeEnum {

    /**
     * 知识库
     */
    KNOWLEDGE("knowledge", "知识库");

    /**
     * 领域编码（存储到数据库）
     */
    @EnumValue
    private final String code;

    /**
     * 领域描述（用于显示）
     */
    @JsonValue
    private final String desc;

    FileAreaTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
