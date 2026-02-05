package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 枚举实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_enum")
public class Enum extends BaseEntity {

    /**
     * 枚举ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 枚举类型
     */
    private String enumType;

    /**
     * 枚举类型中文描述
     */
    private String typeDesc;

    /**
     * 枚举编码
     */
    private String enumCode;

    /**
     * 枚举值
     */
    private String enumValue;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;
}
