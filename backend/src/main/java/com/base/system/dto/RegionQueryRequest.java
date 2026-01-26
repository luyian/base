package com.base.system.dto;

import lombok.Data;

/**
 * 行政区划查询请求DTO
 */
@Data
public class RegionQueryRequest {

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 行政区划名称（模糊查询）
     */
    private String regionName;

    /**
     * 行政区划代码
     */
    private String regionCode;

    /**
     * 层级（1-省，2-市，3-区，4-街道）
     */
    private Integer level;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 拼音首字母
     */
    private String pinyinPrefix;
}
