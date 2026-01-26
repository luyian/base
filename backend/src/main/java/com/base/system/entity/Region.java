package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.base.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 行政区划实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_region")
public class Region extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父级ID（0表示顶级）
     */
    private Long parentId;

    /**
     * 行政区划代码（统计用区划代码）
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
     * 全称（如：广东省广州市天河区）
     */
    private String fullName;

    /**
     * 简称
     */
    private String shortName;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 拼音首字母
     */
    private String pinyinPrefix;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;
}
