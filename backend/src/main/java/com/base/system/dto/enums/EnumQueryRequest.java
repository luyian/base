package com.base.system.dto.enums;

import lombok.Data;

/**
 * 枚举查询请求参数
 */
@Data
public class EnumQueryRequest {

    /**
     * 枚举类型
     */
    private String enumType;

    /**
     * 枚举标签
     */
    private String enumLabel;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页显示数量
     */
    private Integer size = 10;
}
