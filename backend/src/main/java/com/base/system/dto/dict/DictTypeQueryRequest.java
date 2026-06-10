package com.base.system.dto.dict;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型查询请求参数
 *
 * @author base
 * @since 2026-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeQueryRequest extends BasePageRequest {

    /**
     * 字典类型编码
     */
    private String dictType;

    /**
     * 字典类型名称
     */
    private String dictName;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;
}
