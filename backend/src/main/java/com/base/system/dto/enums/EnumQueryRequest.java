package com.base.system.dto.enums;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 枚举查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnumQueryRequest extends BasePageRequest {

    /**
     * 枚举类型
     */
    private String enumType;

    /**
     * 枚举编码
     */
    private String enumCode;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;
}
