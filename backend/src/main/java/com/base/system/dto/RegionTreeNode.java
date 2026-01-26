package com.base.system.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 行政区划树节点DTO
 */
@Data
public class RegionTreeNode {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 行政区划代码
     */
    private String regionCode;

    /**
     * 行政区划名称
     */
    private String regionName;

    /**
     * 层级（1-省，2-市，3-区，4-街道）
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 全称
     */
    private String fullName;

    /**
     * 子节点列表
     */
    private List<RegionTreeNode> children = new ArrayList<>();

    /**
     * 是否有子节点
     */
    private Boolean hasChildren;
}
