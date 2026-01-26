package com.base.system.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 行政区划级联选择节点DTO
 */
@Data
public class RegionCascadeNode {

    /**
     * 值（区划代码）
     */
    private String value;

    /**
     * 标签（区划名称）
     */
    private String label;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 是否为叶子节点
     */
    private Boolean leaf;

    /**
     * 子节点列表
     */
    private List<RegionCascadeNode> children = new ArrayList<>();
}
