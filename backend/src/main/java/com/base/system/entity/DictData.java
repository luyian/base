package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体类
 *
 * @author base
 * @since 2026-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class DictData extends BaseEntity {

    /**
     * 字典类型编码
     */
    private String dictType;

    /**
     * 字典标签（显示值）
     */
    private String dictLabel;

    /**
     * 字典键值（编码）
     */
    private String dictValue;

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
}
