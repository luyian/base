package com.base.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 文件关联类型枚举
 *
 * <p>用于通用文件关联表 file_link_obj 的 link_type 字段，区分文件在所属业务领域下的具体用途。</p>
 *
 * @author base
 */
@Getter
public enum FileLinkTypeEnum {

    /**
     * 文档附件
     */
    DOC_ATTACHMENT("doc_attachment", "文档附件");

    /**
     * 关联类型编码（存储到数据库）
     */
    @EnumValue
    private final String code;

    /**
     * 关联类型描述（用于显示）
     */
    @JsonValue
    private final String desc;

    FileLinkTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
