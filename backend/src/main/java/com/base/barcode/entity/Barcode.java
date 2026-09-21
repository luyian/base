package com.base.barcode.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用条码/二维码记录实体类
 *
 * @author base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_barcode")
@ApiModel(value = "Barcode", description = "通用条码/二维码记录实体")
public class Barcode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 条码/二维码内容（全局唯一）
     */
    @ApiModelProperty("条码/二维码内容")
    private String code;

    /**
     * 类型（1条码 2二维码）
     */
    @ApiModelProperty("类型（1条码 2二维码）")
    private Integer type;

    /**
     * 来源（1自生成 2图片识别录入）
     */
    @ApiModelProperty("来源（1自生成 2图片识别录入）")
    private Integer source;

    /**
     * 业务类型（如 PRODUCT）
     */
    @ApiModelProperty("业务类型")
    private String bizType;

    /**
     * 业务对象ID（如商品ID）
     */
    @ApiModelProperty("业务对象ID")
    private Long bizId;

    /**
     * 所属用户ID
     */
    @ApiModelProperty("所属用户ID")
    private Long userId;

    /**
     * 关联文件ID（sys_file，生成图片归档）
     */
    @ApiModelProperty("关联文件ID")
    private Long fileId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
}