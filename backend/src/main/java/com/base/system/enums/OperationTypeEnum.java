package com.base.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author base
 */
@Getter
public enum OperationTypeEnum {

    /**
     * 新增
     */
    INSERT(1, "新增"),

    /**
     * 修改
     */
    UPDATE(2, "修改"),

    /**
     * 删除
     */
    DELETE(3, "删除"),

    /**
     * 查询
     */
    SELECT(4, "查询"),

    /**
     * 导出
     */
    EXPORT(5, "导出"),

    /**
     * 导入
     */
    IMPORT(6, "导入"),

    /**
     * 其他
     */
    OTHER(7, "其他");

    /**
     * 类型值（存储到数据库）
     */
    @EnumValue
    private final Integer value;

    /**
     * 类型描述（用于显示）
     */
    @JsonValue
    private final String description;

    OperationTypeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 枚举对象
     */
    public static OperationTypeEnum valueOf(Integer value) {
        if (value == null) {
            return null;
        }
        for (OperationTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
