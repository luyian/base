package com.base.system.dto.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 枚举响应结果
 */
@Data
public class EnumResponse {

    /**
     * 枚举ID
     */
    private Long id;

    /**
     * 枚举类型
     */
    private String enumType;

    /**
     * 枚举值
     */
    private String enumValue;

    /**
     * 枚举标签
     */
    private String enumLabel;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
