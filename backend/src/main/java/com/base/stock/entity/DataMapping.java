package com.base.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据映射配置实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stk_data_mapping")
@ApiModel(value = "DataMapping", description = "数据映射配置实体")
public class DataMapping extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 映射编码（唯一标识）
     */
    @ApiModelProperty("映射编码")
    private String mappingCode;

    /**
     * 映射名称
     */
    @ApiModelProperty("映射名称")
    private String mappingName;

    /**
     * 源数据类型
     */
    @ApiModelProperty("源数据类型")
    private String sourceType;

    /**
     * 目标表名
     */
    @ApiModelProperty("目标表名")
    private String targetTable;

    /**
     * 字段映射配置（JSON格式）
     */
    @ApiModelProperty("字段映射配置（JSON格式）")
    private String fieldMapping;

    /**
     * 转换脚本（可选）
     */
    @ApiModelProperty("转换脚本")
    private String transformScript;

    /**
     * 状态（0-禁用, 1-启用）
     */
    @ApiModelProperty("状态（0-禁用, 1-启用）")
    private Integer status;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
}
